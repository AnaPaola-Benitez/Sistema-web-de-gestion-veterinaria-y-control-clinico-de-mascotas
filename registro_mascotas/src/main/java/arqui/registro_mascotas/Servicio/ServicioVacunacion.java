package arqui.registro_mascotas.Servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arqui.registro_mascotas.BD.RepositorioMascotas;
import arqui.registro_mascotas.BD.RepositorioVacunacion;
import arqui.registro_mascotas.Modelo.Mascota;
import arqui.registro_mascotas.Modelo.RegistroVacunacion;

/**
 * Servicio para el control de vacunación y detección de refuerzos pendientes (RF-18 a RF-20).
 */
@Service
public class ServicioVacunacion {

    private final RepositorioVacunacion repositorio;
    private final RepositorioMascotas repositorioMascotas;

    public ServicioVacunacion(RepositorioVacunacion repositorio,
                              RepositorioMascotas repositorioMascotas) {
        this.repositorio = repositorio;
        this.repositorioMascotas = repositorioMascotas;
    }

    @Transactional
    public RegistroVacunacion registrar(RegistroVacunacion r) {
        validar(r);
        Mascota mascota = repositorioMascotas.findById(r.getMascota().getId())
                .orElseThrow(() -> new IllegalArgumentException("La mascota asociada no existe."));
        r.setMascota(mascota);
        return repositorio.save(r);
    }

    @Transactional(readOnly = true)
    public List<RegistroVacunacion> consultar(int mascotaId) {
        if (!repositorioMascotas.existsById(mascotaId)) {
            throw new IllegalArgumentException("Id de mascota inexistente");
        }
        return repositorio.findByMascotaIdOrderByFechaAplicacionDesc(mascotaId);
    }

    @Transactional(readOnly = true)
    public List<RegistroVacunacion> pendientes(LocalDate f) {
        LocalDate fechaConsulta = (f != null) ? f : LocalDate.now();
        return repositorio.findByFechaRefuerzoLessThanEqual(fechaConsulta);
    }

    public void validar(RegistroVacunacion r) {
        if (r == null) {
            throw new IllegalArgumentException("Los datos de vacunación son obligatorios.");
        }
        if (r.getMascota() == null || r.getMascota().getId() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una mascota válida.");
        }
        if (r.getVacuna() == null || r.getVacuna().isBlank()) {
            throw new IllegalArgumentException("El nombre de la vacuna es obligatorio.");
        }
        if (r.getNumeroDosis() <= 0) {
            throw new IllegalArgumentException("El número de dosis debe ser mayor a cero.");
        }
        if (r.getFechaAplicacion() == null) {
            throw new IllegalArgumentException("La fecha de aplicación es obligatoria.");
        }
        if (r.getFechaRefuerzo() != null && r.getFechaRefuerzo().isBefore(r.getFechaAplicacion())) {
            throw new IllegalArgumentException("La fecha de refuerzo no puede ser anterior a la fecha de aplicación.");
        }
    }
}
