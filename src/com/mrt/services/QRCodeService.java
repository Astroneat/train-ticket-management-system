package com.mrt.services;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mrt.models.Ticket;

public class QRCodeService {
    
    public static BufferedImage generateQRCode(String data, int size) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hint = new HashMap<>();
            hint.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = qrCodeWriter.encode(
                data,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hint
            );
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch(Exception e) {
            throw new RuntimeException("QR generation failed", e);
        }
    }

    public static String buildQRData(Ticket ticket) {
        String data = ticket.getTicketId() + "";
        return data;
    }
}
