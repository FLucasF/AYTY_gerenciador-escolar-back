package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.dto.material.MaterialRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.material.MaterialResponse;
import br.com.ufpb.GerenciadorEscolar.model.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.MuralMapper;
import br.com.ufpb.GerenciadorEscolar.model.entity.Mural;
import br.com.ufpb.GerenciadorEscolar.model.entity.Professor;
import br.com.ufpb.GerenciadorEscolar.model.entity.TipoArquivo;
import br.com.ufpb.GerenciadorEscolar.model.entity.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.MuralRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Slf4j
public class MuralServiceImpl implements MuralServiceInterface {

    private final MuralRepository muralRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final MuralMapper muralMapper;
    private final MaterialServiceInterface materialService;

    @Autowired
    public MuralServiceImpl(MuralRepository muralRepository,
                            TurmaRepository turmaRepository,
                            ProfessorRepository professorRepository,
                            MuralMapper muralMapper,
                            MaterialServiceInterface materialService) {
        this.muralRepository = muralRepository;
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
        this.muralMapper = muralMapper;
        this.materialService = materialService;
    }

    @Override
    public MuralResponse criarPostagem(MuralRequest muralRequest, MultipartFile imagem) {
        log.info("Criando nova postagem no mural para Turma ID: {} e Professor ID: {}",
                muralRequest.turmaId(), muralRequest.professorId());

        // Recupera a Turma e o Professor
        Turma turma = turmaRepository.findByIdAndAtivoTrue(muralRequest.turmaId())
                .orElseThrow(() -> new TurmaNaoEncontradaException("Turma não encontrada"));
        log.debug("Turma encontrada: ID {}", turma.getId());

        Professor professor = professorRepository.findByIdAndAtivoTrue(muralRequest.professorId())
                .orElseThrow(() -> new ProfessorNaoEncontradoException("Professor não encontrado"));
        log.debug("Professor encontrado: ID {}", professor.getId());

        // Cria o mural inicialmente sem imagem associada para obter um ID para associação
        Mural mural = muralMapper.toEntity(muralRequest);
        mural.setTurma(turma);
        mural.setProfessor(professor);
        mural.setDataCriacao(LocalDateTime.now());
        mural.setAtivo(true);
        mural = muralRepository.save(mural);
        log.info("Mural criado inicialmente com ID: {}", mural.getId());

        // Verifica se o campo "imagem" foi enviado
        if (imagem != null) {
            log.debug("Campo 'imagem' recebido. Nome: {}, Tamanho: {} bytes",
                    imagem.getOriginalFilename(), imagem.getSize());
        } else {
            log.debug("Campo 'imagem' não foi recebido (null).");
        }

        // Processa o upload da imagem, se enviada e não vazia
        if (imagem != null && !imagem.isEmpty()) {
            try {
                MaterialRequest materialRequest = new MaterialRequest(
                        "mural",  // Valor fixo indicando a origem
                        imagem.getOriginalFilename(),
                        TipoArquivo.IMAGEM,
                        muralRequest.turmaId(),
                        muralRequest.professorId()
                );
                log.info("Iniciando upload da imagem: {}", imagem.getOriginalFilename());
                MaterialResponse materialResponse = materialService.salvarMaterial(materialRequest, imagem.getBytes());
                log.info("Upload da imagem concluído. Material retornado com ID: {}", materialResponse.id());

                // Associa o material (imagem) ao mural (preenchendo o campo mural_id na tabela de materiais)
                materialService.associarMaterialAoMural(materialResponse.id(), mural);
                log.info("Material ID {} associado ao mural ID {}", materialResponse.id(), mural.getId());

                // Define no mural o ID do material da imagem
                mural.setImagemId(materialResponse.id());
            } catch (Exception e) {
                log.error("Erro ao processar a imagem para o mural: {}", e.getMessage());
                throw new RuntimeException("Erro ao processar a imagem.");
            }
        } else {
            log.info("Nenhuma imagem enviada ou o arquivo está vazio.");
        }

        // Atualiza o mural com as informações finais
        mural = muralRepository.save(mural);
        log.info("Postagem no mural finalizada com ID: {}", mural.getId());

        // Monta a resposta: tenta recuperar a URL assinada para o material (imagem), se existir
        String imagemUrl = null;
        if (mural.getImagemId() != null) {
            try {
                MaterialResponse matResp = materialService.buscarMaterialPorId(mural.getImagemId());
                imagemUrl = matResp.urlArquivo();
                log.info("URL assinada recuperada para material ID {}: {}", mural.getImagemId(), imagemUrl);
            } catch (Exception e) {
                log.warn("Falha ao obter URL assinada para material ID {}: {}", mural.getImagemId(), e.getMessage());
            }
        }

        return new MuralResponse(
                mural.getId(),
                mural.getTitulo(),
                mural.getConteudo(),
                mural.getDataCriacao(),
                mural.getTurma().getId(),
                mural.getProfessor().getId(),
                imagemUrl
        );
    }

    @Override
    public MuralResponse buscarPostagemPorId(Long id) {
        log.info("🔍 Buscando postagem no mural com ID: {}", id);
        Mural mural = muralRepository.findById(id)
                .orElseThrow(() -> new PostagemNaoEncontradaException("Postagem não encontrada"));
        log.info("Postagem encontrada: ID {}", mural.getId());

        String imagemUrl = null;
        if (mural.getImagemId() != null) {
            try {
                MaterialResponse matResp = materialService.buscarMaterialPorId(mural.getImagemId());
                imagemUrl = matResp.urlArquivo();
                log.info("URL assinada recuperada para material ID {}: {}", mural.getImagemId(), imagemUrl);
            } catch (Exception e) {
                log.warn("Falha ao buscar URL assinada para material ID {}: {}", mural.getImagemId(), e.getMessage());
            }
        }

        return new MuralResponse(
                mural.getId(),
                mural.getTitulo(),
                mural.getConteudo(),
                mural.getDataCriacao(),
                mural.getTurma().getId(),
                mural.getProfessor().getId(),
                imagemUrl
        );
    }

    @Override
    public Page<MuralResponse> listarPostagensPorTurma(Long idTurma, Pageable pageable) {
        log.info("📌 Listando postagens para Turma ID: {} com paginação: {}", idTurma, pageable);
        Page<MuralResponse> responses = muralRepository.findByTurmaIdAndAtivoTrue(idTurma, pageable)
                .map(mural -> {
                    String imagemUrl = null;
                    if (mural.getImagemId() != null) {
                        try {
                            MaterialResponse matResp = materialService.buscarMaterialPorId(mural.getImagemId());
                            imagemUrl = matResp.urlArquivo();
                        } catch (Exception e) {
                            log.warn("Falha ao buscar URL assinada para material ID {}: {}", mural.getImagemId(), e.getMessage());
                        }
                    }
                    return new MuralResponse(
                            mural.getId(),
                            mural.getTitulo(),
                            mural.getConteudo(),
                            mural.getDataCriacao(),
                            mural.getTurma().getId(),
                            mural.getProfessor().getId(),
                            imagemUrl
                    );
                });
        log.info("Listagem concluída. {} postagens retornadas.", responses.getTotalElements());
        return responses;
    }

    @Override
    public void deletarPostagem(Long id) {
        log.info("🗑️ Desativando postagem no mural com ID: {}", id);
        Mural mural = muralRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new PostagemNaoEncontradaException("Postagem não encontrada"));

        if (mural.getImagemId() != null) {
            log.info("🔄 Removendo material associado à imagem no MinIO, ID: {}", mural.getImagemId());
            try {
                materialService.deletarMaterial(mural.getImagemId()); // Agora só precisa passar o ID
            } catch (Exception e) {
                log.error("❌ Erro ao excluir a mídia no MinIO: {}", e.getMessage());
            }
        }

        mural.setAtivo(false);
        muralRepository.save(mural);
        log.info("✅ Postagem desativada com sucesso. ID: {}", id);
    }

}
