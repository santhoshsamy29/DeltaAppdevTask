package com.example.application.pokedex;


public class Pokemon {

    public String name;
    public int Base_experience;
    int height;
    int weight;
    String image;
    String[] abilities;
    String[] stat_name;
    Integer[] base_stat;
    String[] types;

    public Integer[] getBase_stat() {
        return base_stat;
    }

    public void setBase_stat(Integer[] base_stat) {
        this.base_stat = base_stat;
    }

    public String[] getAbilities() {
        return abilities;
    }

    public void setAbilities(String[] abilities) {
        this.abilities = abilities;
    }

    public String[] getStat_name() {
        return stat_name;
    }

    public void setStat_name(String[] stat_name) {
        this.stat_name = stat_name;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBase_experience() {
        return Base_experience;
    }

    public void setBase_experience(int base_experience) {
        Base_experience = base_experience;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
