package unex;

import com.github.javafaker.Faker;
import com.mongodb.Function;
import com.mongodb.MongoCommandException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.Decimal128;
import unex.model.Cliente;
import unex.model.Destino;
import unex.model.Paquete;
import unex.model.Reserva;
import unex.repository.ClienteRepository;
import unex.repository.DestinoRepository;
import unex.repository.PaqueteRepository;
import unex.repository.ReservaRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class InsertarDatos {
    private MongoDatabase database;
    String nombre = "";
    Faker faker = new Faker(); // Para la generación de datos aleatorios
    List<String> clientes = new ArrayList<>();
    List<String> paquetes = new ArrayList<>();
    List<String> emailDomains = new ArrayList<>();
    List<String> destinos = new ArrayList<>();
    List<String> descripcionDestinos = new ArrayList<>();
    List<String> climas = new ArrayList<>();
    List<String> paises = new ArrayList<>();

    // Obtén las colecciones
    MongoCollection<Document> destinoCollection;
    MongoCollection<Document> paqueteCollection;
    MongoCollection<Document> clienteCollection;
    MongoCollection<Document> reservaCollection;
    MongoCollection<Document> resumenReservasPorPaqueteCollection;
    MongoCollection<Document> tempDestinosCollection;
    MongoCollection<Document> disponibilidadCollection;

    int min = 0, max = 9, range = max - min + 1;

    public InsertarDatos(MongoDatabase db) {
        this.database = db;
        this.destinoCollection = db.getCollection("destinos");
        this.paqueteCollection = db.getCollection("paquetes");
        this.clienteCollection = db.getCollection("clientes");
        this.reservaCollection = db.getCollection("reservas");
        System.out.println(db.getName());
    }

    public void getDataList() {
        File fileData = new File("src/main/resources/data/bd.txt");
        if (fileData.exists()) {
            FileReader fr = null;
            BufferedReader br = null;


            try {
                fr = new FileReader(fileData);
                br = new BufferedReader(fr);
                String linea, newLinea;
                while ((linea = br.readLine()) != null) {
                    if (!linea.isEmpty()) {
                        newLinea = linea.substring(linea.indexOf(':') + 1).trim();
                        switch (linea.substring(0, linea.indexOf(':'))) {
                            case "clientes":
                                clientes = List.of(newLinea.split(",")); // Nombres de clientes
                                break;
                            case "paquetes":
                                paquetes = List.of(newLinea.split(",")); // Nombres de paquetes
                                break;
                            case "climas":
                                climas = List.of(newLinea.split(",")); // Nombres de climas
                                break;
                            case "destinos":
                                destinos = List.of(newLinea.split(",")); // Nombres de destinos
                                break;
                            case "descripcionDestinos":
                                descripcionDestinos = List.of(newLinea.split(",")); // Descripcion de cada uno de los destinos
                                break;
                            case "paises":
                                paises = List.of(newLinea.split(",")); // Nombre de los paises
                                break;
                            case "emailDomains":
                                emailDomains = List.of(newLinea.split(",")); // Nombre de dominios de correo electronico
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }

    public void insertDataDestinos(int numDestinos) {
        // Insertar destinos
        System.out.println(ansi().fg(BLUE).a(("\n\n\t\t\t\t==>TABLA DESTINOS:")).reset());
        for (int i = 0; i < numDestinos; i++) {
            nombre = destinos.get(i);
            String pais = paises.get(i);
            String descripcion = descripcionDestinos.get(i);
            String clima = climas.get((int) (Math.random() * climas.size()));

            Destino destino = new Destino(nombre, pais, descripcion, clima);
            DestinoRepository destinoRepository = new DestinoRepository(destinoCollection);

            // Insertar en la colección de destinos
            destinoRepository.insertDestino(destino);

            System.out.println(ansi().fg(BLUE).a(("===>Destino insertado: " + destino.getDestino_id())).reset());

        }

    }

    public void insertDataClientes(int numClientes) {
        // Insertar clientes
        System.out.println(ansi().fg(BLUE).a(("\n\n\t\t\t\t==>TABLA CLIENTES:")).reset());
        for (int i = 0; i < numClientes; i++) {
            nombre = faker.name().fullName();
            String correoElectronico = faker.internet().emailAddress(nombre).replace(" ", "");
            String telefono = "+34-" + String.valueOf((int) (Math.random() * range) + min) + String.valueOf((int) (Math.random() * range) + min) + String.valueOf((int) (Math.random() * range) + min) + "-" + String.valueOf((int) (Math.random() * range) + min) + String.valueOf((int) (Math.random() * range) + min) + "-" + String.valueOf((int) (Math.random() * range) + min) + String.valueOf((int) (Math.random() * range) + min) + "-" + String.valueOf((int) (Math.random() * range) + min) + String.valueOf((int) (Math.random() * range) + min); // Ajusta según sea necesario

            Cliente cliente = new Cliente(nombre, correoElectronico, telefono);
            ClienteRepository clienteRepository = new ClienteRepository(clienteCollection);

            // Insertar en la colección de clientes
            clienteRepository.insertCliente(cliente);
            System.out.println(ansi().fg(BLUE).a(("===>Cliente insertado: " + cliente.getCliente_id())).reset());

        }

    }

    public void insertDataPaquetes(int numPaquetes) {
        // Obtener todos los destinos existentes
        List<UUID> destinosExistente = getAllDestinoIds();

        // Insertar paquetes
        System.out.println(ansi().fg(BLUE).a(("\n\n\t\t\t\t==>TABLA PAQUETES:")).reset());
        for (int i = 0; i < numPaquetes; i++) {
            nombre = paquetes.get(i);
            UUID destino_id = destinosExistente.get((int) (Math.random() * destinosExistente.size())); // Obtener destino existente de la tabla de destinos
            int duracion = (int) (Math.random() * 10) + 1; // Duración aleatoria entre 1 y 10 días
            BigDecimal precio = BigDecimal.valueOf(Math.round((Math.random() * 1000) * 100.0) / 100.0); // Precio aleatorio;

            Paquete paquete = new Paquete(nombre, destino_id, duracion, precio);
            PaqueteRepository paqueteRepository = new PaqueteRepository(paqueteCollection);

            // Insertar en la colección de paquetes
            paqueteRepository.insertPaquete(paquete);

            System.out.println(ansi().fg(BLUE).a(("===>Paquete insertado: " + paquete.getPaquete_id())).reset());
        }
    }

    public void insertDataReservas(int numReservas) {
        List<UUID> paquetesExistente = getAllPaqueteIds(); // Obtener todos los ID de paquetes existentes
        List<UUID> clientesExistente = getAllClienteIds(); // Obtener todos los ID de clientes existentes

        // Insertar reservas
        System.out.println(ansi().fg(BLUE).a(("\n\n\t\t\t\t==>TABLA RESERVAS:")).reset());
        for (int i = 0; i < numReservas; i++) {
            UUID paquete_id = paquetesExistente.get((int) (Math.random() * paquetesExistente.size())); // Obtener id de paquete existente en la tabla paquetes aleatoriamente
            UUID cliente_id = clientesExistente.get((int) (Math.random() * clientesExistente.size())); // Obtener id de cliente existente en la tabla clientes aleatoriamente
            LocalDate fechaInicio = LocalDate.now().plusDays(i); // Fecha de inicio incremental
            LocalDate fechaFin = fechaInicio.plusDays((int) (Math.random() * 10) + 1); // Duración aleatoria entre 1 y 10 días
            boolean pagado = Math.random() < 0.5; // Pagado aleatorio

            Reserva reserva = new Reserva(paquete_id, cliente_id, fechaInicio, fechaFin, pagado);
            ReservaRepository reservaRepository = new ReservaRepository(reservaCollection);

            // Insertar en la colección de reservas
            reservaRepository.insertReserva(reserva);

            System.out.println(ansi().fg(BLUE).a(("===>Reserva insertada: " + reserva.getReserva_id())).reset());
        }

    }


    public List<UUID> getAllDestinoIds() {
        return getAllIds(destinoCollection, "destino_id");
    }

    public List<UUID> getAllPaqueteIds() {
        return getAllIds(paqueteCollection, "paquete_id");
    }

    public List<UUID> getAllClienteIds() {
        return getAllIds(clienteCollection, "cliente_id");
    }

    public List<UUID> getAllIds(MongoCollection<Document> collection, String idFieldName) {
        List<UUID> idsExistente = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().projection(new Document(idFieldName, 1)).iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Binary binaryUuid = doc.get(idFieldName, Binary.class);
                UUID id = asUuid(binaryUuid.getData());
                idsExistente.add(id);
            }
        } finally {
            cursor.close();
        }

        return idsExistente;
    }

    public static UUID asUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public <T> List<T> getAllEntities(MongoCollection<Document> collection, Function<Document, T> documentToEntityFunction) {
        List<T> entitiesExistente = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                T entity = documentToEntityFunction.apply(doc);
                entitiesExistente.add(entity);
            }
        } finally {
            cursor.close();
        }

        return entitiesExistente;
    }

    public Paquete documentToPaquete(Document doc) {
        Binary binaryUuid = doc.get("paquete_id", Binary.class);
        UUID paquete_id = asUuid(binaryUuid.getData());

        binaryUuid = doc.get("destino_id", Binary.class);
        UUID destino_id = asUuid(binaryUuid.getData());

        return new Paquete(
                paquete_id,
                doc.getString("nombre"),
                destino_id,
                doc.getInteger("duracion"),
                doc.get("precio", Decimal128.class).bigDecimalValue()
        );
    }

    public Cliente documentToCliente(Document doc) {
        Binary binaryUuid = doc.get("cliente_id", Binary.class);
        UUID cliente_id = asUuid(binaryUuid.getData());

        return new Cliente(
                cliente_id,
                doc.getString("nombre"),
                doc.getString("correo_electronico"),
                doc.getString("telefono")
        );
    }

    public Destino documentToDestino(Document doc) {
        Binary binaryUuid = doc.get("destino_id", Binary.class);
        UUID destino_id = asUuid(binaryUuid.getData());

        return new Destino(
                destino_id,
                doc.getString("nombre"),
                doc.getString("pais"),
                doc.getString("descripcion"),
                doc.getString("clima")
        );
    }

    public Reserva documentToReserva(Document doc) {

        Binary binaryUuid = doc.get("reserva_id", Binary.class);
        UUID reserva_id = asUuid(binaryUuid.getData());

        binaryUuid = doc.get("cliente_id", Binary.class);
        UUID cliente_id = asUuid(binaryUuid.getData());

        binaryUuid = doc.get("paquete_id", Binary.class);
        UUID paquete_id = asUuid(binaryUuid.getData());

        Date fechaUtil = doc.get("fecha_inicio", Date.class);
        LocalDate fecha_inicio = convertirFecha(fechaUtil);
        LocalDate fecha_fin = convertirFecha(fechaUtil);

        return new Reserva(
                reserva_id,
                paquete_id,
                cliente_id,
                fecha_inicio,
                fecha_fin,
                doc.getBoolean("pagado")
        );
    }

    public List<Destino> getAllDestinos() {
        return getAllEntities(destinoCollection, this::documentToDestino);
    }

    public void showDestinos(List<Destino> destinos) {
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t|             Registros Destinos       |").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());

        System.out.println(ansi().fg(CYAN).a("|             destino_id            |     nombre     |   pais   |                          descripcion                          |     clima     |").reset());
        System.out.println(ansi().fg(YELLOW).a("+-----------------------------------+----------------+----------+---------------------------------------------------------------+---------------+").reset());

        for (Destino destino : destinos) {

            System.out.println(
                    ansi().fg(GREEN).a("| " + destino.getDestino_id()).reset() + " | " +
                            ansi().fg(GREEN).a(destino.getNombre()).reset() + " | " +
                            ansi().fg(GREEN).a(destino.getPais()).reset() + " | " +
                            ansi().fg(GREEN).a(destino.getDescripcion()).reset() + " | " +
                            ansi().fg(GREEN).a(destino.getClima()).reset() + " |"
            );
        }

        System.out.println(ansi().fg(YELLOW).a("+---------------------------------+----------------+----------+---------------------------------------------------------------+---------------+").reset());
    }

    public List<Cliente> getAllClientes() {
        return getAllEntities(clienteCollection, this::documentToCliente);
    }

    public void showClientes(List<Cliente> clientes) {
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t|           Registros Clientes         |").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());

        System.out.println(ansi().fg(CYAN).a("|             cliente_id             |      nombre      |       correo_electronico      |   telefono   |").reset());
        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+------------------+-------------------------------+--------------+").reset());

        for (Cliente cliente : clientes) {
            System.out.println(
                    ansi().fg(GREEN).a("| " + cliente.getCliente_id()).reset() + " | " +
                            ansi().fg(GREEN).a(cliente.getNombre()).reset() + " | " +
                            ansi().fg(GREEN).a(cliente.getCorreo_electronico()).reset() + " | " +
                            ansi().fg(GREEN).a(cliente.getTelefono()).reset() + " |"
            );
        }

        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+------------------+-------------------------------+--------------+").reset());
    }

    public List<Paquete> getAllPaquetes() {
        return getAllEntities(paqueteCollection, this::documentToPaquete);
    }

    public void showPaquetes(List<Paquete> paquetes) {
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t|           Registros Paquetes         |").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());

        System.out.println(ansi().fg(CYAN).a("|             paquete_id             |       nombre       |             destino_id             | duracion |  precio  |").reset());
        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+--------------------+------------------------------------+----------+----------+").reset());

        for (Paquete paquete : paquetes) {
            System.out.println(
                    ansi().fg(GREEN).a("| " + paquete.getPaquete_id()).reset() + " | " +
                            ansi().fg(GREEN).a(paquete.getNombre()).reset() + " | " +
                            ansi().fg(GREEN).a(paquete.getDestino_id()).reset() + " | " +
                            ansi().fg(GREEN).a(paquete.getDuracion()).reset() + " | " +
                            ansi().fg(GREEN).a(paquete.getPrecio()).reset() + " |"
            );
        }

        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+--------------------+------------------------------------+----------+----------+").reset());
    }

    public List<Reserva> getAllReservas() {
        return getAllEntities(reservaCollection, this::documentToReserva);
    }

    public void showReservas(List<Reserva> reservas) {
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t|           Registros Reservas         |").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());

        System.out.println(ansi().fg(CYAN).a("|             reserva_id             |             paquete_id             |             cliente_id             | fecha_inicio | fecha_fin | pagado |").reset());
        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+------------------------------------+------------------------------------+--------------+-----------+--------+").reset());

        for (Reserva reserva : reservas) {
            System.out.println(
                    ansi().fg(GREEN).a("| " + reserva.getReserva_id()).reset() + " | " +
                            ansi().fg(GREEN).a(reserva.getPaquete_id()).reset() + " | " +
                            ansi().fg(GREEN).a(reserva.getCliente_id()).reset() + " | " +
                            ansi().fg(GREEN).a(reserva.getFecha_inicio()).reset() + " | " +
                            ansi().fg(GREEN).a(reserva.getFecha_fin()).reset() + " | " +
                            ansi().fg(GREEN).a(reserva.isPagado()).reset() + " |"
            );
        }

        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+------------------------------------+------------------------------------+--------------+-----------+--------+").reset());
    }

    public Paquete getPaqueteById(UUID paqueteId) {
        Document query = new Document("paquete_id", paqueteId);
        Document result = paqueteCollection.find(query).first();
        return result != null ? documentToPaquete(result) : null;
    }

    public void showPaqueteById(Paquete paquete) {
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t|          Detalles del Paquete        |").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());

        System.out.println(ansi().fg(CYAN).a("|             paquete_id             |     nombre     |             destino_id             | duracion | precio |").reset());
        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+----------------+------------------------------------+----------+--------+").reset());

        if (paquete != null) {
            System.out.println(
                    ansi().fg(GREEN).a("| " + paquete.getPaquete_id()).reset() + " | " +
                            ansi().fg(GREEN).a(paquete.getNombre()).reset() + " | " +
                            ansi().fg(GREEN).a(paquete.getDestino_id()).reset() + " | " +
                            ansi().fg(GREEN).a(paquete.getDuracion()).reset() + " | " +
                            ansi().fg(GREEN).a(paquete.getPrecio()).reset() + " |"
            );
        }

        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+----------------+------------------------------------+----------+--------+").reset());
    }

    public List<Reserva> getReservasByCliente(UUID clienteId) {
        List<Reserva> reservas = new ArrayList<>();
        FindIterable<Document> results = reservaCollection.find(Filters.eq("cliente_id", clienteId));

        for (Document doc : results) {
            Reserva reserva = documentToReserva(doc);
            reservas.add(reserva);
        }

        return reservas;
    }

    public List<Paquete> getPaquetesByDestino(UUID destinoId) {
        List<Paquete> paquetes = new ArrayList<>();
        FindIterable<Document> results = paqueteCollection.find(Filters.eq("destino_id", destinoId));

        for (Document doc : results) {
            Paquete paquete = documentToPaquete(doc);
            paquetes.add(paquete);
        }

        return paquetes;
    }

    public List<Cliente> getClientesByFechaReserva(Instant fechaInicio, Instant fechaFin) {
        List<Cliente> clientes = new ArrayList<>();

        // Filtra las reservas que tienen fecha de inicio o fin dentro del rango especificado
        FindIterable<Document> results = reservaCollection.find(
                Filters.and(
                        Filters.gte("fecha_inicio", fechaInicio),
                        Filters.lte("fecha_inicio", fechaFin)
                )
        );

        // Obtiene los IDs de los clientes asociados a las reservas encontradas
        Set<UUID> clienteIds = new HashSet<>();
        for (Document doc : results) {
            Binary binaryUuid = doc.get("cliente_id", Binary.class);
            UUID cliente_id = asUuid(binaryUuid.getData());
            clienteIds.add(cliente_id);
        }

        // Busca los clientes que tienen reservas en el rango de fechas
        for (UUID clienteId : clienteIds) {
            Document clienteDoc = clienteCollection.find(Filters.eq("cliente_id", clienteId)).first();
            if (clienteDoc != null) {
                Cliente cliente = documentToCliente(clienteDoc);
                clientes.add(cliente);
            }
        }

        return clientes;
    }

    public static LocalDate convertirFecha(Date fecha) {
        Instant instant = fecha.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void createIndexForPaquetesByName() {
        // Crear índice para el campo "nombre" en la colección de paquetes
        paqueteCollection.createIndex(Indexes.ascending("nombre"));
        System.out.println(ansi().fg(GREEN).a("\t\t\t\t\t--->Índice generado correctamente").reset());
    }

    public void createResumenReservasPorPaqueteTable() {
        resumenReservasPorPaqueteCollection = database.getCollection("resumenReservasPorPaquete");
        System.out.println(ansi().fg(GREEN).a("\t\t\t\t\t--->Tabla creada correctamente").reset());
    }

    public void updateResumenReservasPorPaquete() {
        // Obtener todas las reservas
        for (Document reservaDoc : reservaCollection.find()) {
            Binary binaryUuid = reservaDoc.get("paquete_id", Binary.class);
            UUID paqueteId = asUuid(binaryUuid.getData());

            // Contar las reservas para el paquete actual
            long totalReservas = reservaCollection.countDocuments(new Document("paquete_id", paqueteId));

            // Actualizar la colección resumen_reservas_por_paquete
            Document resumenDoc = new Document("paquete_id", paqueteId)
                    .append("total_reservas", totalReservas);

            // Reemplazar el documento existente o insertar uno nuevo
            resumenReservasPorPaqueteCollection.replaceOne(new Document("paquete_id", paqueteId), resumenDoc, new ReplaceOptions().upsert(true));
        }

        System.out.println("Colección resumen_reservas_por_paquete actualizada con registros");
    }

    public void getResumenReservasPorPaqueteCollection() {
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t| Resumen Total de Reservas por paquete |").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());

        System.out.println(ansi().fg(CYAN).a("|             paquete_id             | total_reservas |").reset());
        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+----------------+").reset());
        for (Document resumenDoc : resumenReservasPorPaqueteCollection.find()) {
            Binary binaryUuid = resumenDoc.get("paquete_id", Binary.class);
            UUID paqueteId = asUuid(binaryUuid.getData());
            long totalReservas = resumenDoc.getLong("total_reservas");

            System.out.println(ansi().fg(GREEN).a("| " + paqueteId + "|\t\t" + totalReservas).reset() + "\t\t|");
        }
        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+----------------+").reset());
    }

    public void getAllClientesByDestinoClima(String clima) {
        // Crear un índice en la colección de destinos basado en el campo "clima"
        destinoCollection.createIndex(Indexes.ascending("clima"));


        AggregateIterable<Document> aggregate = reservaCollection.aggregate(Arrays.asList(
                new Document("$lookup", new Document()
                        .append("from", "paquetes")
                        .append("localField", "paquete_id")
                        .append("foreignField", "paquete_id")
                        .append("as", "paquete")),
                new Document("$unwind", "$paquete"),
                new Document("$lookup", new Document()
                        .append("from", "destinos")
                        .append("localField", "paquete.destino_id")
                        .append("foreignField", "destino_id")
                        .append("as", "destino")),
                new Document("$unwind", "$destino"),
                new Document("$match", new Document("destino.clima", clima)),
                new Document("$lookup", new Document()
                        .append("from", "clientes")
                        .append("localField", "cliente_id")
                        .append("foreignField", "cliente_id")
                        .append("as", "cliente")),
                new Document("$unwind", "$cliente")
        ));

        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t|         Clientes por Clima           |").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());
        System.out.println(ansi().fg(CYAN).a("|             cliente_id             |      nombre      | correo_electronico |  telefono  |").reset());
        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+------------------+--------------------+------------+").reset());

        for (Document document : aggregate) {

            Document clienteDoc = (Document) document.get("cliente");
            Binary binaryUuid = document.get("cliente_id", Binary.class);
            UUID cliente_id = asUuid(binaryUuid.getData());

            System.out.println(
                    ansi().fg(GREEN).a("| " + cliente_id).reset() + " | " +
                            ansi().fg(GREEN).a(clienteDoc.getString("nombre")).reset() + " | " +
                            ansi().fg(GREEN).a(clienteDoc.getString("correo_electronico")).reset() + " | " +
                            ansi().fg(GREEN).a(clienteDoc.getString("telefono")).reset() + " |"
            );
            System.out.println("+------------------------------------+------------------+--------------------+------------+");
        }

    }

    public void crearIndiceReservasPorClienteYFechas() {
        // Especificar las claves del índice compuesto
        Document keys = new Document();
        keys.put("cliente_id", 1);  // 1 para orden ascendente
        keys.put("fecha_inicio", 1); // 1 para orden ascendente

        // Opciones del índice
        IndexOptions indexOptions = new IndexOptions().name("reservas_cliente_fecha_index");

        // Crear el índice compuesto
        reservaCollection.createIndex(keys, indexOptions);

        System.out.println(ansi().fg(GREEN).a("Índice creado con éxito para optimizar la búsqueda de reservas por cliente y fechas.").reset());
    }

    public void createTempTable() {
        tempDestinosCollection = database.getCollection("temp_destinos");
        tempDestinosCollection.createIndex(new Document("pais", 1)); // Índice ascendente en el campo "pais"
        tempDestinosCollection.drop(); // Eliminar la colección temporal si existe
        System.out.println(ansi().fg(GREEN).a("Índice creado en la colección 'destinos' para acelerar la búsqueda por país.").reset());

    }

    public void findAndInsertDestinosByPais(String pais) {
        tempDestinosCollection.drop(); // Limpiar la colección temporal

        // Realizar un find de destinos por país en la colección original
        destinoCollection.find(new Document("pais", pais)).forEach((Document destino) -> {
            // Insertar cada destino encontrado en la colección temporal
            tempDestinosCollection.insertOne(destino);
        });

        System.out.println("Destinos para el país '" + pais + "' insertados en la colección temporal.");
    }


    public void showDestinosFromTempTable() {
        System.out.println("Destinos en la colección temporal:");

        // Obtén todos los documentos de la colección temporal
        //FindIterable<Document> destinosDocuments = tempDestinosCollection.find();
        List<Destino> destinos = getAllEntities(tempDestinosCollection, this::documentToDestino);
        showDestinos(destinos);
    }

    public void clearTempTable() {
        tempDestinosCollection.drop(); // Eliminar todos los registros de la colección temporal
        System.out.println("Registros eliminados de la colección temporal.");
    }

    public void checkIndex(String tipo) {
        System.out.println(ansi().fg(GREEN).a("Índices en la colección '" + tipo + "':").reset());
        switch (tipo) {
            case "destinos":
                destinoCollection.listIndexes().forEach((Document index) -> {
                    System.out.println(ansi().fg(BLUE).a(index.toJson()).reset());
                });
                break;
            case "reservas":
                reservaCollection.listIndexes().forEach((Document index) -> {
                    System.out.println(ansi().fg(BLUE).a(index.toJson()).reset());
                });
                break;
            case "paquetes":
                paqueteCollection.listIndexes().forEach((Document index) -> {
                    System.out.println(ansi().fg(BLUE).a(index.toJson()).reset());
                });
                break;
            case "clientes":
                clienteCollection.listIndexes().forEach((Document index) -> {
                    System.out.println(ansi().fg(BLUE).a(index.toJson()).reset());
                });
                break;
            case "disponibilidad_paquetes":
                disponibilidadCollection.listIndexes().forEach((Document index) -> {
                    System.out.println(ansi().fg(BLUE).a(index.toJson()).reset());
                });
                break;
            case "temp_destinos":
                tempDestinosCollection.listIndexes().forEach((Document index) -> {
                    System.out.println(ansi().fg(BLUE).a(index.toJson()).reset());
                });
                break;

            default:
        }
    }

    public void getDestinosPopulares() {

        AggregateIterable<Document> aggregate = reservaCollection.aggregate(Arrays.asList(
                new Document("$group", new Document("_id", "$paquete_id").append("reservasCount", new Document("$sum", 1))),
                new Document("$lookup", new Document()
                        .append("from", "paquetes")
                        .append("localField", "_id")
                        .append("foreignField", "paquete_id")
                        .append("as", "paquete")),
                new Document("$unwind", "$paquete"),
                new Document("$lookup", new Document()
                        .append("from", "destinos")
                        .append("localField", "paquete.destino_id")
                        .append("foreignField", "destino_id")
                        .append("as", "destino")),
                new Document("$unwind", "$destino"),
                new Document("$group", new Document("_id", "$destino.destino_id").append("reservasCount", new Document("$sum", 1))),
                new Document("$sort", new Document("reservasCount", -1))
        ));

        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t|             Destinos Populares       |").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+--------------------------------------+").reset());

        System.out.println(ansi().fg(CYAN).a("|             Id de destino          | Total Reservas |").reset());
        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+----------------+").reset());

        for (Document document : aggregate) {
            Binary binaryUuid = document.get("_id", Binary.class);
            UUID _id = asUuid(binaryUuid.getData());
            System.out.println(
                    ansi().fg(GREEN).a("| " + _id).reset() + " | " +
                            ansi().fg(GREEN).a("\t\t" + document.get("reservasCount")).reset() + "\t\t | "
            );

            System.out.println(ansi().fg(YELLOW).a("+------------------------------------+----------------+").reset());
        }
    }

    public void createIndexForClientesByEmail() {
        // Crear un índice ascendente en el campo "correo_electronico"
        clienteCollection.createIndex(new Document("correo_electronico", 1));
        System.out.println(ansi().fg(GREEN).a("Índice creado en el campo 'correo_electronico' para acelerar la búsqueda de clientes por correo electrónico.").reset());
    }

    public void createIndexIdDestinoFecha() {
        disponibilidadCollection = database.getCollection("disponibilidad_paquetes");
        // Crear un índice compuesto en destino_id y fecha
        disponibilidadCollection.createIndex(new Document("destino_id", 1).append("fecha", 1));
        System.out.println(ansi().fg(GREEN).a("Índice creado en el campo 'destino_id' y 'fecha' para acelerar la búsqueda de paquetes disponibles.").reset());
    }

    public void getPaquetesDisponibles(UUID destinoId, int duracion) {
        AggregateIterable<Document> aggregate = paqueteCollection.aggregate(Arrays.asList(
                new Document("$match", new Document("destino_id", destinoId).append("duracion", duracion)),
                new Document("$project", new Document("paquete_id", 1).append("nombre", 1).append("duracion", 1).append("precio", 1))
        ));

        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+---------------------------------------------------------------------------------------------------------+").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t| Paquetes turísticos disponibles para el destino " + destinoId + " con duración " + duracion + " días: |").reset());
        System.out.println(ansi().fg(YELLOW).a("\t\t\t\t\t+---------------------------------------------------------------------------------------------------------+").reset());

        System.out.println(ansi().fg(CYAN).a("|             paquete_id             |       nombre       |             destino_id             | duracion |  precio  |").reset());
        System.out.println(ansi().fg(YELLOW).a("+------------------------------------+--------------------+------------------------------------+----------+----------+").reset());

        for (Document document : aggregate) {
            Binary binaryUuid = document.get("paquete_id", Binary.class);
            UUID paquete_id = asUuid(binaryUuid.getData());

            System.out.println(
                    ansi().fg(GREEN).a("| " + paquete_id).reset() + " | " +
                            ansi().fg(GREEN).a(document.get("nombre")).reset() + " | " +
                            ansi().fg(GREEN).a(destinoId).reset() + " | " +
                            ansi().fg(GREEN).a(document.get("duracion")).reset() + " dias | " +
                            ansi().fg(GREEN).a(document.get("precio")).reset() + " |"
            );
            System.out.println(ansi().fg(YELLOW).a("+------------------------------------+--------------------+------------------------------------+----------+----------+").reset());
        }

    }

    public void crearIndiceCompuestoClienteId_DestinoId_Pagado() {
        Document keys = new Document();
        keys.put("cliente_id", 1);
        keys.put("destino_id", 1);
        keys.put("pagado", 1);

        IndexOptions indexOptions = new IndexOptions().name("reservas_cliente_destino_pagado_idx");

        reservaCollection.createIndex(keys, indexOptions);

        System.out.println(ansi().fg(GREEN).a("Índice compuesto creado con éxito.").reset());
    }

    public void cargarDatos() {
        getDataList(); // Obtener datos de insercion de fichero
        insertDataDestinos(50); // Insertar destinos
        insertDataPaquetes(100); // Insertar paquetes
        insertDataClientes(200); // Insertar clientes
        insertDataReservas(500); // Insertar reservas
    }

    public void limpiarDatos() {
        boolean existBD = true;
        try{

            database.listCollectionNames();

        }catch(MongoCommandException e){

            if(e.getErrorCode() == 13){
                System.err.println(ansi().fg(RED).a("Error, NO existe la base de datos").reset());
                existBD = false;
            }else{
                System.err.println(ansi().fg(RED).a("Error, NO existe la base de datos").reset());
                existBD = false;
            }
            existBD = false;
        }catch(Exception e){

            System.err.println(ansi().fg(RED).a("Error, NO existe la base de datos " + e.getMessage()).reset());
            existBD = false;

        }

        if(existBD){
            database.drop();
            System.out.println(ansi().fg(GREEN).a("Base de Datos eliminada exitosamente.").reset());
        }

    }

    public void eliminarRegistroPorId(String nameCollection, UUID id){
        if(existeColeccion(nameCollection)){

            DeleteResult rs = null;

            switch (nameCollection) {
                case "clientes":
                    rs = clienteCollection.deleteOne(Filters.eq("cliente_id", id));
                    if(rs.getDeletedCount() > 0){
                        System.out.println(ansi().fg(GREEN).a("\t\t\t\t\t| Cliente " + id + " eliminado correctamente").reset());
                    }else{
                        System.out.println(ansi().fg(RED).a("\t\t\t\t\t| No existe un cliente con el id: " + id).reset());
                    }

                    break;

                case "destinos":
                    rs = destinoCollection.deleteOne(Filters.eq("destino_id", id));
                    if(rs.getDeletedCount() > 0){
                        System.out.println(ansi().fg(GREEN).a("\t\t\t\t\t| Destino " + id + " eliminado correctamente").reset());
                    }else{
                        System.out.println(ansi().fg(RED).a("\t\t\t\t\t| No existe un destino con el id: " + id).reset());
                    }
                    break;

                case "paquetes":
                    rs = paqueteCollection.deleteOne(Filters.eq("paquete_id", id));
                    if(rs.getDeletedCount() > 0){
                        System.out.println(ansi().fg(GREEN).a("\t\t\t\t\t| Paquete " + id + " eliminado correctamente").reset());
                    }else{
                        System.out.println(ansi().fg(RED).a("\t\t\t\t\t| No existe un paquete con el id: " + id).reset());
                    }
                    break;

                case "reservas":
                    rs = reservaCollection.deleteOne(Filters.eq("reserva_id", id));
                    if(rs.getDeletedCount() > 0){
                        System.out.println(ansi().fg(GREEN).a("\t\t\t\t\t| Reserva " + id + " eliminado correctamente").reset());
                    }else{
                        System.out.println(ansi().fg(RED).a("\t\t\t\t\t| No existe una reserva con el id: " + id).reset());
                    }
                    break;

                default:
                    System.err.println(ansi().fg(RED).a("==>Error, NO existe la colección: " + nameCollection +" con el id " + id).reset());
                    break;
            }

        } else{
            System.err.println(ansi().fg(RED).a("==>Error, NO existe la colección: " + nameCollection).reset());
        }
    }

    public void dropCollection(String nameCollection){
        if(existeColeccion(nameCollection)){
            database.getCollection(nameCollection).drop();
            System.out.println(ansi().fg(GREEN).a("Colección '" + nameCollection + "' eliminada exitosamente.").reset());
        }else{
            System.out.println(ansi().fg(RED).a("La Colección '" + nameCollection + "' no existe.").reset());
        }
    }

    public boolean existeColeccion(String nombreColeccion){
        try {
            // Verificar si la colección existe
            return database.listCollectionNames().into(new ArrayList<>()).contains(nombreColeccion);
        } catch (Exception e) {
            System.err.println(ansi().fg(RED).a("==>Error, NO existe la colección: " + e.getMessage()).reset());
            return false;
        }
    }

    public void dropCollectionsSecundaries() {
        dropCollection("tempDestinosCollection");
        dropCollection("resumenReservasPorPaqueteCollection");
        dropCollection("disponibilidad_paquetes");
    }

}
