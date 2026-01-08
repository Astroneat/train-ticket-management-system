package com.mrt.services;

public class SeatService {

    private static final int rows = 6;
    private static final int cols = 20;
    private static final int pricePerTicket = 100_000;

    public static int getRows() {
        return rows;
    }
    public static int getCols() {
        return cols;
    }
    public static int getPricePerTicket() {
        return pricePerTicket;
    }
    
    public static String toSeatCode(int seatIndex) {
        int seatCol = seatIndex / rows + 1;
        char seatRow = (char) ('A' + seatIndex % rows);

        return seatRow + "" + seatCol;
    }
}
