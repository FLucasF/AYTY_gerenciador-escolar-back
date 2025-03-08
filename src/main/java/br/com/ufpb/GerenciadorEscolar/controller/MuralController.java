package br.com.ufpb.GerenciadorEscolar.controller;

import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralRequest;
import br.com.ufpb.GerenciadorEscolar.dto.mural.MuralResponse;
import br.com.ufpb.GerenciadorEscolar.service.MuralServiceImpl;
import br.com.ufpb.GerenciadorEscolar.service.MuralServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/murais")
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


    @GetMapping("/{id}")
    public ResponseEntity<MuralResponse> buscarPostagemPorId(
            @PathVariable Long id) {
        // Se necessário, o parâmetro "service" pode ser usado para construir a URL.
        // Neste exemplo, utilizamos o valor da constante Material.SERVICE_NAME no serviço.
        MuralResponse response = muralService.buscarPostagemPorId(id);
        return ResponseEntity.ok(response);
    }

}
