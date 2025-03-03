package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.dto.aluno.AlunoResponse;
import br.com.ufpb.GerenciadorEscolar.mapper.AlunoMapper;
import br.com.ufpb.GerenciadorEscolar.model.Aluno;
import br.com.ufpb.GerenciadorEscolar.repository.AlunoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
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
        log.info("Listando alunos ativos com paginação: {}", pageable);
        Page<AlunoResponse> page = alunoRepository.findAllByAtivoTrue(pageable)
                .map(alunoMapper::toResponse);
        log.info("Total de alunos ativos encontrados: {}", page.getTotalElements());
        return page;
    }

    @Override
    public Optional<AlunoResponse> buscarAlunoPorId(Long id) {
        log.info("Buscando aluno por ID: {}", id);
        Optional<AlunoResponse> response = alunoRepository.findByIdAndAtivoTrue(id)
                .map(alunoMapper::toResponse);
        if (response.isEmpty()) {
            log.warn("Aluno não encontrado para o ID: {}", id);
        }
        return response;
    }

    @Override
    public AlunoResponse cadastrarAluno(AlunoRequest alunoRequest) {
        log.info("🔍 Verificando se já existe aluno ativo com o e-mail {} ou CPF {}", alunoRequest.email(), alunoRequest.cpf());

        // ⚠️ Verifica se já existe um aluno ATIVO com esse e-mail ou CPF
        Optional<Aluno> alunoComMesmoEmail = alunoRepository.findByEmailAndAtivoTrue(alunoRequest.email());
        Optional<Aluno> alunoComMesmoCpf = alunoRepository.findByCpfAndAtivoTrue(alunoRequest.cpf());

        if (alunoComMesmoEmail.isPresent()) {
            log.warn("❌ Tentativa de cadastrar aluno com e-mail duplicado: {}", alunoRequest.email());
            throw new RuntimeException("Já existe um aluno ativo cadastrado com esse e-mail.");
        }

        if (alunoComMesmoCpf.isPresent()) {
            log.warn("❌ Tentativa de cadastrar aluno com CPF duplicado: {}", alunoRequest.cpf());
            throw new RuntimeException("Já existe um aluno ativo cadastrado com esse CPF.");
        }

        Aluno aluno = alunoMapper.toEntity(alunoRequest);
        aluno.setSenha(passwordEncoder.encode(alunoRequest.senha()));
        aluno.setAtivo(true);

        try {
            alunoRepository.save(aluno);
            log.info("✅ Aluno cadastrado com sucesso. ID: {}", aluno.getId());
        } catch (Exception e) {
            log.error("❌ Erro ao cadastrar aluno: {}", e.getMessage());
            throw new RuntimeException("Erro ao cadastrar aluno: " + e.getMessage(), e);
        }

        return alunoMapper.toResponse(aluno);
    }

    @Override
    public AlunoResponse atualizarAluno(Long id, AlunoRequest alunoRequest) {
        log.info("Atualizando aluno com ID: {}", id);

        // Buscar aluno ativo
        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.error("Tentativa de atualizar aluno inativo ou inexistente. ID: {}", id);
                    return new RuntimeException("Aluno não encontrado ou inativo.");
                });

        // Verificar se o e-mail já pertence a outro aluno ativo
        Optional<Aluno> alunoComMesmoEmail = alunoRepository.findByEmailAndAtivoTrue(alunoRequest.email());
        if (alunoComMesmoEmail.isPresent() && !alunoComMesmoEmail.get().getId().equals(id)) {
            log.warn("Tentativa de atualizar aluno com e-mail duplicado: {}", alunoRequest.email());
            throw new RuntimeException("Já existe outro aluno ativo cadastrado com esse e-mail.");
        }

        // Atualizar os dados
        aluno.setNome(alunoRequest.nome());
        aluno.setEmail(alunoRequest.email());
        aluno.setCurso(alunoRequest.curso());

        // **Se a senha foi enviada, encode ela. Se não, mantém a senha original**
        if (alunoRequest.senha() != null && !alunoRequest.senha().isEmpty()) {
            aluno.setSenha(passwordEncoder.encode(alunoRequest.senha()));
        } else {
            log.info("🔒 Senha não alterada. Mantendo a original.");
        }

        alunoRepository.save(aluno);
        log.info("✅ Aluno atualizado com sucesso. ID: {}", aluno.getId());

        return alunoMapper.toResponse(aluno);
    }



    @Override
    public void desativarAluno(Long id) {
        log.info("🔴 Desativando aluno com ID: {}", id);
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Aluno não encontrado para desativação. ID: {}", id);
                    return new RuntimeException("Aluno não encontrado");
                });
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
        log.info("✅ Aluno desativado com sucesso. ID: {}", id);
    }

    @Override
    public Optional<Aluno> findByEmail(String email) {
        log.debug("🔍 Buscando aluno por email: {}", email);
        Optional<Aluno> alunoOpt = alunoRepository.findByEmailAndAtivoTrue(email);
        if (alunoOpt.isEmpty()) {
            log.warn("⚠️ Aluno não encontrado para o email: {}", email);
        }
        return alunoOpt;
    }
}
