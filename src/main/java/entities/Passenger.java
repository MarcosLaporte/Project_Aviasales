package entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import entities.annotations.Column;
import entities.annotations.Range;
import entities.annotations.Size;

@JsonPropertyOrder({"id", "name"})
public class Passenger implements Entity {
    @Column(name = "id", autoIncrement = true)
    @Range(min = 101)
    private int id;

    @Column(name = "name")
    @Size(min = 1, max = 255)
    private String name;

    @Column(name = "last_name")
    @Size(min = 1, max = 255)
    private String lastName;

    private Passenger() {
    }

    public Passenger(int id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }

    public Passenger(String name, String lastName) {
        this.name = name;
        this.lastName = lastName;
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

    @JsonGetter("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonSetter("name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return this.name + " " + this.lastName;
    }

    @Override
    public String toString() {
        return String.format("ID%d - %s %s", this.id, this.name, this.lastName);
    }
}
