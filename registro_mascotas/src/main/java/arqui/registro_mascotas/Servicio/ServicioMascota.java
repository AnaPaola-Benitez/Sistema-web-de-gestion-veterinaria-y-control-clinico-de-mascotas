package arqui.registro_mascotas.Servicio;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arqui.registro_mascotas.BD.RepositorioMascotas;
import arqui.registro_mascotas.Modelo.Mascota;

/**
 * Gestiona las operaciones y validaciones de mascotas de los RF-01 a RF-06.
 * Implementa las operaciones del UML sobre el modelo actual; las reglas de
 * propietarios y registros clínicos se integrarán al implementar esos módulos.
 */
@Service
public class ServicioMascota {

    private final RepositorioMascotas repositorio;

    /**
     * Crea el servicio con su dependencia de persistencia.
     *
     * @param repositorio repositorio de mascotas
     */
    public ServicioMascota(RepositorioMascotas repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Registra una mascota después de validar sus datos (RF-01 y RF-06).
     *
     * @param mascota datos de la nueva mascota
     * @return mascota almacenada con su identificador
     * @throws IllegalArgumentException si los datos son inválidos o ya tienen ID
     */
    @Transactional
    public Mascota registrar(Mascota mascota) {
        validar(mascota);
        if (mascota.getId() != 0) {
            throw new IllegalArgumentException("Una mascota nueva no debe tener identificador.");
        }
        return repositorio.save(mascota);
    }

    /**
     * Consulta las mascotas registradas (RF-02).
     *
     * @return lista de mascotas, vacía cuando no hay registros
     */
    @Transactional(readOnly = true)
    public List<Mascota> listar() {
        return repositorio.findAll();
    }

    /**
     * Consulta una mascota existente (RF-02).
     *
     * @param id identificador de la mascota
     * @return mascota encontrada
     * @throws IllegalArgumentException si la mascota no existe
     */
    @Transactional(readOnly = true)
    public Mascota buscar(int id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de mascota inexistente"));
    }

    /**
     * Actualiza los campos editables de una mascota existente (RF-03 y RF-06).
     * El identificador de los datos recibidos no modifica el registro elegido.
     *
     * @param id identificador del registro que se actualizará
     * @param datos nuevos valores de nombre, edad y fotografía
     * @return mascota actualizada
     * @throws IllegalArgumentException si los datos son inválidos o no existe el ID
     */
    @Transactional
    public Mascota actualizar(int id, Mascota datos) {
        validar(datos);
        Mascota mascota = buscar(id);
        mascota.setNombre(datos.getNombre());
        mascota.setEdad(datos.getEdad());
        mascota.setUrlImagen(datos.getUrlImagen());
        return repositorio.save(mascota);
    }

    /**
     * Elimina una mascota después de comprobar su existencia (RF-04).
     *
     * @param id identificador de la mascota
     * @throws IllegalArgumentException si la mascota no existe
     */
    @Transactional
    public void eliminar(int id) {
        buscar(id);
        repositorio.deleteById(id);
    }

    /**
     * Elimina una selección en una transacción (RF-05). Comprueba todos los
     * identificadores antes de eliminar; una selección nula o vacía no hace cambios.
     *
     * @param ids identificadores seleccionados
     * @throws IllegalArgumentException si hay un ID nulo o una mascota inexistente
     */
    @Transactional
    public void eliminarMultiples(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Integer id : ids) {
            if (id == null) {
                throw new IllegalArgumentException("La selección contiene un identificador de mascota inválido.");
            }
            buscar(id);
        }
        repositorio.deleteAllById(ids);
    }

    /**
     * Valida los campos actuales de una mascota sin acceder a la BD (RF-06).
     * El nombre es obligatorio, la edad admite cero y la fotografía es opcional.
     * Si se indica una fotografía, debe ser una URL HTTP o HTTPS con servidor.
     *
     * @param mascota datos que se validarán
     * @throws IllegalArgumentException si se incumple alguna regla
     */
    public void validar(Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("Los datos de la mascota son obligatorios.");
        }
        if (mascota.getNombre() == null || mascota.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la mascota no puede estar vacío.");
        }
        if (mascota.getEdad() < 0) {
            throw new IllegalArgumentException("La edad de la mascota no puede ser negativa.");
        }
        String urlImagen = mascota.getUrlImagen();
        if (urlImagen == null || urlImagen.isBlank()) {
            return;
        }
        try {
            URI uri = new URI(urlImagen);
            if (("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme())) && uri.getHost() != null) {
                return;
            }
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("La URL de la fotografía debe ser una dirección HTTP o HTTPS válida.", ex);
        }
        throw new IllegalArgumentException("La URL de la fotografía debe ser una dirección HTTP o HTTPS válida.");
    }
}
