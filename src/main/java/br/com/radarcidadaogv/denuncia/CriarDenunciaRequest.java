package br.com.radarcidadaogv.denuncia;
import jakarta.validation.constraints.*;
public record CriarDenunciaRequest(
    @NotBlank @Size(max = 2000) String descricao,
    @NotNull Categoria categoria,
    @Size(max = 255) String endereco,
    @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
    @DecimalMin("-180.0") @DecimalMax("180.0") double longitude) { }
