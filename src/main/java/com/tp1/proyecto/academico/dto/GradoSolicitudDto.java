package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GradoSolicitudDto {

    @NotBlank(message = "El nombre del grado es obligatorio")
    @Size(max = 50, message = "El nombre del grado no debe exceder 50 caracteres")
    private String nombre;

    @NotNull(message = "El orden es obligatorio")
    @Min(value = 1, message = "El orden debe ser mayor o igual a 1")
    @Max(value = 20, message = "El orden no debe exceder 20")
    private Short orden;

    @NotNull(message = "El nivel es obligatorio")
    private Long nivelId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Short getOrden() {
        return orden;
    }

    public void setOrden(Short orden) {
        this.orden = orden;
    }

    public Long getNivelId() {
        return nivelId;
    }

    public void setNivelId(Long nivelId) {
        this.nivelId = nivelId;
    }
}
