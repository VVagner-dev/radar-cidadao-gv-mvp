package br.com.radarcidadaogv.denuncia;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets; import java.util.*; import java.util.stream.Collectors;

@RestController @RequestMapping("/api/denuncias") @CrossOrigin(origins = "http://localhost:4200")
public class DenunciaController {
    private final DenunciaService service;
    public DenunciaController(DenunciaService service) { this.service = service; }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DenunciaResponse criar(@Valid @RequestPart("denuncia") CriarDenunciaRequest request, @RequestPart(value = "fotos", required = false) List<MultipartFile> fotos) { return map(service.criar(request, fotos == null ? List.of() : fotos)); }
    @GetMapping public Map<String, Object> listar(@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "12") int tamanho, @RequestParam(required = false) Categoria categoria, @RequestParam(required = false) Prioridade prioridade, @RequestParam(required = false) Situacao situacao) {
        List<DenunciaResponse> content = service.listar(pagina, tamanho).getContent().stream().map(this::map).filter(item -> (categoria == null || item.categoriaSugerida() == categoria) && (prioridade == null || item.prioridadeSugerida() == prioridade) && (situacao == null || item.situacao() == situacao)).toList();
        return Map.of("itens", content, "pagina", pagina, "tamanho", tamanho);
    }
    @GetMapping("/resumo") public DenunciaService.Resumo resumo() { return service.resumo(); }
    @PatchMapping("/{id}/situacao") public DenunciaResponse atualizar(@PathVariable Long id, @RequestParam Situacao situacao) { return map(service.atualizarSituacao(id, situacao)); }
    @GetMapping(value = "/exportar.csv", produces = "text/csv") public ResponseEntity<byte[]> csv() {
        String body = "protocolo,categoria,prioridade,situacao,endereco,criada_em\n" + service.listar(0, 50).getContent().stream().map(item -> String.join(",", quote(item.getProtocolo()), item.getCategoriaSugerida().name(), item.getPrioridadeSugerida().name(), item.getSituacao().name(), quote(item.getEndereco()), item.getCriadaEm().toString())).collect(Collectors.joining("\n"));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=denuncias.csv").contentType(new MediaType("text", "csv", StandardCharsets.UTF_8)).body(body.getBytes(StandardCharsets.UTF_8));
    }
    private String quote(String value) { return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\""; }
    private DenunciaResponse map(Denuncia item) { return new DenunciaResponse(item.getProtocolo(), item.getDescricao(), item.getEndereco(), item.getLatitude(), item.getLongitude(), item.getCategoriaInformada(), item.getCategoriaSugerida(), item.getPrioridadeSugerida(), item.getSituacao(), item.getJustificativaIa(), item.getMetodoClassificacao(), item.getCriadaEm()); }
    public record DenunciaResponse(String protocolo, String descricao, String endereco, double latitude, double longitude, Categoria categoriaInformada, Categoria categoriaSugerida, Prioridade prioridadeSugerida, Situacao situacao, String justificativaIa, String metodoClassificacao, java.time.Instant criadaEm) { }
}
