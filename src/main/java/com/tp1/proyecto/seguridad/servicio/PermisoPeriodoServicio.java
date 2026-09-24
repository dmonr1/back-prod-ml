package com.tp1.proyecto.seguridad.servicio;

import com.tp1.proyecto.academico.entidad.PeriodoAcademico;
import com.tp1.proyecto.excepcion.ReglaNegocioException;
import java.time.Year;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PermisoPeriodoServicio {

    private static final String ROL_ADMIN = "ADMIN";
    private static final String ROL_DIRECTOR_ACADEMICO = "DIRECTOR_ACADEMICO";

    public void validarEdicion(PeriodoAcademico periodoAcademico) {
        if (periodoAcademico != null) {
            validarAnio(periodoAcademico.getAnio());
        }
    }

    public void validarCreacion(Integer anio) {
        validarAnio(anio);
    }

    private void validarAnio(Integer anio) {
        if (anio == null || !esDirectorSinRolAdministrador() || anio >= Year.now().getValue()) {
            return;
        }

        throw new ReglaNegocioException(
            "Los periodos historicos solo pueden ser modificados por un administrador."
        );
    }

    private boolean esDirectorSinRolAdministrador() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !(autenticacion.getPrincipal() instanceof UsuarioAutenticado)) {
            return false;
        }

        UsuarioAutenticado usuario = (UsuarioAutenticado) autenticacion.getPrincipal();
        boolean esAdministrador = usuario.getRoles().contains(ROL_ADMIN);
        boolean esDirector = usuario.getRoles().contains(ROL_DIRECTOR_ACADEMICO);
        return esDirector && !esAdministrador;
    }
}
