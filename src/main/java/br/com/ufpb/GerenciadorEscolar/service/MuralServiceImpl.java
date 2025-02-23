package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.MuralMapper;
import br.com.ufpb.GerenciadorEscolar.model.Mural;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import br.com.ufpb.GerenciadorEscolar.model.Turma;
import br.com.ufpb.GerenciadorEscolar.repository.MuralRepository;
import br.com.ufpb.GerenciadorEscolar.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MuralServiceImpl implements MuralServiceInterface {

    private final MuralRepository muralRepository;
    private final TurmaRepository turmaRepository;
    private final MuralMapper muralMapper;

    @Autowired
    public MuralServiceImpl(MuralRepository muralRepository, TurmaRepository turmaRepository, MuralMapper muralMapper) {
        this.muralRepository = muralRepository;
        this.turmaRepository = turmaRepository;
        this.muralMapper = muralMapper;
    }

    @Override
    public MuralResponse criarPostagem(MuralRequest muralRequest) {
        if (muralRequest.professorId() == null) {
            throw new IllegalArgumentException("O campo professorId é obrigatório para publicar uma postagem.");
        }

        Mural mural = muralMapper.toEntity(muralRequest);

        // Define o objeto Professor manualmente
        Professor professor = new Professor();
        professor.setId(muralRequest.professorId());
        mural.setProfessor(professor);

        // Se necessário, também pode definir o objeto Turma manualmente
        Turma turma = new Turma();
        turma.setId(muralRequest.turmaId());
        mural.setTurma(turma);

        mural.setAtivo(true);

        try {
            muralRepository.save(mural);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar a postagem no mural: " + e.getMessage(), e);
        }

        return muralMapper.toResponse(mural);
    }




    @Override
    public Optional<MuralResponse> buscarPostagemPorId(Long id) {
        return muralRepository.findById(id).map(muralMapper::toResponse);
    }

    @Override
    public List<MuralResponse> listarPostagensPorTurma(Long idTurma) {
        return muralRepository.findByTurmaIdAndAtivoTrue(idTurma).stream().map(muralMapper::toResponse).toList();
    }

    @Override
    public void deletarPostagem(Long id) {
        Mural mural = muralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postagem não encontrada"));
        mural.setAtivo(false);
        muralRepository.save(mural);
    }
}
