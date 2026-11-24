package arqui.registro_mascotas.BD;

import org.springframework.data.jpa.repository.JpaRepository;

import arqui.registro_mascotas.Modelo.Propietario;

/** Acceso a los propietarios registrados en la base de datos. */
public interface RepositorioPropietario extends JpaRepository<Propietario, Integer> {
}
