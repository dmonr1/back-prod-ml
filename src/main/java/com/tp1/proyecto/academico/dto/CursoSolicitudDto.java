package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CursoSolicitudDto {

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 100, message = "El nombre del curso no debe exceder 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripcion no debe exceder 255 caracteres")
    private String descripcion;

    @Size(max = 30, message = "El color de portada no debe exceder 30 caracteres")
    private String portadaColor;

    @Size(max = 80, message = "El icono de portada no debe exceder 80 caracteres")
    private String portadaIcono;

    @Size(max = 255, message = "La imagen de portada no debe exceder 255 caracteres")
    private String portadaImagen;

    @NotNull(message = "El nivel es obligatorio")
    private Long nivelId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPortadaColor() {
        return portadaColor;
    }

    public void setPortadaColor(String portadaColor) {
        this.portadaColor = portadaColor;
    }

    public String getPortadaIcono() {
        return portadaIcono;
    }

    public void setPortadaIcono(String portadaIcono) {
        this.portadaIcono = portadaIcono;
    }

    public String getPortadaImagen() {
        return portadaImagen;
    }

    public void setPortadaImagen(String portadaImagen) {
        this.portadaImagen = portadaImagen;
    }

    public Long getNivelId() {
        return nivelId;
    }

    public void setNivelId(Long nivelId) {
        this.nivelId = nivelId;
    }
}
