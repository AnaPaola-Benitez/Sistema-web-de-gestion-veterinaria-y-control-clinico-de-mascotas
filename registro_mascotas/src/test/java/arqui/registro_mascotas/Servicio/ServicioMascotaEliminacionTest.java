package arqui.registro_mascotas.Servicio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import arqui.registro_mascotas.BD.RepositorioMascotas;
import arqui.registro_mascotas.BD.RepositorioPropietario;
import arqui.registro_mascotas.Modelo.Mascota;
import arqui.registro_mascotas.Modelo.Propietario;

/** Casos normal, límite y de error de la eliminación individual: RF-04. */
@ExtendWith(MockitoExtension.class)
class ServicioMascotaEliminacionTest {

    @Mock
    private RepositorioMascotas repositorioMascotas;

    @Mock
    private RepositorioPropietario repositorioPropietario;

    @InjectMocks
    private ServicioMascota servicio;

    private Mascota existente;
    private Propietario propietario;

    @BeforeEach
    void prepararDatosNuevos() {
        propietario = new Propietario();
        propietario.setId(7);
        propietario.setNombre("Ana");

        existente = new Mascota();
        existente.setId(25);
        existente.setNombre("Luna");
        existente.setEdad(3);
        existente.setPropietario(propietario);
    }

    @Test
    void eliminacionValida_borraLaMascotaSinModificarSuPropietario() {
        when(repositorioMascotas.findById(25)).thenReturn(Optional.of(existente));

        assertDoesNotThrow(() -> servicio.eliminar(25));

        verify(repositorioMascotas).deleteById(25);
        verify(repositorioMascotas).flush();
        verifyNoInteractions(repositorioPropietario);
        assertSame(propietario, existente.getPropietario());
        assertTrue(propietario.isActivo());
    }

    @Test
    void ultimoRegistroExistente_permiteDejarVacioElRepositorioSimulado() {
        // Estado local del mock: solo existe una mascota al comenzar esta prueba.
        Map<Integer, Mascota> registros = new HashMap<>();
        registros.put(25, existente);
        when(repositorioMascotas.findById(25))
                .thenAnswer(invocacion -> Optional.ofNullable(registros.get(25)));
        doAnswer(invocacion -> {
            registros.remove(invocacion.<Integer>getArgument(0));
            return null;
        }).when(repositorioMascotas).deleteById(25);
        assertEquals(1, registros.size());

        assertDoesNotThrow(() -> servicio.eliminar(25));

        assertTrue(registros.isEmpty());
        verify(repositorioMascotas).deleteById(25);
        verify(repositorioMascotas).flush();
        verifyNoInteractions(repositorioPropietario);
        assertTrue(propietario.isActivo());
    }

    @Test
    void registroInexistente_lanzaExcepcionSinEliminarNiConfirmarCambios() {
        when(repositorioMascotas.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.eliminar(999));

        assertTrue(error.getMessage().contains("inexistente"));
        verify(repositorioMascotas, never()).deleteById(anyInt());
        verify(repositorioMascotas, never()).flush();
        verifyNoInteractions(repositorioPropietario);
    }
}
