package unex.repository;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import unex.model.Destino;

public class DestinoRepository {
    private final MongoCollection<Document> destinoCollection;

    public DestinoRepository(MongoCollection<Document> destinoCollection) {
        this.destinoCollection = destinoCollection;
    }

    public void insertDestino(Destino destino) {
        Document doc = new Document("destino_id", destino.getDestino_id())
                .append("nombre", destino.getNombre())
                .append("pais", destino.getPais())
                .append("descripcion", destino.getDescripcion())
                .append("clima", destino.getClima());

        destinoCollection.insertOne(doc);
    }

    // Agrega métodos para otras operaciones como buscar por ID, actualizar, eliminar, etc.
}

