package com.mrt.dbobject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Train {
    private int trainId;
    private String trainCode;
    private int seatCapacity;
    private String status;

    public Train(int trainId, String trainCode, int seatCapacity, String status) {
        this.trainId = trainId;
        this.trainCode = trainCode;
        this.seatCapacity = seatCapacity;
        this.status = status;
    }

    public int getTrainId() { return trainId; }
    public String getTrainCode() { return trainCode; }
    public int getSeatCapacity() { return seatCapacity; }
    public String getStatus() { return status; }

    public static Train parseResultSet(ResultSet rs) throws SQLException {
        return new Train(
            rs.getInt("train_id"), 
            rs.getString("train_code"), 
            rs.getInt("seat_capacity"), 
            rs.getString("status")
        );
    }

    @Override
    public String toString() {
        return trainCode + " (" + seatCapacity + " seats)";
    }
}
