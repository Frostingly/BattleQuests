package me.frostingly.gequests.Information.Data;

import org.bukkit.entity.EntityType;

public class EntityData {

    private String entityName;
    private String entityCustomName;
    private EntityType entityType;

    public EntityData(String entityName, String entityCustomName, EntityType entityType) {
        this.entityName = entityName;
        this.entityCustomName = entityCustomName;
        this.entityType = entityType;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityCustomName() {
        return entityCustomName;
    }

    public void setEntityCustomName(String entityCustomName) {
        this.entityCustomName = entityCustomName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
}
