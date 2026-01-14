package com.mrt.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
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

    public static String decode(File imageFile) throws Exception {
        BufferedImage image = ImageIO.read(imageFile);
        if(image == null) {
            throw new IllegalArgumentException("Invalid image");
        }

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        return new MultiFormatReader().decode(bitmap).getText();
    }
}
