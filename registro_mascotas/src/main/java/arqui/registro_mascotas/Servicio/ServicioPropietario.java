package arqui.registro_mascotas.Servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arqui.registro_mascotas.BD.RepositorioPropietario;
import arqui.registro_mascotas.Modelo.Propietario;

/** Consulta los propietarios disponibles para vincular las mascotas. */
@Service
public class ServicioPropietario {

    private final RepositorioPropietario repositorio;

    /**
     * Crea el servicio de consulta de propietarios.
     *
     * @param repositorio acceso a los propietarios registrados
     */
    public ServicioPropietario(RepositorioPropietario repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Lista los propietarios existentes para los formularios de mascotas.
     *
     * @return propietarios registrados, o una lista vacía si no existen
     */
    @Transactional(readOnly = true)
    public List<Propietario> listar() {
        return repositorio.findAll();
    }
}
