package com.tp1.proyecto.documento.dto;

public class TipoDocumentoRespuestaDto {

    private Long id;
    private String codigo;
    private String descripcionLarga;
    private String descripcionCorta;
    private Short longitud;
    private String tipo;
    private String alcanceNacionalidad;
    private boolean longitudExacta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcionLarga() { return descripcionLarga; }
    public void setDescripcionLarga(String descripcionLarga) { this.descripcionLarga = descripcionLarga; }
    public String getDescripcionCorta() { return descripcionCorta; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }
    public Short getLongitud() { return longitud; }
    public void setLongitud(Short longitud) { this.longitud = longitud; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getAlcanceNacionalidad() { return alcanceNacionalidad; }
    public void setAlcanceNacionalidad(String alcanceNacionalidad) { this.alcanceNacionalidad = alcanceNacionalidad; }
    public boolean isLongitudExacta() { return longitudExacta; }
    public void setLongitudExacta(boolean longitudExacta) { this.longitudExacta = longitudExacta; }
}
