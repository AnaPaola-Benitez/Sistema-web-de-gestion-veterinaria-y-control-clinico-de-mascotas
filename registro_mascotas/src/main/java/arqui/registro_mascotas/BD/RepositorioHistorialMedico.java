package arqui.registro_mascotas.BD;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import arqui.registro_mascotas.Modelo.AtencionClinica;

/** Acceso a las atenciones clínicas del historial médico de las mascotas. */
public interface RepositorioHistorialMedico extends JpaRepository<AtencionClinica, Integer> {
    List<AtencionClinica> findByMascotaIdOrderByFechaDesc(Integer id);
    boolean existsByMascotaId(Integer id);
}
