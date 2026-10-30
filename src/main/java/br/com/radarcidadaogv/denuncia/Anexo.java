package br.com.radarcidadaogv.denuncia;

import jakarta.persistence.*;

@Entity
public class Anexo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "denuncia_id", nullable = false) private Denuncia denuncia;
    @Column(nullable = false) private String nomeOriginal;
    @Column(nullable = false, unique = true) private String nomeArmazenado;
    @Column(nullable = false) private String tipoConteudo;
    private long tamanhoBytes;
    public void setDenuncia(Denuncia value) { denuncia = value; } public void setNomeOriginal(String value) { nomeOriginal = value; }
    public void setNomeArmazenado(String value) { nomeArmazenado = value; } public void setTipoConteudo(String value) { tipoConteudo = value; }
    public void setTamanhoBytes(long value) { tamanhoBytes = value; }
}
