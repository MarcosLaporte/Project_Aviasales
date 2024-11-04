package entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import entities.annotations.Column;
import entities.annotations.Range;
import entities.annotations.Size;

@JsonPropertyOrder({"id", "name"})
public class Airport implements Entity {
    @Column(name = "id", autoIncrement = true)
    @Range(min = 101)
    private int id;

    @Column(name = "name")
    @Size(min = 1, max = 255)
    private String name;

    private Airport() {
    }

    public Airport(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Airport(String name) {
        this.name = name;
    }

    @JsonGetter("id")
    public int getId() {
        return id;
    }

    @JsonSetter("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonGetter("name")
    public String getName() {
        return name;
    }

    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }
}
