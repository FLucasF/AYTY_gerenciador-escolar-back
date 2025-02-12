package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.Mural;
import br.com.ufpb.GerenciadorEscolar.repository.MuralRepository;
import br.com.ufpb.GerenciadorEscolar.service.MuralServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MuralServiceImpl implements MuralServiceInterface {

    @Autowired
    private MuralRepository muralRepository;

    @Override
    public Mural criarPostagem(Mural mural) {
        return muralRepository.save(mural);
    }

    @Override
    public List<Mural> listarPostagensPorTurma(Long idTurma) {
        return muralRepository.findByTurmaId(idTurma);
    }

    @Override
    public void deletarPostagem(Long id) {
        muralRepository.deleteById(id);
    }
}
