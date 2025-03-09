package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.service.MuralServiceImpl;
import br.com.ufpb.GerenciadorEscolar.service.MuralServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/murais")
@Slf4j
public class MuralController {

    private final MuralServiceImpl muralService;

    @Autowired
    public MuralController(MuralServiceImpl muralService) {
        this.muralService = muralService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MuralResponse> criarPostagem(
            @RequestPart("mural") MuralRequest muralRequest,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem) {
        if (imagem != null) {
            System.out.println("Imagem recebida: " + imagem.getOriginalFilename() +
                    " - Tamanho: " + imagem.getSize() + " bytes");
        } else {
            System.out.println("Imagem está nula");
        }
        MuralResponse response = muralService.criarPostagem(muralRequest, imagem);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/turma/{idTurma}")
    public ResponseEntity<Page<MuralResponse>> listarPostagensPorTurma(
            @PathVariable("idTurma") Long idTurma,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dataCriacao,desc") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("dataCriacao")));

        log.info("📜 Listando postagens da turma ID: {} com paginação: {}", idTurma, pageable);

        Page<MuralResponse> response = muralService.listarPostagensPorTurma(idTurma, pageable);

        if (response.isEmpty()) {
            log.warn("⚠️ Nenhuma postagem encontrada para a turma ID: {}", idTurma);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}")
    public ResponseEntity<MuralResponse> buscarPostagemPorId(
            @PathVariable Long id) {
        // Se necessário, o parâmetro "service" pode ser usado para construir a URL.
        // Neste exemplo, utilizamos o valor da constante Material.SERVICE_NAME no serviço.
        MuralResponse response = muralService.buscarPostagemPorId(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPostagem(@PathVariable Long id) {
        log.info("🗑️ Recebendo solicitação para deletar material com ID: {}", id);

        muralService.deletarPostagem(id);

        log.info("✅ Material deletado com sucesso. ID: {}", id);
        return ResponseEntity.noContent().build();
    }

}
