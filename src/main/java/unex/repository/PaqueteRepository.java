package unex.repository;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import unex.model.Paquete;

public class PaqueteRepository {
    private final MongoCollection<Document> paqueteCollection;

    public PaqueteRepository(MongoCollection<Document> paqueteCollection) {
        this.paqueteCollection = paqueteCollection;
    }

    public void insertPaquete(Paquete paquete) {
        Document doc = new Document("paquete_id", paquete.getPaquete_id())
                .append("nombre", paquete.getNombre())
                .append("destino_id", paquete.getDestino_id())
                .append("duracion", paquete.getDuracion())
                .append("precio", paquete.getPrecio());

        paqueteCollection.insertOne(doc);
    }

    // Agrega métodos para otras operaciones como buscar por ID, actualizar, eliminar, etc.
}

