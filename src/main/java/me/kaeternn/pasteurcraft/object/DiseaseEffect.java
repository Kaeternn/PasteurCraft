package me.kaeternn.pasteurcraft.object;

public class DiseaseEffect {
    // Variables
    private String name;
    private int level;

    // Constructor
    public DiseaseEffect(String name, int level){
        this.name = name;
        this.level = level;
    }

    // Assessors
    public String getName() { return name; }
    public int getLevel() { return level; }
}
