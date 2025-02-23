package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
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
    private final AlunoMapper alunoMapper;

    @Autowired
    public AlunoServiceImpl(AlunoRepository alunoRepository,
                            PasswordEncoder passwordEncoder,
                            AlunoMapper alunoMapper) {
        this.alunoRepository = alunoRepository;
        this.passwordEncoder = passwordEncoder;
        this.alunoMapper = alunoMapper;
    }

    @Override
    public Page<AlunoResponse> listarAlunosAtivos(Pageable pageable) {
        return alunoRepository.findAllByAtivoTrue(pageable)
                .map(alunoMapper::toResponse);
    }

    @Override
    public Optional<AlunoResponse> buscarAlunoPorId(Long id) {
        return alunoRepository.findByIdAndAtivoTrue(id)
                .map(alunoMapper::toResponse);
    }

    @Override
    public AlunoResponse cadastrarAluno(AlunoRequest alunoRequest) {
        Aluno aluno = alunoMapper.toEntity(alunoRequest);
        aluno.setSenha(passwordEncoder.encode(alunoRequest.senha()));
        aluno.setAtivo(true);
        alunoRepository.save(aluno);
        return alunoMapper.toResponse(aluno);
    }

    @Override
    public AlunoResponse atualizarAluno(Long id, AlunoRequest alunoRequest) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        aluno.setNome(alunoRequest.nome());
        aluno.setEmail(alunoRequest.email());
        aluno.setCurso(alunoRequest.curso());
        alunoRepository.save(aluno);
        return alunoMapper.toResponse(aluno);
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
