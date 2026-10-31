package br.com.radarcidadaogv.denuncia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException; import java.nio.file.*; import java.time.YearMonth; import java.util.*;

@Service
public class DenunciaService {
    private static final Set<String> TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private final DenunciaRepository repository; private final ClassificadorDenuncia classifier; private final Path uploadDirectory;
    public DenunciaService(DenunciaRepository repository, ClassificadorDenuncia classifier, @Value("${app.uploads-directory}") String uploadDirectory) { this.repository = repository; this.classifier = classifier; this.uploadDirectory = Paths.get(uploadDirectory).toAbsolutePath().normalize(); }
    @Transactional
    public Denuncia criar(CriarDenunciaRequest request, List<MultipartFile> fotos) {
        if (fotos.size() > 3) throw new IllegalArgumentException("Envie no máximo três fotos.");
        Classificacao classification = classifier.classificar(request.descricao(), request.categoria());
        Denuncia report = new Denuncia(); report.setProtocolo("RGV-" + YearMonth.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        report.setDescricao(request.descricao().trim()); report.setEndereco(blank(request.endereco())); report.setLatitude(request.latitude()); report.setLongitude(request.longitude()); report.setCategoriaInformada(request.categoria()); report.setCategoriaSugerida(classification.categoria()); report.setPrioridadeSugerida(classification.prioridade()); report.setJustificativaIa(classification.justificativa()); report.setMetodoClassificacao(classification.metodo());
        fotos.forEach(file -> report.getAnexos().add(store(file, report)));
        return repository.save(report);
    }
    public Page<Denuncia> listar(int page, int size) { return repository.findAll(PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), Sort.by(Sort.Direction.DESC, "criadaEm"))); }
    public Resumo resumo() { return new Resumo(repository.count(), repository.countBySituacao(Situacao.RECEBIDA), repository.countBySituacao(Situacao.EM_ANALISE), repository.countBySituacao(Situacao.RESOLVIDA)); }
    @Transactional public Denuncia atualizarSituacao(Long id, Situacao situacao) { Denuncia report = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Denúncia não encontrada.")); report.setSituacao(situacao); return report; }
    private Anexo store(MultipartFile file, Denuncia report) { if (file.isEmpty() || !TYPES.contains(file.getContentType()) || file.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("Cada foto deve ser JPG, PNG ou WebP de até 5 MB."); try { Files.createDirectories(uploadDirectory); String ext = Optional.ofNullable(file.getOriginalFilename()).filter(n -> n.contains(".")).map(n -> n.substring(n.lastIndexOf('.'))).orElse(".bin"); String stored = UUID.randomUUID() + ext; Files.copy(file.getInputStream(), uploadDirectory.resolve(stored), StandardCopyOption.REPLACE_EXISTING); Anexo attachment = new Anexo(); attachment.setDenuncia(report); attachment.setNomeOriginal(file.getOriginalFilename()); attachment.setNomeArmazenado(stored); attachment.setTipoConteudo(file.getContentType()); attachment.setTamanhoBytes(file.getSize()); return attachment; } catch (IOException exception) { throw new IllegalStateException("Não foi possível guardar a foto.", exception); } }
    private String blank(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    public record Resumo(long total, long recebidas, long emAnalise, long resolvidas) { }
}
