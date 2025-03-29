package me.kaeternn.pasteurcraft.entities.transmission;

import java.util.Set;

abstract public class AbstractTransmission<T> {
    // Variables
    private Set<T> list;
    private int chance;

    // Constructor
    public AbstractTransmission(Set<T> list, int chance){
        this.list = list;
        this.chance = chance;
    }

    // Assessors
    public Set<T> getList() { return list; }
    public int getChance() { return chance; }
}
