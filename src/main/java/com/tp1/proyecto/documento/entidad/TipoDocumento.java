package com.tp1.proyecto.documento.entidad;

import com.tp1.proyecto.comun.entidad.AuditoriaEntidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipos_documento", schema = "db_tp1")
public class TipoDocumento extends AuditoriaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 2)
    private String codigo;

    @Column(name = "descripcion_larga", nullable = false, length = 100)
    private String descripcionLarga;

    @Column(name = "descripcion_corta", nullable = false, length = 30)
    private String descripcionCorta;

    @Column(name = "longitud", nullable = false)
    private Short longitud;

    @Column(name = "tipo", nullable = false, length = 15)
    private String tipo;

    @Column(name = "alcance_nacionalidad", nullable = false, length = 15)
    private String alcanceNacionalidad;

    @Column(name = "longitud_exacta", nullable = false)
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
