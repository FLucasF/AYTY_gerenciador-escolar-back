package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorRequest;
import br.com.ufpb.GerenciadorEscolar.dto.professor.ProfessorResponse;
import br.com.ufpb.GerenciadorEscolar.model.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProfessorServiceInterface {

    /**
     * Lista os professores ativos com paginação.
     *
     * @param pageable Configuração da paginação.
     * @return Página contendo os professores ativos.
     */
    Page<ProfessorResponse> listarProfessoresAtivos(Pageable pageable);

    /**
     * Busca um professor ativo pelo ID.
     *
     * @param id ID do professor.
     * @return Optional contendo o ProfessorResponse se encontrado.
     * @throws IllegalArgumentException se o ID for inválido.
     */
    ProfessorResponse buscarProfessorPorId(Long id);

    /**
     * Cadastra um novo professor.
     *
     * @param professorRequest Dados do professor a ser cadastrado.
     * @return ProfessorResponse com os dados do professor cadastrado.
     * @throws EmailJaCadastradoException se o e-mail já estiver em uso.
     * @throws CpfJaCadastradoException se o CPF já estiver em uso.
     */
    ProfessorResponse cadastrarProfessor(ProfessorRequest professorRequest);

    /**
     * Atualiza os dados de um professor existente.
     *
     * @param id ID do professor a ser atualizado.
     * @param professorRequest Dados a serem atualizados.
     * @return ProfessorResponse com os dados atualizados.
     * @throws ProfessorNaoEncontradoException se o professor não for encontrado.
     * @throws NenhumaAlteracaoRealizadaException se nenhuma mudança for feita.
     */
    ProfessorResponse atualizarProfessor(Long id, ProfessorRequest professorRequest);

    /**
     * Desativa um professor, tornando-o inativo.
     *
     * @param id ID do professor a ser desativado.
     * @throws ProfessorNaoEncontradoException se o professor não for encontrado.
     */
    void desativarProfessor(Long id);

}
