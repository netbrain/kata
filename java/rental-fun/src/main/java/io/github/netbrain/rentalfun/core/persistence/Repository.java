package io.github.netbrain.rentalfun.core.persistence;


import org.boon.json.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * An in memory repository implementation that reads and writes entities from/to json
 * @param <T>
 */
public abstract class Repository<T extends Entity> {
    protected ArrayList<byte[]> repository = new ArrayList<>();
    private AtomicInteger idSequence = new AtomicInteger(0);
    private final ObjectMapper json;
    private final Class<T> type;

    protected Repository(Class<T> type, ObjectMapper json) {
        this.json = json;
        this.type = type;
    }

    /**
     * Retrieves a single entity by id.
     */
    public T getById(int id){
        if (id >= repository.size() || id < 0){
            return null;
        }
        return json.readValue(repository.get(id),type);
    }

    /**
     * Retrieves entities by one or more id's
     */
    public List<T> getByIds(List<Integer> ids){
        return getByIds(ids.stream().mapToInt(i->i).toArray());
    }

    /**
     * Retrieves entities by one or more id's
     */
    public List<T> getByIds(int ... ids){
        ArrayList<T> result = new ArrayList<>();
        for(int i = 0; i < ids.length; i++){
            T entity = getById(ids[i]);
            if (entity == null){
                throw new IllegalArgumentException("Nothing found for one or more id's");
            }
            result.add(entity);
        }
        return result;
    }

    /**
     * Inserts a new entity, will throw IllegalArgumentException if the entity is already stored.
     */
    public void insert(T entity){
        if(entity.getId() != -1){
            throw new IllegalArgumentException("Entity already exists in repository");
        }
        entity.setId(idSequence.getAndAdd(1));
        repository.add(entity.getId(),json.writeValueAsBytes(entity));
    }

    /**
     * Removes a single entity by id
     */
    public void remove(int id){
        repository.set(id,null);
    }

    /**
     * Removes a single entity
     */
    public void remove(T entity){
        remove(entity.getId());
    }

    /**
     * Retrieves all entities within this collection
     */
    public List<T> all(){
        return repository.stream()
                .filter(e -> e != null)
                .map(e -> json.readValue(e,type))
                .collect(Collectors.toList());
    }

    /**
     * Updates an entity, will throw a RuntimeException on optimistic locking failure.
     */
    public void update(T entity) {
        T other = getById(entity.getId());
        if(other == null || other.getVersion() != entity.getVersion()){
            throw new RuntimeException("Optimistic locking failed, entity is outdated");
        }

        entity.setVersion(entity.getVersion()+1);
        entity.setLastUpdated(new Date());
        repository.set(entity.getId(),json.writeValueAsBytes(entity));
    }

    /**
     * Truncates the collection
     */
    public void truncate(){
        repository = new ArrayList<>();
        idSequence = new AtomicInteger(0);
    }

}
