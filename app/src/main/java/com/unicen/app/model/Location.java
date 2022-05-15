package com.unicen.app.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

@Getter
@Setter
@MappedSuperclass
public class Location {

    private double latitude;
    private double longitude;

    // string representation of (lat, long) pair (e.g. street, number, zipcode, state, country)
    private String geocode;

    public Location(double latitude, double longitude, String geocode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.geocode = geocode;
    }

    protected Location() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Math.abs(latitude - location.latitude) < 0.01 && Math.abs(longitude - location.longitude) < 0.01 && Objects.equals(geocode, location.geocode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, geocode);
    }
}
