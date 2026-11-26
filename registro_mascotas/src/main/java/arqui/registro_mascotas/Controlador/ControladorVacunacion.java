package arqui.registro_mascotas.Controlador;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import arqui.registro_mascotas.Modelo.RegistroVacunacion;
import arqui.registro_mascotas.Servicio.ServicioMascota;
import arqui.registro_mascotas.Servicio.ServicioVacunacion;

/**
 * Controlador web para el control de vacunación y refuerzos pendientes (RF-18 a RF-20).
 */
@Controller
@RequestMapping("/vacunacion")
public class ControladorVacunacion {

    private final ServicioVacunacion servicio;
    private final ServicioMascota servicioMascota;

    public ControladorVacunacion(ServicioVacunacion servicio, ServicioMascota servicioMascota) {
        this.servicio = servicio;
        this.servicioMascota = servicioMascota;
    }

    @GetMapping("/nueva")
    public String formularioNueva(@RequestParam(required = false) Integer mascotaId, Model modelo) {
        RegistroVacunacion registro = new RegistroVacunacion();
        if (mascotaId != null) {
            registro.setMascota(servicioMascota.buscar(mascotaId));
        }
        modelo.addAttribute("registro", registro);
        modelo.addAttribute("mascotas", servicioMascota.listar());
        return "vacunacion/nueva_vacuna";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute RegistroVacunacion datos, Model modelo) {
        try {
            RegistroVacunacion guardado = servicio.registrar(datos);
            return "redirect:/vacunacion/mascota/" + guardado.getMascota().getId() + "?exito=Vacuna+registrada";
        } catch (IllegalArgumentException e) {
            modelo.addAttribute("error", e.getMessage());
            modelo.addAttribute("registro", datos);
            modelo.addAttribute("mascotas", servicioMascota.listar());
            return "vacunacion/nueva_vacuna";
        }
    }

    @GetMapping("/mascota/{mascotaId}")
    public String consultar(@PathVariable int mascotaId, Model modelo) {
        modelo.addAttribute("mascota", servicioMascota.buscar(mascotaId));
        modelo.addAttribute("vacunas", servicio.consultar(mascotaId));
        modelo.addAttribute("hoy", LocalDate.now());
        return "vacunacion/index";
    }

    @GetMapping("/pendientes")
    public String pendientes(Model modelo) {
        modelo.addAttribute("vacunas", servicio.pendientes(LocalDate.now()));
        modelo.addAttribute("hoy", LocalDate.now());
        return "vacunacion/pendientes";
    }
}
