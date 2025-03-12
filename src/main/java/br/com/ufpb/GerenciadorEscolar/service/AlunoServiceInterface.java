package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.model.dto.aluno.AlunoRequest;
import br.com.ufpb.GerenciadorEscolar.model.dto.aluno.AlunoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlunoServiceInterface {

    /**
     * Lista todos os alunos ativos com paginação.
     */
    Page<AlunoResponse> listarAlunosAtivos(Pageable pageable);

    /**
     * Busca um aluno ativo pelo ID.
     *
     * @param id ID do aluno a ser buscado.
     * @return DTO do aluno encontrado.
     * @throws AlunoNaoEncontradoException se o aluno não for encontrado.
     */
    AlunoResponse buscarAlunoPorId(Long id);

    /**
     * Cadastra um novo aluno no sistema.
     *
     * @param alunoRequest Dados do aluno a ser cadastrado.
     * @return DTO do aluno cadastrado.
     * @throws EmailJaCadastradoException se o e-mail já estiver cadastrado.
     * @throws CpfJaCadastradoException se o CPF já estiver cadastrado.
     */
    AlunoResponse cadastrarAluno(AlunoRequest alunoRequest);

    /**
     * Atualiza os dados de um aluno ativo.
     *
     * @param id ID do aluno a ser atualizado.
     * @param alunoRequest Novos dados do aluno.
     * @return DTO do aluno atualizado.
     * @throws AlunoNaoEncontradoException se o aluno não for encontrado.
     * @throws EmailJaCadastradoException se o novo e-mail já estiver em uso.
     * @throws NenhumaAlteracaoRealizadaException se nenhuma alteração for feita.
     */
    AlunoResponse atualizarAluno(Long id, AlunoRequest alunoRequest);

    /**
     * Desativa um aluno, tornando-o inativo no sistema.
     *
     * @param id ID do aluno a ser desativado.
     * @throws AlunoNaoEncontradoException se o aluno não for encontrado.
     */
    void desativarAluno(Long id);
}
