package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlunoServiceImpl implements AlunoServiceInterface {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AlunoServiceImpl(AlunoRepository alunoRepository, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<AlunoResponse> listarAlunosAtivos(Pageable pageable) {
        return alunoRepository.findAllByAtivoTrue(pageable)
                .map(aluno -> new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getCurso()));
    }

    @Override
    public Optional<AlunoResponse> buscarAlunoPorId(Long id) {
        return alunoRepository.findByIdAndAtivoTrue(id)
                .map(aluno -> new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getCurso()));
    }


    @Override
    public AlunoResponse cadastrarAluno(AlunoRequest alunoRequest) {
        Aluno aluno = new Aluno(
                alunoRequest.nome(),
                alunoRequest.email(),
                passwordEncoder.encode(alunoRequest.senha()),
                alunoRequest.cpf(),
                alunoRequest.curso()
        );
        aluno.setAtivo(true);
        alunoRepository.save(aluno);

        return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getCurso());
    }

    @Override
    public AlunoResponse atualizarAluno(Long id, AlunoRequest alunoRequest) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        aluno.setNome(alunoRequest.nome());
        aluno.setEmail(alunoRequest.email());
        aluno.setCurso(alunoRequest.curso());

        alunoRepository.save(aluno);
        return new AlunoResponse(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCpf(), aluno.getCurso());
    }

    @Override
    public void desativarAluno(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
    }

    @Override
    public Optional<Aluno> findByEmail(String email) {
        return alunoRepository.findByEmailAndAtivoTrue(email);
    }

}
