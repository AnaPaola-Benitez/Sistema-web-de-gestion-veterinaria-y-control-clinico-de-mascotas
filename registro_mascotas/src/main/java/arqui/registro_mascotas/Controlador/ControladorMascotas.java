package arqui.registro_mascotas.Controlador;

import java.beans.PropertyEditorSupport;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import arqui.registro_mascotas.Modelo.Mascota;
import arqui.registro_mascotas.Servicio.ServicioMascota;
import arqui.registro_mascotas.Servicio.ServicioPropietario;

/**
 * Coordina las vistas y operaciones de mascotas de los RF-01 a RF-06.
 * Expone las seis operaciones del UML y delega las reglas de negocio al servicio.
 * Los métodos HTTP auxiliares conservan las rutas, los mensajes flash y la
 * confirmación de eliminación de la aplicación.
 */

@Controller
@RequestMapping("/mascotas")
public class ControladorMascotas {
    private final ServicioMascota servicio;
    private final ServicioPropietario servicioPropietario;

    public ControladorMascotas(ServicioMascota servicio, ServicioPropietario servicioPropietario) {
        this.servicio = servicio;
        this.servicioPropietario = servicioPropietario;
    }

    // El peso es opcional; una captura vacía mantiene el valor cero del modelo UML.
    @InitBinder("mascota")
    public void configurarFormulario(WebDataBinder binder) {
        binder.registerCustomEditor(double.class, "peso", new PropertyEditorSupport() {
            @Override
            public void setAsText(String texto) {
                setValue(texto == null || texto.isBlank() ? 0.0 : Double.parseDouble(texto));
            }
        });
    }

    /**
     * Lista las mascotas registradas (RF-02).
     *
     * @param modelo datos para la vista
     * @return nombre de la vista del listado
     */
    @GetMapping("")
    public String listar(Model modelo)
    {
        var mascotas = servicio.listar();
        modelo.addAttribute("mascotas", mascotas);
        return "mascotas/index";
    }

    // Mostrar formulario para agregar una nueva mascota
    @GetMapping("/nuevo")
    public String mostrarFormularioDeNuevaMascota(Model modelo)
    {
        modelo.addAttribute("mascota", new Mascota());
        modelo.addAttribute("propietarios", servicioPropietario.listar());
        return "mascotas/nueva_mascota";
    }

    // Guardar una nueva mascota
    @PostMapping("/guardar")
    public String guardarMascota(@ModelAttribute("mascota") Mascota mascota, RedirectAttributes redirectAttributes)
    {
        return registrar(mascota, redirectAttributes);
    }

    /**
     * Registra una mascota validada por el servicio (RF-01 y RF-06).
     *
     * @param datos datos de la mascota y su propietario
     * @param modelo modelo que recibe la confirmación del registro
     * @return redirección al listado
     */
    public String registrar(Mascota datos, Model modelo) {
        Mascota registrada = servicio.registrar(datos);
        agregarMensajeExito(modelo,
                "Se registró la mascota " + registrada.getNombre() + " con ID " + registrada.getId() + " correctamente.");
        return "redirect:/mascotas";
    }

    /**
     * Busca una mascota por ID y muestra sus datos en el formulario actual
     * de consulta y edición (RF-02 y RF-03).
     *
     * @param id identificador de la mascota
     * @param modelo datos para la vista y selección de propietario
     * @return nombre de la vista de edición
     */
    @GetMapping("/editar/{id}")
    public String buscar(@PathVariable("id") int id, Model modelo) {
        var mascota = servicio.buscar(id);
        modelo.addAttribute("mascota", mascota);
        modelo.addAttribute("propietarios", servicioPropietario.listar());
        return "mascotas/editar_mascota";
    }

    // Actualizar una mascota existente
    @PostMapping("/actualizar/{id}")
    public String actualizarMascota(@PathVariable("id") int id, @ModelAttribute("mascota") Mascota mascota,
            RedirectAttributes redirectAttributes)
    {
        return actualizar(id, mascota, redirectAttributes);
    }

    /**
     * Actualiza una mascota existente mediante el servicio (RF-03 y RF-06).
     *
     * @param id identificador de la mascota que se actualizará
     * @param datos nuevos datos de la mascota
     * @param modelo modelo que recibe la confirmación de la actualización
     * @return redirección al listado
     */
    public String actualizar(int id, Mascota datos, Model modelo) {
        servicio.actualizar(id, datos);
        agregarMensajeExito(modelo,
                "La mascota con ID " + id + " se actualizó correctamente.");
        return "redirect:/mascotas";
    }

    // Consultar por GET solo muestra la confirmación; no elimina registros.
    @GetMapping("/eliminar/{id}")
    public String mostrarConfirmacionDeEliminarMascota(@PathVariable("id") int id, Model modelo) {
        modelo.addAttribute("mascota", servicio.buscar(id));
        return "mascotas/confirmar_eliminacion";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable("id") int id,
            @RequestParam(value = "confirmado", defaultValue = "false") boolean confirmado,
            RedirectAttributes redirectAttributes) {
        if (!confirmado) {
            redirectAttributes.addFlashAttribute("mensajeAviso", "La eliminación fue cancelada.");
            return "redirect:/mascotas";
        }
        String destino = eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "La mascota con ID " + id + " se eliminó correctamente.");
        return destino;
    }

    /**
     * Elimina una mascota mediante el servicio (RF-04). El flujo HTTP invoca
     * esta operación después de comprobar la confirmación del usuario.
     *
     * @param id identificador de la mascota
     * @return redirección al listado
     */
    public String eliminar(int id) {
        servicio.eliminar(id);
        return "redirect:/mascotas";
    }
    
    // Eliminar multiples mascotas
    @PostMapping("/eliminar-multiples")
    public String procesarEliminacionMultiple(@RequestParam(value = "mascotasIds", required = false) List<Integer> mascotasIds,
            RedirectAttributes redirectAttributes) {
        String destino = eliminarMultiples(mascotasIds);
        if (mascotasIds == null || mascotasIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeAviso", "No se seleccionaron mascotas para eliminar.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito", "Se eliminaron las mascotas seleccionadas correctamente.");
        }
        return destino;
    }

    /**
     * Elimina las mascotas seleccionadas mediante el servicio (RF-05).
     *
     * @param ids identificadores seleccionados, o una selección vacía
     * @return redirección al listado
     */
    public String eliminarMultiples(List<Integer> ids) {
        servicio.eliminarMultiples(ids);
        return "redirect:/mascotas";
    }

    private void agregarMensajeExito(Model modelo, String mensaje) {
        if (modelo instanceof RedirectAttributes redirectAttributes) {
            redirectAttributes.addFlashAttribute("mensajeExito", mensaje);
        } else {
            modelo.addAttribute("mensajeExito", mensaje);
        }
    }
}
