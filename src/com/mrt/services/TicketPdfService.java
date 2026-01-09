package com.mrt.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfWriter;

import com.mrt.models.Route;
import com.mrt.models.Schedule;
import com.mrt.models.Ticket;
import com.mrt.models.Train;
import com.mrt.models.User;

public class TicketPdfService {
    
    public static File generateTicketPdf(Ticket ticket, Schedule schedule, Route route, Train train, User user, BufferedImage qrImage) {
        try {
            File file = File.createTempFile("ticket_" + ticket.getTicketId(), ".pdf");
            
            Document doc = new Document(PageSize.A6);
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.setMargins(18, 18 , 18, 18);
            doc.open();

            BaseFont baseNormal = BaseFont.createFont("src/com/mrt/fonts/NotoSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont baseBold = BaseFont.createFont("src/com/mrt/fonts/NotoSans-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            // BaseFont baseExtraBold = BaseFont.createFont("src/com/mrt/fonts/NotoSans-ExtraBold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            // BaseFont baseBlack = BaseFont.createFont("src/com/mrt/fonts/NotoSans-Black.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font title = new Font(baseBold, 14);
            Font bold = new Font(baseBold, 9);
            Font normal = new Font(baseNormal, 9);

            doc.add(new Paragraph("MRT Viet Nam - Boarding Ticket", title));
            doc.add(new Paragraph(" ", normal));

            addRow(doc, "Ticket ID:", ticket.getTicketId() + "", bold, normal);
            addRow(doc, "Train:", train.getTrainSummary(), bold, normal);
            addRow(doc, "Route:", route.getRouteSummary(), bold, normal);
            // addRow(doc, "Departure:", schedule.getFormattedDepartureTime(), bold, normal);
            // addRow(doc, "Arrival:", schedule.getFormattedArrivalTime(), bold, normal);
            addRow(doc, "Travel Time:", schedule.getFormattedDepartureTime() + " - " + schedule.getFormattedArrivalTime(), bold, normal);
            addRow(doc, "Passenger:", user.getFullName(), bold, normal);
            addRow(doc, "Seat:", "Car " + ticket.getCarNo() + " - " + SeatService.toSeatCode(ticket.getSeatIndex()), bold, normal);

            doc.add(new Paragraph(" ", normal));
            addRow(doc, "Price:", CurrencyService.formatVnd(ticket.getPrice()), bold, normal);
            addRow(doc, "Fees:", CurrencyService.formatVnd(ticket.getFees()), bold, normal);
            addRow(doc, "Total:", CurrencyService.formatVnd(ticket.getPrice() + ticket.getFees()), bold, bold);
            // doc.add(new Paragraph(" ", normal));

            Image qr = Image.getInstance(qrImage, null);
            qr.scaleToFit(150, 150);
            qr.setAlignment(Image.ALIGN_CENTER);
            doc.add(qr);

            Paragraph scanAtGate = new Paragraph("Scan at gate", bold);
            scanAtGate.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(scanAtGate);
            doc.close();

            return file;

        } catch(Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private static void addRow(Document doc, String label, String value, Font labelFont, Font valueFont) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        p.add(new Chunk(value, valueFont));
        doc.add(p);
    }
}
