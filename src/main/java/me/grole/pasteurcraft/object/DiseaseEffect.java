package me.grole.pasteurcraft.object;

public class DiseaseEffect {
    private String name;
    private int level;

    public DiseaseEffect(String name, int level){
        this.name = name;
        this.level = level;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
}
