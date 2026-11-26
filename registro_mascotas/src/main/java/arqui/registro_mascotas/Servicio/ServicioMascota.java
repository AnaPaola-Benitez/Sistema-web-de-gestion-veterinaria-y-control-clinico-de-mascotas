package arqui.registro_mascotas.Servicio;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import arqui.registro_mascotas.BD.RepositorioMascotas;
import arqui.registro_mascotas.BD.RepositorioPropietario;
import arqui.registro_mascotas.Modelo.Mascota;
import arqui.registro_mascotas.Modelo.Propietario;

/**
 * Gestiona las operaciones y validaciones de mascotas de los RF-01 a RF-06.
 * Exige un propietario registrado al crear o actualizar una mascota.
 * Las reglas de registros clínicos se integrarán al implementar esos módulos.
 */
@Service
public class ServicioMascota {

    private final RepositorioMascotas repositorio;
    private final RepositorioPropietario repositorioPropietario;

    /**
     * Crea el servicio con su dependencia de persistencia.
     *
     * @param repositorio repositorio de mascotas
     * @param repositorioPropietario repositorio de propietarios existentes
     */
    public ServicioMascota(RepositorioMascotas repositorio, RepositorioPropietario repositorioPropietario) {
        this.repositorio = repositorio;
        this.repositorioPropietario = repositorioPropietario;
    }

    /**
     * Registra una mascota después de validar sus datos (RF-01 y RF-06).
     *
     * @param mascota datos de la nueva mascota
     * @return mascota almacenada con su identificador
     * @throws IllegalArgumentException si los datos son inválidos, ya tienen ID
     *         o el propietario no está registrado
     */
    @Transactional
    public Mascota registrar(Mascota mascota) {
        Propietario propietario = validarYObtenerPropietario(mascota);
        if (mascota.getId() != 0) {
            throw new IllegalArgumentException("Una mascota nueva no debe tener identificador.");
        }
        mascota.setPropietario(propietario);
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
     * @param datos nuevos valores de nombre, edad, fotografía, propietario y datos clínicos
     * @return mascota actualizada
     * @throws IllegalArgumentException si los datos son inválidos, no existe el ID
     *         o el propietario no está registrado
     */
    @Transactional
    public Mascota actualizar(int id, Mascota datos) {
        Propietario propietario = validarYObtenerPropietario(datos);
        Mascota mascota = buscar(id);

     actualizarDatosMascota(mascota, datos, propietario);

     return repositorio.save(mascota);
    }

    // Actualiza los atributos de la mascota sin cambiar su ID.
    private void actualizarDatosMascota(Mascota mascota, Mascota datos,
       Propietario propietario) {
    mascota.setNombre(datos.getNombre());
    mascota.setEdad(datos.getEdad());
    mascota.setUrlImagen(datos.getUrlImagen());
    mascota.setPropietario(propietario);
    mascota.setEspecie(datos.getEspecie());
    mascota.setRaza(datos.getRaza());
    mascota.setSexo(datos.getSexo());
    mascota.setPeso(datos.getPeso());
    }

    /**
     * Elimina una mascota después de comprobar su existencia (RF-04).
     *
     * @param id identificador de la mascota
     * @throws IllegalArgumentException si la mascota no existe
     * @throws IllegalStateException si hay registros relacionados que impiden eliminarla
     */
    @Transactional
    public void eliminar(int id) {
        buscar(id);
        try {
            repositorio.deleteById(id);
            repositorio.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                    "No se puede eliminar la mascota porque tiene registros relacionados.", ex);
        }
    }

    /**
     * Elimina una selección en una transacción (RF-05). Comprueba todos los
     * identificadores antes de eliminar; una selección nula o vacía no hace cambios.
     *
     * @param ids identificadores seleccionados
     * @throws IllegalArgumentException si hay un ID nulo o una mascota inexistente
     * @throws IllegalStateException si hay registros relacionados que impiden la eliminación
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
        try {
            repositorio.deleteAllById(ids);
            repositorio.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                    "No se pueden eliminar las mascotas seleccionadas porque tienen registros relacionados.", ex);
        }
    }

    /**
     * Valida los campos y la existencia del propietario (RF-01, RF-03 y RF-06).
     * El nombre es obligatorio, la edad admite cero y la fotografía es opcional.
     * Si se indica una fotografía, debe ser una URL HTTP o HTTPS con servidor.
     * El peso se expresa en kg, debe ser finito y no negativo; cero indica que no se capturó.
     *
     * @param mascota datos que se validarán
     * @throws IllegalArgumentException si se incumple alguna regla o el propietario
     *         no está registrado
     */
    @Transactional(readOnly = true)
    public void validar(Mascota mascota) {
        validarYObtenerPropietario(mascota);
    }

    private Propietario validarYObtenerPropietario(Mascota mascota) {
        validarDatos(mascota);
        if (mascota.getPropietario() == null || mascota.getPropietario().getId() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un propietario registrado para la mascota.");
        }
        return repositorioPropietario.findById(mascota.getPropietario().getId())
                .orElseThrow(() -> new IllegalArgumentException("El propietario seleccionado no está registrado."));
    }

    private void validarDatos(Mascota mascota) {
        if (mascota == null) {
        throw new IllegalArgumentException("Los datos de la mascota son obligatorios.");
    }
    if (mascota.getNombre() == null || mascota.getNombre().isBlank()) {
        throw new IllegalArgumentException("El nombre de la mascota no puede estar vacío.");
    }
    if (mascota.getEdad() < 0) {
        throw new IllegalArgumentException("La edad de la mascota no puede ser negativa.");
    }
    if (!Double.isFinite(mascota.getPeso()) || mascota.getPeso() < 0) {
        throw new IllegalArgumentException("El peso de la mascota debe ser un número finito mayor o igual a cero.");
    }

    validarUrlImagen(mascota.getUrlImagen());
}

    // Valida la URL de la fotografía.
    private void validarUrlImagen(String urlImagen) {
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
