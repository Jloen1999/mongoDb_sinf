package unex.model;

import java.util.UUID;

// Clase para la entidad Destino
public class Destino{
    private UUID destino_id;
    private String nombre;
    private String pais;
    private String descripcion;
    private String clima;

    public Destino(UUID destino_id, String nombre, String pais, String descripcion, String clima) {
        this.destino_id = destino_id;
        this.nombre = nombre;
        this.pais = pais;
        this.descripcion = descripcion;
        this.clima = clima;
    }

    public Destino(String nombre, String pais, String descripcion, String clima) {
        destino_id = UUID.randomUUID();
        this.nombre = nombre;
        this.pais = pais;
        this.descripcion = descripcion;
        this.clima = clima;
    }

   public Destino(){
        destino_id = UUID.randomUUID();
   }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getClima() {
        return clima;
    }

    public void setClima(String clima) {
        this.clima = clima;
    }

    public UUID getDestino_id() {
        return destino_id;
    }
}

