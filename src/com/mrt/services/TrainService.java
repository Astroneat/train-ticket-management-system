package com.mrt.services;

import java.util.List;

import com.mrt.Universal;
import com.mrt.model.Train;

public class TrainService {
    
    public static final int OK = 0;
    public static final int BLANK_TRAIN_CODE = 1;
    public static final int INVALID_SEAT_CAPACITY = 2;
    public static final int ANOTHER_TRAIN_CODE_EXISTS = 3;

    public static int createTrain(String trainCode, int seatCapacity) {
        int state = validateTrain(trainCode, seatCapacity);
        if(state != OK) return state;

        Universal.db().execute(
            """
                INSERT INTO trains(train_code, seat_capacity)
                VALUES (?, ?)    
            """,
            trainCode,
            seatCapacity
        );
        return OK;
    }

    public static int updateTrain(int trainId, String trainCode, int seatCapacity, String status) {
        int state = validateTrain(trainCode, seatCapacity);
        if(state != OK) return state;

        Universal.db().execute(
            """
                UPDATE trains 
                SET train_code = ?, seat_capacity = ?, status = ?
                WHERE train_id = ?
            """,
            trainCode,
            seatCapacity,
            status,
            trainId
        );
        return OK;
    }

    public static Train getTrainFromId(int trainId) {
        return Universal.db().queryOne(
            """
                SELECT * FROM trains WHERE train_id = ?    
            """,
            rs -> Train.parseResultSet(rs),
            trainId
        );
    }

    public static int validateTrain(String trainCode, int seatCapacity) {
        if(trainCode.isBlank()) {
            return BLANK_TRAIN_CODE;
        }
        if(seatCapacity <= 0) {
            return INVALID_SEAT_CAPACITY;
        }
        if(trainExists(trainCode)) {
            return ANOTHER_TRAIN_CODE_EXISTS;
        }

        return OK;
    }

    public static boolean trainExists(String trainCode) {
        Boolean exists = Universal.db().queryOne(
            """
                SELECT 1 AS exist FROM trains
                WHERE train_code = ? 
            """,
            rs -> rs.getBoolean("exist"),
            trainCode
        );
        if(exists == null) return true;
        return false;
    }
}
