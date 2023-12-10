package unex.repository;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import unex.model.Cliente;

public class ClienteRepository {
    private final MongoCollection<Document> clienteCollection;

    public ClienteRepository(MongoCollection<Document> clienteCollection) {
        this.clienteCollection = clienteCollection;
    }

    public void insertCliente(Cliente cliente) {
        Document doc = new Document("cliente_id", cliente.getCliente_id())
                .append("nombre", cliente.getNombre())
                .append("correo_electronico", cliente.getCorreo_electronico())
                .append("telefono", cliente.getTelefono());

        clienteCollection.insertOne(doc);
    }

    // Agrega métodos para otras operaciones como buscar por ID, actualizar, eliminar, etc.
}

