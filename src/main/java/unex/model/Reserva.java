package unex.model;

import java.time.LocalDate;
import java.util.UUID;

public class Reserva{
    private UUID reserva_id;
    private UUID paquete_id;
    private UUID cliente_id;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private boolean pagado;

    public Reserva(UUID reserva_id, UUID paquete_id, UUID cliente_id, LocalDate fecha_inicio, LocalDate fecha_fin, boolean pagado) {
        this.reserva_id = reserva_id;
        this.paquete_id = paquete_id;
        this.cliente_id = cliente_id;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.pagado = pagado;
    }

    public Reserva(UUID paquete_id, UUID cliente_id, LocalDate fecha_inicio, LocalDate fecha_fin, boolean pagado) {
        reserva_id = UUID.randomUUID();
        this.paquete_id = paquete_id;
        this.cliente_id = cliente_id;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.pagado = pagado;
    }

    public LocalDate getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(LocalDate fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public LocalDate getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(LocalDate fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public UUID getReserva_id() {
        return reserva_id;
    }

    public UUID getPaquete_id() {
        return paquete_id;
    }

    public UUID getCliente_id() {
        return cliente_id;
    }
}
