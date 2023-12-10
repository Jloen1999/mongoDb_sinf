package unex.model;

import java.util.UUID;

public class Cliente{
    private UUID cliente_id;
    private String nombre;
    private String correo_electronico;
    private String telefono;

    public Cliente(UUID cliente_id, String nombre, String correo_electronico, String telefono) {
        this.cliente_id = cliente_id;
        this.nombre = nombre;
        this.correo_electronico = correo_electronico;
        this.telefono = telefono;
    }

    public Cliente(String nombre, String correo_electronico, String telefono) {
        cliente_id = UUID.randomUUID();
        this.nombre = nombre;
        this.correo_electronico = correo_electronico;
        this.telefono = telefono;
    }

    public Cliente() {
        cliente_id = UUID.randomUUID();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo_electronico() {
        return correo_electronico;
    }

    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public UUID getCliente_id() {
        return cliente_id;
    }
}
