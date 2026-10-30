package br.com.radarcidadaogv.denuncia;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Denuncia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 20) private String protocolo;
    @Column(nullable = false, length = 2000) private String descricao;
    @Column(length = 255) private String endereco;
    private double latitude;
    private double longitude;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Categoria categoriaInformada;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Categoria categoriaSugerida;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Prioridade prioridadeSugerida;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Situacao situacao = Situacao.RECEBIDA;
    @Column(length = 500) private String justificativaIa;
    @Column(nullable = false) private String metodoClassificacao;
    @Column(nullable = false) private Instant criadaEm;
    @OneToMany(mappedBy = "denuncia", cascade = CascadeType.ALL, orphanRemoval = true) private List<Anexo> anexos = new ArrayList<>();

    @PrePersist void onCreate() { if (criadaEm == null) criadaEm = Instant.now(); }
    public Long getId() { return id; } public String getProtocolo() { return protocolo; } public void setProtocolo(String value) { protocolo = value; }
    public String getDescricao() { return descricao; } public void setDescricao(String value) { descricao = value; }
    public String getEndereco() { return endereco; } public void setEndereco(String value) { endereco = value; }
    public double getLatitude() { return latitude; } public void setLatitude(double value) { latitude = value; }
    public double getLongitude() { return longitude; } public void setLongitude(double value) { longitude = value; }
    public Categoria getCategoriaInformada() { return categoriaInformada; } public void setCategoriaInformada(Categoria value) { categoriaInformada = value; }
    public Categoria getCategoriaSugerida() { return categoriaSugerida; } public void setCategoriaSugerida(Categoria value) { categoriaSugerida = value; }
    public Prioridade getPrioridadeSugerida() { return prioridadeSugerida; } public void setPrioridadeSugerida(Prioridade value) { prioridadeSugerida = value; }
    public Situacao getSituacao() { return situacao; } public void setSituacao(Situacao value) { situacao = value; }
    public String getJustificativaIa() { return justificativaIa; } public void setJustificativaIa(String value) { justificativaIa = value; }
    public String getMetodoClassificacao() { return metodoClassificacao; } public void setMetodoClassificacao(String value) { metodoClassificacao = value; }
    public Instant getCriadaEm() { return criadaEm; } public List<Anexo> getAnexos() { return anexos; }
}
