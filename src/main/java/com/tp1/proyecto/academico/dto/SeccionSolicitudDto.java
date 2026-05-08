package com.tp1.proyecto.academico.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SeccionSolicitudDto {

    @NotNull(message = "El grado es obligatorio")
    private Long gradoId;

    @NotBlank(message = "El nombre de la seccion es obligatorio")
    @Size(max = 20, message = "El nombre de la seccion no debe exceder 20 caracteres")
    private String nombre;

    @Min(value = 1, message = "La capacidad debe ser mayor o igual a 1")
    @Max(value = 100, message = "La capacidad no debe exceder 100")
    private Integer capacidad;

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }
}
