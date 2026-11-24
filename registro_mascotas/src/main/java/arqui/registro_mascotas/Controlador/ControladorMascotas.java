package arqui.registro_mascotas.Controlador;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String guardarMascota(@ModelAttribute("mascota") Mascota mascota)
    {
        servicio.registrar(mascota);
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
    public String actualizarMascota(@PathVariable("id") int id, @ModelAttribute("mascota") Mascota mascota)
    {
        servicio.actualizar(id, mascota);

        return "redirect:/mascotas";
    }

    // Eliminar una mascota
    @GetMapping("/eliminar/{id}")
    public String eliminarMascota(@PathVariable("id") int id) {
        servicio.eliminar(id);
        return "redirect:/mascotas";
    }
    
    // Eliminar multiples mascotas
    @PostMapping("/eliminar-multiples")
    public String eliminarMultiples(@RequestParam(value = "mascotasIds", required = false) List<Integer> mascotasIds) {
        servicio.eliminarMultiples(mascotasIds);
        return "redirect:/mascotas";
    }
}
