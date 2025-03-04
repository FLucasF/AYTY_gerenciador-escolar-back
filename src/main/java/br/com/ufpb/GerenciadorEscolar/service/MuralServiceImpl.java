package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.MuralMapper;
import br.com.ufpb.GerenciadorEscolar.model.Mural;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.MuralRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MuralServiceImpl implements MuralServiceInterface {

    private final MuralRepository muralRepository;
    private final MuralMapper muralMapper;

    @Autowired
    public MuralServiceImpl(MuralRepository muralRepository, TurmaRepository turmaRepository, MuralMapper muralMapper) {
        this.muralRepository = muralRepository;
        this.muralMapper = muralMapper;
    }

    @Override
    public MuralResponse criarPostagem(MuralRequest muralRequest) {
        log.info("Criando postagem no mural para turma ID: {} e professor ID: {}",
                muralRequest.turmaId(), muralRequest.professorId());

        if (muralRequest.professorId() == null) {
            log.error("O campo professorId é obrigatório para publicar uma postagem.");
            throw new IllegalArgumentException("O campo professorId é obrigatório para publicar uma postagem.");
        }

        Mural mural = muralMapper.toEntity(muralRequest);

        Professor professor = new Professor();
        professor.setId(muralRequest.professorId());
        mural.setProfessor(professor);
        log.debug("Professor definido para a postagem com ID: {}", muralRequest.professorId());

        Turma turma = new Turma();
        turma.setId(muralRequest.turmaId());
        mural.setTurma(turma);
        log.debug("Turma definida para a postagem com ID: {}", muralRequest.turmaId());

        mural.setAtivo(true);

        try {
            muralRepository.save(mural);
            log.info("Postagem no mural salva com sucesso. ID da postagem: {}", mural.getId());
        } catch (Exception e) {
            log.error("Erro ao salvar a postagem no mural: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar a postagem no mural: " + e.getMessage(), e);
        }

        MuralResponse response = muralMapper.toResponse(mural);
        log.info("Retornando resposta da postagem: {}", response);
        return response;
    }

    @Override
    public Optional<MuralResponse> buscarPostagemPorId(Long id) {
        log.info("Buscando postagem no mural com ID: {}", id);
        Optional<MuralResponse> response = muralRepository.findById(id).map(muralMapper::toResponse);
        if (response.isEmpty()) {
            log.warn("Postagem no mural não encontrada para o ID: {}", id);
        }
        return response;
    }

    @Override
    public Page<MuralResponse> listarPostagensPorTurma(Long idTurma, Pageable pageable) {
        log.info("📥 Listando postagens para a turma ID: {} com paginação: {}", idTurma, pageable);

        Page<MuralResponse> responses = muralRepository
                .findByTurmaIdAndAtivoTrue(idTurma, pageable)
                .map(muralMapper::toResponse);

        log.info("✅ Total de postagens encontradas: {}", responses.getTotalElements());
        return responses;
    }

    @Override
    public void deletarPostagem(Long id) {
        log.info("Deletando postagem no mural com ID: {}", id);
        Mural mural = muralRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Postagem não encontrada para deleção com ID: {}", id);
                    return new RuntimeException("Postagem não encontrada");
                });
        mural.setAtivo(false);
        muralRepository.save(mural);
        log.info("Postagem desativada com sucesso. ID: {}", id);
    }
}
