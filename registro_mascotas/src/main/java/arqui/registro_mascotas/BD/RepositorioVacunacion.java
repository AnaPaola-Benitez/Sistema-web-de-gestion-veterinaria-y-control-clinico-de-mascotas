package arqui.registro_mascotas.BD;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import arqui.registro_mascotas.Modelo.RegistroVacunacion;

/** Acceso a los registros de vacunación y consulta de refuerzos pendientes. */
public interface RepositorioVacunacion extends JpaRepository<RegistroVacunacion, Integer> {
    List<RegistroVacunacion> findByMascotaIdOrderByFechaAplicacionDesc(Integer id);
    List<RegistroVacunacion> findByFechaRefuerzoLessThanEqual(LocalDate fecha);
    boolean existsByMascotaId(Integer id);
}
