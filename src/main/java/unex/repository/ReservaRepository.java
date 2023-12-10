package unex.repository;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import unex.model.Reserva;

public class ReservaRepository {
    private final MongoCollection<Document> reservaCollection;

    public ReservaRepository(MongoCollection<Document> reservaCollection) {
        this.reservaCollection = reservaCollection;
    }

    public void insertReserva(Reserva reserva) {
        Document doc = new Document("reserva_id", reserva.getReserva_id())
                .append("paquete_id", reserva.getPaquete_id())
                .append("cliente_id", reserva.getCliente_id())
                .append("fecha_inicio", reserva.getFecha_inicio())
                .append("fecha_fin", reserva.getFecha_fin())
                .append("pagado", reserva.isPagado());

        reservaCollection.insertOne(doc);
    }

    // Agrega métodos para otras operaciones como buscar por ID, actualizar, eliminar, etc.
}

