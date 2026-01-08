package com.mrt.services;

public class CurrencyService {
    
    public static String formatVnd(int amount) {
        return String.format("%,d ₫", amount);
    }
}
