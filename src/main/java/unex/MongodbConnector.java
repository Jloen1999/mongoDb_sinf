package unex;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.Jsr310CodecProvider;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class MongodbConnector {

    private MongoClient mongoClient;
    private MongoDatabase db;
    public MongodbConnector(){

    }
    /**
     * Configuración de conexión a MongoDB
     */
    public MongoDatabase connect(String host, int port, String database, String username, String password) {
        // Construye la cadena de conexión
        String connectionString = String.format("mongodb://%s:%s@%s:%d", username, password, host, port);

        try {
            this.setMongoClient(MongoClients.create(connectionString));
            System.out.println(ansi().fg(GREEN).a("==>Conexión exitosa a MongoDB").reset());

            // Configura el CodecRegistry con la representación UUID
            CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                    CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
                    CodecRegistries.fromRegistries(
                            CodecRegistries.fromProviders(new UuidCodecProvider(UuidRepresentation.STANDARD)),
                            MongoClientSettings.getDefaultCodecRegistry()
                    )
            );

            // Obtén la base de datos (se creará si no existe)
            MongoDatabase db = mongoClient.getDatabase(database).withCodecRegistry(codecRegistry);

            System.out.println(ansi().fg(GREEN).a("Conexión exitosa a la base de datos: " + database).reset());
            // Crear e insertar datos
            System.out.println(ansi().fg(YELLOW).a("╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╗\n" +
                    ansi().fg(BLUE).a(             "\t\t\t\t\t\t\t\t\tInsertar datos"         ).reset() + "\n" +
                    ansi().fg(YELLOW).a("╠═══════════════════════════════════════════════════════════════════════════════════════════════════════════════╣\n").reset()));

            // Insertar registros
//            InsertarDatos insertarDatos = new InsertarDatos(db);
//            insertarDatos.getDataList(); // Obtener datos de insercion de fichero
//            insertarDatos.insertDataDestinos(50); // Insertar destinos
//            insertarDatos.insertDataPaquetes(100); // Insertar paquetes
//            insertarDatos.insertDataClientes(200); // Insertar clientes
//            insertarDatos.insertDataReservas(500); // Insertar reservas

            // Ejecutar consultas
            QueryExecutator queryExecutator = new QueryExecutator(db);
            queryExecutator.executeQueries();


            System.out.println(ansi().fg(YELLOW).a(("================Datos inicializados================")).reset());

        } catch (Exception e) {
            System.out.println(ansi().fg(RED).a("==>Error al conectar a la base de datos "+e.getMessage()).reset());
        }

        return db;
    }

    public void closeConnection(){
        mongoClient.close();
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoDatabase getDb() {
        return db;
    }

    public void setDb(MongoDatabase db) {
        this.db = db;
    }


}
