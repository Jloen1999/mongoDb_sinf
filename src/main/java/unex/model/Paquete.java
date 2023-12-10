package unex.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Paquete{
    private UUID paquete_id;
    private String nombre;
    private UUID destino_id;
    private int duracion;
    private BigDecimal precio;

    public Paquete(UUID paquete_id, String nombre, UUID destino_id, int duracion, BigDecimal precio) {
        this.paquete_id = paquete_id;
        this.nombre = nombre;
        this.destino_id = destino_id;
        this.duracion = duracion;
        this.precio = precio;
    }

    public Paquete(String nombre, UUID destino_id, int duracion, BigDecimal precio) {
        paquete_id = UUID.randomUUID();
        this.nombre = nombre;
        this.duracion = duracion;
        this.precio = precio;
        this.destino_id = destino_id;
    }

    public Paquete() {
        paquete_id = UUID.randomUUID();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public UUID getPaquete_id() {
        return paquete_id;
    }

    public UUID getDestino_id() {
        return destino_id;
    }
}
