package arqui.registro_mascotas.Modelo;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

/**
 * Registro de aplicación de vacuna y programación de próximo refuerzo.
 */
@Entity
@Table(name = "registros_vacunacion")
public class RegistroVacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mascota_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vacuna_mascota"))
    private Mascota mascota;

    @Column(nullable = false, length = 120)
    private String vacuna;

    @Column(name = "numero_dosis", nullable = false)
    private int numeroDosis;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_refuerzo")
    private LocalDate fechaRefuerzo;

    public RegistroVacunacion() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public String getVacuna() {
        return vacuna;
    }

    public void setVacuna(String vacuna) {
        this.vacuna = vacuna;
    }

    public int getNumeroDosis() {
        return numeroDosis;
    }

    public void setNumeroDosis(int numeroDosis) {
        this.numeroDosis = numeroDosis;
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public LocalDate getFechaRefuerzo() {
        return fechaRefuerzo;
    }

    public void setFechaRefuerzo(LocalDate fechaRefuerzo) {
        this.fechaRefuerzo = fechaRefuerzo;
    }

    public boolean tieneRefuerzoPendiente(LocalDate fecha) {
        return fechaRefuerzo != null && fecha != null && !fechaRefuerzo.isAfter(fecha);
    }
}
