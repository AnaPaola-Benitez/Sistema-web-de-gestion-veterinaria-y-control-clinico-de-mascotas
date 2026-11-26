package arqui.registro_mascotas.Servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import arqui.registro_mascotas.BD.RepositorioMascotas;
import arqui.registro_mascotas.BD.RepositorioPropietario;
import arqui.registro_mascotas.Modelo.Mascota;

@ExtendWith(MockitoExtension.class)
class ServicioMascotaEliminacionMultipleTest {

    @Mock
    private RepositorioMascotas repositorioMascotas;

    @Mock
    private RepositorioPropietario repositorioPropietario;

    @InjectMocks
    private ServicioMascota servicio;

    private Mascota mascota1;
    private Mascota mascota2;

    @BeforeEach
    void prepararDatos() {
        mascota1 = new Mascota();
        mascota1.setId(1);
        mascota1.setNombre("Luna");

        mascota2 = new Mascota();
        mascota2.setId(2);
        mascota2.setNombre("Max");
    }

    // Comprueba que se puedan eliminar varias mascotas existentes.
    @Test
    void eliminarVariasMascotasExistentes() {
        List<Integer> ids = List.of(1, 2);

        when(repositorioMascotas.findById(1))
                .thenReturn(Optional.of(mascota1));

        when(repositorioMascotas.findById(2))
                .thenReturn(Optional.of(mascota2));

        assertDoesNotThrow(() -> servicio.eliminarMultiples(ids));

        verify(repositorioMascotas).deleteAllById(ids);
        verify(repositorioMascotas).flush();
        verifyNoInteractions(repositorioPropietario);
    }

    // Comprueba que una lista vacía no elimine registros.
    @Test
    void eliminarConListaVacia() {
        assertDoesNotThrow(
                () -> servicio.eliminarMultiples(List.of()));

        verifyNoInteractions(repositorioMascotas);
        verifyNoInteractions(repositorioPropietario);
    }

    // Comprueba que una lista nula no elimine registros.
    @Test
    void eliminarConListaNula() {
        assertDoesNotThrow(
                () -> servicio.eliminarMultiples(null));

        verifyNoInteractions(repositorioMascotas);
        verifyNoInteractions(repositorioPropietario);
    }

    // Si una mascota no existe, no se debe eliminar ninguna.
    @Test
    void eliminarConMascotaInexistente() {
        List<Integer> ids = List.of(1, 99);

        when(repositorioMascotas.findById(1))
                .thenReturn(Optional.of(mascota1));

        when(repositorioMascotas.findById(99))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> servicio.eliminarMultiples(ids));

        verify(repositorioMascotas, never()).deleteAllById(any());
        verify(repositorioMascotas, never()).flush();
    }

    // Comprueba el manejo del error cuando hay registros relacionados.
    @Test
    void eliminarMascotasConRegistrosRelacionados() {
        List<Integer> ids = List.of(1, 2);

        when(repositorioMascotas.findById(1))
                .thenReturn(Optional.of(mascota1));

        when(repositorioMascotas.findById(2))
                .thenReturn(Optional.of(mascota2));

        doThrow(new DataIntegrityViolationException(
                "Existen registros relacionados"))
                .when(repositorioMascotas).flush();

        assertThrows(IllegalStateException.class,
                () -> servicio.eliminarMultiples(ids));

        verify(repositorioMascotas).deleteAllById(ids);
        verify(repositorioMascotas).flush();
    }
}