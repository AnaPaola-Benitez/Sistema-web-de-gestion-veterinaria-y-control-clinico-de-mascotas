package arqui.registro_mascotas.Controlador;

import java.util.List;
import java.beans.PropertyEditorSupport;

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

/*
 * El controlador es la clase que se encarga de asignar peticiones http a los diferentes urls
 * ControladorMascotas implementa el crud para añadir mascotas a la base de datos
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

    // Mostrar lista de mascotas
    @GetMapping("")
    public String MostrarListaDeMascotas(Model modelo) 
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
        Mascota registrada = servicio.registrar(mascota);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Se registró la mascota " + registrada.getNombre() + " con ID " + registrada.getId() + " correctamente.");
        return "redirect:/mascotas";
    }

    // Mostrar formulario para editar una mascota existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEditarMascota(@PathVariable("id") int id, Model modelo) {
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
        servicio.actualizar(id, mascota);
        redirectAttributes.addFlashAttribute("mensajeExito",
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
        servicio.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "La mascota con ID " + id + " se eliminó correctamente.");
        return "redirect:/mascotas";
    }
    
    // Eliminar multiples mascotas
    @PostMapping("/eliminar-multiples")
    public String eliminarMultiples(@RequestParam(value = "mascotasIds", required = false) List<Integer> mascotasIds,
            RedirectAttributes redirectAttributes) {
        servicio.eliminarMultiples(mascotasIds);
        if (mascotasIds == null || mascotasIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeAviso", "No se seleccionaron mascotas para eliminar.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito", "Se eliminaron las mascotas seleccionadas correctamente.");
        }
        return "redirect:/mascotas";
    }
}
