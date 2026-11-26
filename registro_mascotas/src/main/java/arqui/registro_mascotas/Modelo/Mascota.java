package arqui.registro_mascotas.Modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table (name = "mascotas")
public class Mascota 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private int edad;

    @Column(length = 500)
    private String urlImagen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "propietario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_mascotas_propietario"))
    private Propietario propietario;

    private String especie;
    private String raza;
    private String sexo;

    @Column(nullable = false)
    @ColumnDefault("0")
    private double peso;

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

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }
}
