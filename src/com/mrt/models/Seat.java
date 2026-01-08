package com.mrt.models;

public class Seat {
    private int carNo;
    private int seatIndex;
    public int getCarNo() {
        return carNo;
    }
    public int getSeatIndex() {
        return seatIndex;
    }
    public Seat(int carNo, int seatIndex) {
        this.carNo = carNo;
        this.seatIndex = seatIndex;
    }

    // @Override
    // public boolean equals(Object other) {
    //     if(this == other) return true;
    //     if(other instanceof Seat) {
    //         return ((Seat)other).getCarNo() == carNo && ((Seat) other).getSeatIndex() == seatIndex;
    //     }
    //     return false;
    // }
}
