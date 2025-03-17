package me.kaeternn.pasteurcraft.abstraction;

import java.util.List;

abstract public class AbstractTransmission<T> {
    // Variables
    private List<T> list;
    private int chance;

    // Constructor
    public AbstractTransmission(List<T> list, int chance){
        this.list = list;
        this.chance = chance;
    }

    // Assessors
    public List<T> getList() { return list; }
    public int getChance() { return chance; }
}
