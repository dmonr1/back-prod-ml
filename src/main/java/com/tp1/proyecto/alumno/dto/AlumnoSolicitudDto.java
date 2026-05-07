package com.tp1.proyecto.alumno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AlumnoSolicitudDto {

    @NotBlank(message = "El codigo es obligatorio")
    @Size(max = 30, message = "El codigo no debe exceder 30 caracteres")
    private String codigo;

    @Size(max = 8, message = "El DNI no debe exceder 8 caracteres")
    private String dni;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no deben exceder 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no deben exceder 100 caracteres")
    private String apellidos;

    @Past(message = "La fecha de nacimiento debe ser valida")
    private LocalDate fechaNacimiento;

    @Size(max = 20, message = "El sexo no debe exceder 20 caracteres")
    private String sexo;

    @Size(max = 255, message = "La direccion no debe exceder 255 caracteres")
    private String direccion;

    @Size(max = 150, message = "El nombre del apoderado no debe exceder 150 caracteres")
    private String nombreApoderado;

    @Size(max = 20, message = "El telefono del apoderado no debe exceder 20 caracteres")
    private String telefonoApoderado;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreApoderado() {
        return nombreApoderado;
    }

    public void setNombreApoderado(String nombreApoderado) {
        this.nombreApoderado = nombreApoderado;
    }

    public String getTelefonoApoderado() {
        return telefonoApoderado;
    }

    public void setTelefonoApoderado(String telefonoApoderado) {
        this.telefonoApoderado = telefonoApoderado;
    }
}
