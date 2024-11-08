package entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import entities.annotations.Column;
import entities.annotations.Range;

@JsonPropertyOrder({"id", "idFrom", "idTo", "airlineId", "km", "price"})
public class Route implements Entity {
    @Column(name="id", autoIncrement = true)
    @Range(min = 1001)
    private int id;

    @Column(name = "id_from")
    @Range(min = 101)
    private int idFrom;

    @Column(name = "id_to")
    @Range(min = 101)
    private int idTo;

    @Column(name = "airline_id")
    @Range(min = 201)
    private int airlineId;

    @Column(name = "km")
    @Range(min = 1, max = 40_075)
    private int km;

    @Column(name = "price")
    @Range(min = 1)
    private double price;

    private Route() {
    }

    public Route(int id, int idFrom, int idTo, int airlineId, int km, double price) {
        this.id = id;
        this.idFrom = idFrom;
        this.idTo = idTo;
        this.airlineId = airlineId;
        this.km = km;
        this.price = price;
    }

    public Route(int idFrom, int idTo, int airlineId, int km, double price) {
        this.idFrom = idFrom;
        this.idTo = idTo;
        this.airlineId = airlineId;
        this.km = km;
        this.price = price;
    }

    private Route(Integer id, Integer idFrom, Integer idTo, Integer airlineId, Integer km, Double price) {
        this(id.intValue(), idFrom.intValue(), idTo.intValue(), airlineId.intValue(), km.intValue(), price.doubleValue());
    }

    private Route(Integer idFrom, Integer idTo, Integer airlineId, Integer km, Double price) {
        this(idFrom.intValue(), idTo.intValue(), airlineId.intValue(), km.intValue(), price.doubleValue());
    }

    @JsonGetter("id")
    public int getId() {
        return id;
    }

    @JsonSetter("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonGetter("idFrom")
    public int getIdFrom() {
        return this.idFrom;
    }

    @JsonSetter("idFrom")
    public void setIdFrom(int idFrom) {
        this.idFrom = idFrom;
    }

    @JsonGetter("idTo")
    public int getIdTo() {
        return this.idTo;
    }

    @JsonSetter("idTo")
    public void setIdTo(int idTo) {
        this.idTo = idTo;
    }

    @JsonGetter("airlineId")
    public int getAirlineId() {
        return this.airlineId;
    }

    @JsonSetter("airlineId")
    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    @JsonGetter("km")
    public int getKm() {
        return this.km;
    }

    @JsonSetter("km")
    public void setKm(int km) {
        this.km = km;
    }

    @JsonGetter("price")
    public double getPrice() {
        return this.price;
    }

    @JsonSetter("price")
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Route{" + "id=" + id + ", idFrom=" + idFrom + ", idTo=" + idTo + ", km=" + km + ", price=" + price + '}';
    }
}
