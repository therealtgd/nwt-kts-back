package com.foober.foober.model.enumeration;

public enum VehicleType {
    SEDAN(140),
    WAGON(180);

    private final int price;

    VehicleType(int price) {
        this.price = price;
    }

    public int getPrice() {
        return this.price;
    }
}