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

import java.lang.reflect.Method;
import java.util.*;

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

    // Método para validar os campos do AlunoRequest
    private void validarCampos(AlunoRequest alunoRequest) {
        // Obter todos os métodos get do AlunoRequest
        Arrays.stream(alunoRequest.getClass().getDeclaredMethods())
                .filter(method -> method.getName().startsWith("get")) // Pega apenas os métodos 'get'
                .forEach(method -> {
                    try {
                        Object value = method.invoke(alunoRequest); // Obtém o valor do campo

                        log.debug("Validando campo: {}", method.getName().substring(3));
                        // Verifica se o campo é nulo ou vazio
                        if (value == null) {
                            throw new NullPointerException("Campo " + method.getName().substring(3) + " não pode ser nulo.");
                        }

                        // Se for uma String, verifica se está vazia
                        if (value instanceof String && ((String) value).trim().isEmpty()) {
                            throw new IllegalArgumentException("Campo " + method.getName().substring(3) + " não pode ser vazio.");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao validar o campo: " + method.getName(), e);
                    }
                });
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
        if (id == null) throw new NullPointerException("ID não pode ser nulo");

        if(id <= 0) throw new IllegalArgumentException("ID não pode ser nulo ou inválido");

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

        // 🚨 Validação de campos nulos ou vazios diretamente no cadastrarAluno
// Lista com todos os campos que precisam ser validados e seus respectivos nomes
        List<Map.Entry<String, String>> campos = Arrays.asList(
                new AbstractMap.SimpleEntry<>("Nome", alunoRequest.nome()),
                new AbstractMap.SimpleEntry<>("Email", alunoRequest.email()),
                new AbstractMap.SimpleEntry<>("CPF", alunoRequest.cpf()),
                new AbstractMap.SimpleEntry<>("Curso", alunoRequest.curso()),
                new AbstractMap.SimpleEntry<>("Senha", alunoRequest.senha())
        );

        campos.forEach(campo -> {
            if (campo.getValue() == null) {
                throw new NullPointerException(campo.getKey() + " não pode ser nulo.");
            }

            if (campo.getValue().trim().isEmpty()) {
                throw new IllegalArgumentException(campo.getKey() + " não pode ser vazio.");
            }
        });


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
        log.info("🔄 Iniciando atualização do aluno com ID: {}", id);

        if (id == null) {
            log.error("❌ O ID do aluno não pode ser nulo.");
            throw new IllegalArgumentException("O ID do aluno não pode ser nulo.");
        }

        Aluno aluno = alunoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.error("❌ Aluno não encontrado ou inativo. ID: {}", id);
                    return new RuntimeException("Aluno não encontrado ou inativo.");
                });

        if (alunoRequest.email() != null && !alunoRequest.email().equals(aluno.getEmail())) {
            log.info("🔍 Verificando se já existe outro aluno ativo com o e-mail: {}", alunoRequest.email());

            Optional<Aluno> alunoComMesmoEmail = alunoRepository.findByEmailAndAtivoTrue(alunoRequest.email());

            if (alunoComMesmoEmail.isPresent() && !alunoComMesmoEmail.get().getId().equals(id)) {
                log.warn("❌ Tentativa de atualizar para um e-mail já existente: {}", alunoRequest.email());
                throw new RuntimeException("Já existe outro aluno ativo cadastrado com esse e-mail.");
            }

            log.info("📌 Atualizando e-mail: {} → {}", aluno.getEmail(), alunoRequest.email());
            aluno.setEmail(alunoRequest.email());
        }

        // Atualiza apenas os campos informados
        if (alunoRequest.nome() != null) {
            log.info("📌 Atualizando nome: {} → {}", aluno.getNome(), alunoRequest.nome());
            aluno.setNome(alunoRequest.nome());
        }
        if (alunoRequest.cpf() != null) {
            log.info("📌 Atualizando CPF: {} → {}", aluno.getCpf(), alunoRequest.cpf());
            aluno.setCpf(alunoRequest.cpf());
        }
        if (alunoRequest.curso() != null) {
            log.info("📌 Atualizando curso: {} → {}", aluno.getCurso(), alunoRequest.curso());
            aluno.setCurso(alunoRequest.curso());
        }

        // 🔒 Atualiza senha apenas se foi enviada e não está vazia, mantendo a senha antiga caso contrário
        if (alunoRequest.senha() == null || alunoRequest.senha().trim().isEmpty()) {
            log.info("🔒 Nenhuma senha nova fornecida. Mantendo a senha existente para o aluno ID: {}", id);
        } else {
            log.info("🔑 Atualizando senha para o aluno ID: {}", id);
            aluno.setSenha(passwordEncoder.encode(alunoRequest.senha()));
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
