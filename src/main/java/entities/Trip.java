package entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import entities.annotations.Column;
import entities.annotations.Range;

import java.time.LocalDate;

@JsonPropertyOrder({"id", "passId", "routeId", "date"})
public class Trip implements Entity {
    @Column(name = "id")
    @Range(min = 101)
    private int id;

    @Column(name = "pass_id")
    @Range(min = 101)
    private int passId;

    @Column(name = "route_id")
    @Range(min = 1001)
    private int routeId;

    @Column(name = "date")
    private LocalDate date;

    private Trip() {
    }

    public Trip(int id, int passId, int routeId, LocalDate date) {
        this.id = id;
        this.passId = passId;
        this.routeId = routeId;
        this.date = date;
    }

    public Trip(int passId, int routeId, LocalDate date) {
        this.passId = passId;
        this.routeId = routeId;
        this.date = date;
    }

    private Trip(Integer id, Integer passId, Integer routeId, LocalDate date) {
        this(id.intValue(), passId.intValue(), routeId.intValue(), date);
    }

    private Trip(Integer passId, Integer routeId, LocalDate date) {
        this(passId.intValue(), routeId.intValue(), date);
    }

    @JsonGetter("id")
    public int getId() {
        return id;
    }

    @JsonSetter("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonGetter("passId")
    public int getPassId() {
        return passId;
    }

    @JsonSetter("passId")
    public void setPassId(int passId) {
        this.passId = passId;
    }

    @JsonGetter("routeId")
    public int getRouteId() {
        return routeId;
    }

    @JsonSetter("routeId")
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    @JsonGetter("date")
    public LocalDate getDate() {
        return date;
    }

    @JsonSetter("date")
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%d - %d | %s", this.passId, this.routeId, this.date);
    }
}
