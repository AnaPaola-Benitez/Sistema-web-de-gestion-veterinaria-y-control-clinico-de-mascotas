package arqui.registro_mascotas.Servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arqui.registro_mascotas.BD.RepositorioHistorialMedico;
import arqui.registro_mascotas.BD.RepositorioMascotas;
import arqui.registro_mascotas.Modelo.AtencionClinica;
import arqui.registro_mascotas.Modelo.Mascota;

/**
 * Servicio para el registro y consulta cronológica del historial médico de las mascotas (RF-16 y RF-17).
 */
@Service
public class ServicioHistorialMedico {

    private final RepositorioHistorialMedico repositorio;
    private final RepositorioMascotas repositorioMascotas;

    public ServicioHistorialMedico(RepositorioHistorialMedico repositorio,
                                   RepositorioMascotas repositorioMascotas) {
        this.repositorio = repositorio;
        this.repositorioMascotas = repositorioMascotas;
    }

    @Transactional
    public AtencionClinica registrar(AtencionClinica a) {
        validar(a);
        Mascota mascota = repositorioMascotas.findById(a.getMascota().getId())
                .orElseThrow(() -> new IllegalArgumentException("La mascota asociada no existe."));
        a.setMascota(mascota);
        if (a.getFecha() == null) {
            a.setFecha(LocalDate.now());
        }
        return repositorio.save(a);
    }

    @Transactional(readOnly = true)
    public List<AtencionClinica> consultar(int mascotaId) {
        if (!repositorioMascotas.existsById(mascotaId)) {
            throw new IllegalArgumentException("Id de mascota inexistente");
        }
        return repositorio.findByMascotaIdOrderByFechaDesc(mascotaId);
    }

    public void validar(AtencionClinica a) {
        if (a == null) {
            throw new IllegalArgumentException("Los datos de la atención clínica son obligatorios.");
        }
        if (a.getMascota() == null || a.getMascota().getId() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una mascota válida.");
        }
        if (a.getDiagnostico() == null || a.getDiagnostico().isBlank()) {
            throw new IllegalArgumentException("El diagnóstico es obligatorio.");
        }
        if (a.getTratamiento() == null || a.getTratamiento().isBlank()) {
            throw new IllegalArgumentException("El tratamiento es obligatorio.");
        }
    }
}
