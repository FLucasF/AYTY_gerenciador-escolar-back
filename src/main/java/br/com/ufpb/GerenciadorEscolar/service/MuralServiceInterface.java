package br.com.ufpb.GerenciadorEscolar.service;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MuralServiceInterface {

    /**
     * Salva uma nova postagem no mural.
     *
     * @param muralRequest Dados da postagem a ser criada.
     * @param imagem Arquivo de imagem opcional.
     * @return MuralResponse contendo os detalhes da postagem salva.
     */
    MuralResponse criarPostagem(MuralRequest muralRequest, MultipartFile imagem);

    /**
     * Busca uma postagem no mural pelo ID.
     *
     * @param id ID da postagem.
     * @return MuralResponse contendo os detalhes da postagem.
     */
    MuralResponse buscarPostagemPorId(Long id);

    /**
     * Lista as postagens do mural de uma turma com paginação.
     *
     * @param idTurma ID da turma.
     * @param pageable Objeto de paginação.
     * @return Página contendo as postagens ativas do mural.
     */
    Page<MuralResponse> listarPostagensPorTurma(Long idTurma, Pageable pageable);

    /**
     * Desativa uma postagem no mural.
     *
     * @param id ID da postagem a ser desativada.
     */
    void deletarPostagem(Long id);
}
