package arqui.registro_mascotas.Modelo;

import jakarta.persistence.*;

@Entity
@Table (name = "mascotas")
public class Mascota 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String nombre;
    int edad;

    @Column(length = 500)
    String urlImagen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "propietario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_mascotas_propietario"))
    private Propietario propietario;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int getEdad() {
        return edad;
    }
    public void setEdad(int edad) {
        this.edad = edad;
    }
    public String getUrlImagen() {
        return urlImagen;
    }
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }
}
