package io.github.netbrain.rentalfun.core.persistence;


import java.util.Date;

public abstract class Entity {

    protected int id = -1;

    protected int version = 0;

    protected Date lastUpdated = new Date();

    protected Date created = new Date();

    public int getId(){
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    void setVersion(int version) {
        this.version = version;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return hashCode() == entity.hashCode();

    }

    @Override
    public int hashCode() {
        return id == -1 ? super.hashCode() : id;
    }

    /**
     * @deprecated Should never be set manually.
     */
    @Deprecated
    public void setCreated(Date created) {
        this.created = created;
    }
}
