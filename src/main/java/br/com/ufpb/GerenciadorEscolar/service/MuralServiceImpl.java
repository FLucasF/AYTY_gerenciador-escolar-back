package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.MuralMapper;
import br.com.ufpb.GerenciadorEscolar.model.Mural;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.MuralRepository;
import br.com.ufpb.GerenciadorEscolar.repository.ProfessorRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MuralServiceImpl implements MuralServiceInterface {

    private final MuralRepository muralRepository;
    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final MuralMapper muralMapper;

    @Autowired
    public MuralServiceImpl(MuralRepository muralRepository,
                            TurmaRepository turmaRepository,
                            ProfessorRepository professorRepository,
                            MuralMapper muralMapper) {
        this.muralRepository = muralRepository;
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
        this.muralMapper = muralMapper;
    }

    @Override
    public MuralResponse criarPostagem(MuralRequest muralRequest) {
        log.info("Criando postagem no mural para Turma ID: {} e Professor ID: {}",
                muralRequest.turmaId(), muralRequest.professorId());

        Turma turma = turmaRepository.findById(muralRequest.turmaId())
                .orElseThrow(() -> {
                    log.error("Turma não encontrada para ID: {}", muralRequest.turmaId());
                    return new TurmaNaoEncontradaException("Turma não encontrada");
                });

        Professor professor = professorRepository.findById(muralRequest.professorId())
                .orElseThrow(() -> {
                    log.error("Professor não encontrado para ID: {}", muralRequest.professorId());
                    return new ProfessorNaoEncontradoException("Professor não encontrado");
                });

        Mural mural = muralMapper.toEntity(muralRequest);
        mural.setTurma(turma);
        mural.setProfessor(professor);
        mural.setAtivo(true);

        mural = muralRepository.save(mural);
        log.info("Postagem criada com sucesso. ID da postagem: {}", mural.getId());

        return muralMapper.toResponse(mural);
    }

    @Override
    public MuralResponse buscarPostagemPorId(Long id) {
        log.info("Buscando postagem no mural com ID: {}", id);

        return muralRepository.findById(id)
                .map(muralMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Postagem não encontrada para ID: {}", id);
                    return new PostagemNaoEncontradaException("Postagem não encontrada");
                });
    }

    @Override
    public Page<MuralResponse> listarPostagensPorTurma(Long idTurma, Pageable pageable) {
        log.info("Listando postagens ativas para Turma ID: {} com paginação: {}", idTurma, pageable);

        Page<MuralResponse> responses = muralRepository
                .findByTurmaIdAndAtivoTrue(idTurma, pageable)
                .map(muralMapper::toResponse);

        log.info("Total de postagens encontradas: {}", responses.getTotalElements());
        return responses;
    }

    @Override
    public void deletarPostagem(Long id) {
        log.info("Iniciando exclusão lógica da postagem com ID: {}", id);

        Mural mural = muralRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de exclusão falhou. Postagem não encontrada ou já está desativada. ID: {}", id);
                    return new PostagemNaoEncontradaException("Postagem não encontrada");
                });

        mural.setAtivo(false);
        muralRepository.save(mural);
        log.info("Postagem desativada com sucesso. ID: {}", id);
    }

}
