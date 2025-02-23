package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.MuralMapper;
import br.com.ufpb.GerenciadorEscolar.model.Mural;
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
        Mural mural = muralMapper.toEntity(muralRequest);
        mural.setAtivo(true);
        muralRepository.save(mural);
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
