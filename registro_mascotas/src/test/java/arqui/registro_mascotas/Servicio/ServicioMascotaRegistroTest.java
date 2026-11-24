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

/** Casos normal, límite y de error del registro: RF-01 y RF-06. */
@ExtendWith(MockitoExtension.class)
class ServicioMascotaRegistroTest {

    @Mock
    private RepositorioMascotas repositorioMascotas;

    @Mock
    private RepositorioPropietario repositorioPropietario;

    @InjectMocks
    private ServicioMascota servicio;

    private Propietario propietarioRegistrado;
    private Mascota datos;

    @BeforeEach
    void prepararDatosNuevos() {
        propietarioRegistrado = new Propietario();
        propietarioRegistrado.setId(7);
        propietarioRegistrado.setNombre("Ana");

        Propietario referencia = new Propietario();
        referencia.setId(7);

        datos = new Mascota();
        datos.setNombre("Luna");
        datos.setEdad(3);
        datos.setUrlImagen("https://example.com/luna.jpg");
        datos.setEspecie("Canina");
        datos.setRaza("Mestiza");
        datos.setSexo("Hembra");
        datos.setPeso(12.5);
        datos.setPropietario(referencia);
    }

    @Test
    void registroValido_guardaMascotaConIdentificadorYPropietarioExistente() {
        when(repositorioPropietario.findById(7)).thenReturn(Optional.of(propietarioRegistrado));
        when(repositorioMascotas.save(datos)).thenAnswer(invocacion -> {
            Mascota guardada = invocacion.getArgument(0);
            guardada.setId(25);
            return guardada;
        });

        Mascota resultado = servicio.registrar(datos);

        assertAll(
                () -> assertEquals(25, resultado.getId()),
                () -> assertEquals("Luna", resultado.getNombre()),
                () -> assertEquals(3, resultado.getEdad()),
                () -> assertEquals("https://example.com/luna.jpg", resultado.getUrlImagen()),
                () -> assertEquals("Canina", resultado.getEspecie()),
                () -> assertEquals("Mestiza", resultado.getRaza()),
                () -> assertEquals("Hembra", resultado.getSexo()),
                () -> assertEquals(12.5, resultado.getPeso()),
                () -> assertSame(propietarioRegistrado, resultado.getPropietario()));
        verify(repositorioMascotas).save(datos);
        verify(repositorioPropietario, never()).save(any(Propietario.class));
    }

    @Test
    void edadCero_permiteRegistrarMascota() {
        datos.setEdad(0);
        when(repositorioPropietario.findById(7)).thenReturn(Optional.of(propietarioRegistrado));
        when(repositorioMascotas.save(datos)).thenReturn(datos);

        Mascota resultado = servicio.registrar(datos);

        assertEquals(0, resultado.getEdad());
        assertSame(propietarioRegistrado, resultado.getPropietario());
        verify(repositorioMascotas).save(datos);
    }

    @Test
    void edadNegativa_lanzaExcepcionSinGuardarMascota() {
        datos.setEdad(-1);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar(datos));

        assertTrue(error.getMessage().contains("edad"));
        assertTrue(error.getMessage().contains("negativa"));
        verify(repositorioMascotas, never()).save(any(Mascota.class));
        assertEquals(0, datos.getId());
    }
}
