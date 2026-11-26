package arqui.registro_mascotas.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import arqui.registro_mascotas.Modelo.AtencionClinica;
import arqui.registro_mascotas.Servicio.ServicioHistorialMedico;
import arqui.registro_mascotas.Servicio.ServicioMascota;

/**
 * Controlador web para el historial médico de las mascotas (RF-16 y RF-17).
 */
@Controller
@RequestMapping("/historial")
public class ControladorHistorialMedico {

    private final ServicioHistorialMedico servicio;
    private final ServicioMascota servicioMascota;

    public ControladorHistorialMedico(ServicioHistorialMedico servicio, ServicioMascota servicioMascota) {
        this.servicio = servicio;
        this.servicioMascota = servicioMascota;
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(@RequestParam(required = false) Integer mascotaId, Model modelo) {
        AtencionClinica atencion = new AtencionClinica();
        if (mascotaId != null) {
            atencion.setMascota(servicioMascota.buscar(mascotaId));
        }
        modelo.addAttribute("atencion", atencion);
        modelo.addAttribute("mascotas", servicioMascota.listar());
        return "historial/nueva_atencion";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute AtencionClinica datos, Model modelo) {
        try {
            AtencionClinica guardada = servicio.registrar(datos);
            return "redirect:/historial/mascota/" + guardada.getMascota().getId() + "?exito=Atencion+clinica+registrada";
        } catch (IllegalArgumentException e) {
            modelo.addAttribute("error", e.getMessage());
            modelo.addAttribute("atencion", datos);
            modelo.addAttribute("mascotas", servicioMascota.listar());
            return "historial/nueva_atencion";
        }
    }

    @GetMapping("/mascota/{mascotaId}")
    public String consultar(@PathVariable int mascotaId, Model modelo) {
        modelo.addAttribute("mascota", servicioMascota.buscar(mascotaId));
        modelo.addAttribute("historial", servicio.consultar(mascotaId));
        return "historial/index";
    }
}
