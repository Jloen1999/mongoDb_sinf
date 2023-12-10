package unex.model;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.UUID;

// Clase base para las entidades con identificador UUID
public abstract class BaseEntity {
    @BsonId
    protected UUID id;

    public BaseEntity(UUID id) {
        this.id = id;
    }

    public BaseEntity() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }
}

