package io.github.tduva.fredlist.d;

/**
 * Created by tduva on 08.08.2017.
 */

public class Category {

    private final int id;
    private final String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Category) {
            return id == ((Category)other).id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + id;
        return hash;
    }

}
