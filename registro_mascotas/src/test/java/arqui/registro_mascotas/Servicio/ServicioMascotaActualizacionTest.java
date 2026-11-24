package arqui.registro_mascotas.Servicio;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

/** Casos normal, límite y de error de la actualización: RF-03 y RF-06. */
@ExtendWith(MockitoExtension.class)
class ServicioMascotaActualizacionTest {

    @Mock
    private RepositorioMascotas repositorioMascotas;

    @Mock
    private RepositorioPropietario repositorioPropietario;

    @InjectMocks
    private ServicioMascota servicio;

    private Mascota existente;
    private Mascota cambios;
    private Propietario propietarioRegistrado;

    @BeforeEach
    void prepararDatosNuevos() {
        Propietario propietarioAnterior = new Propietario();
        propietarioAnterior.setId(3);
        propietarioAnterior.setNombre("Luis");

        existente = new Mascota();
        existente.setId(25);
        existente.setNombre("Luna");
        existente.setEdad(3);
        existente.setEspecie("Canina");
        existente.setRaza("Mestiza");
        existente.setSexo("Hembra");
        existente.setPeso(12.5);
        existente.setPropietario(propietarioAnterior);

        propietarioRegistrado = new Propietario();
        propietarioRegistrado.setId(7);
        propietarioRegistrado.setNombre("Ana");

        Propietario referencia = new Propietario();
        referencia.setId(7);

        cambios = new Mascota();
        cambios.setNombre("Sol");
        cambios.setEdad(4);
        cambios.setUrlImagen("https://example.com/sol.jpg");
        cambios.setEspecie("Felina");
        cambios.setRaza("Siamesa");
        cambios.setSexo("Macho");
        cambios.setPeso(4.2);
        cambios.setPropietario(referencia);
    }

    @Test
    void actualizacionValida_cambiaLosDatosYConservaElIdentificador() {
        when(repositorioPropietario.findById(7)).thenReturn(Optional.of(propietarioRegistrado));
        when(repositorioMascotas.findById(25)).thenReturn(Optional.of(existente));
        when(repositorioMascotas.save(existente)).thenReturn(existente);

        Mascota resultado = servicio.actualizar(25, cambios);

        assertAll(
                () -> assertEquals(25, resultado.getId()),
                () -> assertEquals("Sol", resultado.getNombre()),
                () -> assertEquals(4, resultado.getEdad()),
                () -> assertEquals("https://example.com/sol.jpg", resultado.getUrlImagen()),
                () -> assertEquals("Felina", resultado.getEspecie()),
                () -> assertEquals("Siamesa", resultado.getRaza()),
                () -> assertEquals("Macho", resultado.getSexo()),
                () -> assertEquals(4.2, resultado.getPeso()),
                () -> assertSame(propietarioRegistrado, resultado.getPropietario()));
        verify(repositorioMascotas).save(existente);
        verify(repositorioPropietario, never()).save(any(Propietario.class));
    }

    @Test
    void edadCero_permiteActualizarMascotaExistente() {
        cambios.setEdad(0);
        when(repositorioPropietario.findById(7)).thenReturn(Optional.of(propietarioRegistrado));
        when(repositorioMascotas.findById(25)).thenReturn(Optional.of(existente));
        when(repositorioMascotas.save(existente)).thenReturn(existente);

        Mascota resultado = servicio.actualizar(25, cambios);

        assertEquals(0, resultado.getEdad());
        assertEquals(25, resultado.getId());
        assertSame(propietarioRegistrado, resultado.getPropietario());
        verify(repositorioMascotas).save(existente);
    }

    @Test
    void mascotaInexistente_lanzaExcepcionSinGuardarCambios() {
        when(repositorioPropietario.findById(7)).thenReturn(Optional.of(propietarioRegistrado));
        when(repositorioMascotas.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizar(999, cambios));

        assertTrue(error.getMessage().contains("inexistente"));
        verify(repositorioMascotas, never()).save(any(Mascota.class));
    }
}
