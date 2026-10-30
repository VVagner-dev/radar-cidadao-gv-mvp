package br.com.radarcidadaogv.localizacao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/localizacao")
@CrossOrigin(origins = "http://localhost:4200")
public class LocalizacaoController {
    private final String token;
    private final ObjectMapper mapper = new ObjectMapper();

    public LocalizacaoController(@Value("${LOCATIONIQ_TOKEN:}") String token) { this.token = token; }

    @GetMapping("/sugestoes")
    public List<SugestaoEndereco> sugestoes(@RequestParam String q) throws Exception {
        if (q == null || q.trim().length() < 3) return List.of();
        if (token.isBlank()) throw new IllegalStateException("A busca de endereço ainda não foi configurada.");
        String body = RestClient.create("https://api.locationiq.com").get().uri(uri -> uri.path("/v1/autocomplete")
                .queryParam("key", token).queryParam("q", q).queryParam("countrycodes", "br").queryParam("viewbox", "-40.45,-20.20,-40.20,-20.45").queryParam("bounded", 1).queryParam("limit", 5).build())
                .retrieve().body(String.class);
        JsonNode results = mapper.readTree(body);
        List<SugestaoEndereco> response = new ArrayList<>();
        for (JsonNode item : results) response.add(new SugestaoEndereco(item.path("display_name").asText(), item.path("display_place").asText("Endereço sugerido"), item.path("lat").asDouble(), item.path("lon").asDouble()));
        return response;
    }

    @GetMapping(value = "/mapa/{z}/{x}/{y}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> mapa(@org.springframework.web.bind.annotation.PathVariable int z, @org.springframework.web.bind.annotation.PathVariable int x, @org.springframework.web.bind.annotation.PathVariable int y) {
        if (token.isBlank()) return ResponseEntity.notFound().build();
        byte[] tile = RestClient.create("https://a-tiles.locationiq.com").get().uri(uri -> uri.path("/v3/light/r/{z}/{x}/{y}.png")
                .queryParam("key", token).build(z, x, y)).retrieve().body(byte[].class);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(tile);
    }

    public record SugestaoEndereco(String displayName, String displayPlace, double latitude, double longitude) { }
}
