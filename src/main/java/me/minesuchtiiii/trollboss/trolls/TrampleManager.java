package me.minesuchtiiii.trollboss.trolls;

import org.bukkit.entity.Entity;

import java.util.ArrayList;

public class TrampleManager {
    private final ArrayList<Entity> cows;

    public TrampleManager() {
        this.cows = new ArrayList<>();
    }

    public void addCow(Entity entity) {
        this.cows.add(entity);
    }

    public void removeCow(Entity entity) {
        this.cows.remove(entity);
    }

    public void removeAllCows() {
        cows.forEach(Entity::remove);
        this.cows.clear();
    }
}
