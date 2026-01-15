package com.mrt.user.schedules.seats;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.mrt.factory.UIFactory;
import com.mrt.models.Seat;
import com.mrt.models.Ticket;
import com.mrt.services.SeatService;
import com.mrt.services.TicketService;

public class CarPanel extends JPanel {

    private SeatSelectionPanel parent;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private final int rows = SeatService.getRows();
    private final int cols = SeatService.getCols();
    private int numCars;
    private int currentShownCar;
    private Map<Integer, List<JToggleButton>> seats;

    private JButton scrollLeftBtn;
    private JButton scrollRightBtn;
    
    public CarPanel(SeatSelectionPanel parent, int numSeats) {
        this.parent = parent;
        seats = new HashMap<>();

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        if(numSeats % (rows * cols) != 0) {
            throw new RuntimeException("Number of seats not even (" + numSeats + " is not divisble by " + rows * cols + ")");
        }
        numCars = numSeats / (rows * cols);

        currentShownCar = 1;
        for(int i = 1; i <= numCars; i++) {
            cardPanel.add(String.valueOf(i), createCarPanel(i));
        }
        loadBookedSeats();

        add(cardPanel, BorderLayout.CENTER);
        add(createScrollLeftBtnButton(), BorderLayout.WEST);
        add(createScrollRightBtnButton(), BorderLayout.EAST);
    }

    private void showCar(int carIndex) {
        currentShownCar = carIndex;
        cardLayout.show(cardPanel, String.valueOf(currentShownCar));
        if(carIndex == 1) {
            scrollRightBtn.setEnabled(false);
        } else {
            scrollRightBtn.setEnabled(true);
        }

        if(carIndex == numCars) {
            scrollLeftBtn.setEnabled(false);
        } else {
            scrollLeftBtn.setEnabled(true);
        }
    }

    private void loadNextCar() {
        showCar(currentShownCar + 1);
    }

    private void loadPreviousCar() {
        showCar(currentShownCar - 1);
    }

    private JPanel createScrollLeftBtnButton() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        scrollLeftBtn = UIFactory.createIconButton("src/com/mrt/img/caret-left.png", new Dimension(25, 25));
        scrollLeftBtn.setPreferredSize(new Dimension(30, 50));
        scrollLeftBtn.addActionListener(e -> {
            loadNextCar();
        });

        panel.add(scrollLeftBtn, new GridBagConstraints());
        return panel;
    }

    private JPanel createScrollRightBtnButton() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        scrollRightBtn = UIFactory.createIconButton("src/com/mrt/img/caret-right.png", new Dimension(25, 25));
        scrollRightBtn.setPreferredSize(new Dimension(30, 50));
        scrollRightBtn.setEnabled(false);
        scrollRightBtn.addActionListener(e -> {
            loadPreviousCar();
        });

        panel.add(scrollRightBtn, new GridBagConstraints());
        return panel;
    }

    private JPanel createCarPanel(int carIndex) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createTitlePanel(carIndex));
        panel.add(createSeatMapPanel(carIndex));

        return panel;
    }

    private JPanel createTitlePanel(int carIndex) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        panel.add(UIFactory.createBoldLabel("Car " + carIndex, 18));

        JButton refreshBtn = UIFactory.createIconButton("src/com/mrt/img/refresh.png", new Dimension(10, 10));
        refreshBtn.setPreferredSize(new Dimension(32, 32));
        refreshBtn.addActionListener(e -> {
            loadBookedSeats();
        });
        panel.add(refreshBtn);

        return panel;
    }

    private JPanel createSeatMapPanel(int carIndex) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        for(int x = 1; x <= cols; x++) {
            for(int y: new int[]{0, rows + 2}) {
                gbc.gridx = x;
                gbc.gridy = y;

                JButton lbl = parent.createSeatLabel((cols - x + 1) + "");
                lbl.setFont(UIFactory.createDefaultBoldFont(14));
                panel.add(lbl, gbc);
            }
        }

        for(int y = 1; y <= rows + 1; y++) {
            if(y == rows/2 + 1) continue;

            for(int x: new int[]{0, cols + 1}) {
                gbc.gridx = x;
                gbc.gridy = y;

                int seatRow = y - (y > rows/2 ? 1 : 0);
                char rowChar = (char) ('A' + seatRow - 1);
                
                JButton lbl = parent.createSeatLabel(rowChar + "");
                lbl.setFont(UIFactory.createDefaultBoldFont(14));
                panel.add(lbl, gbc);
            }
        }

        seats.put(carIndex, new ArrayList<>());
        int seatIndex = 0;
        for(int x = cols; x >= 1; x--) {
            for(int y = 1; y <= rows + 1; y++) {
                gbc.gridx = x;
                gbc.gridy = y;
                if(y == rows/2 + 1) {
                    panel.add(parent.createSeatLabel(""), gbc);
                    continue;
                }

                JToggleButton btn = parent.createSeatToggleButton();
                seats.get(carIndex).add(btn);
                btn.putClientProperty("seat", new Seat(carIndex, seatIndex));
                btn.addActionListener(e -> {
                    Seat seat = (Seat) btn.getClientProperty("seat");
                    if(btn.isSelected()) {
                        parent.addSeatToSelection(seat);
                    }
                    else {
                        parent.removeSeatFromSelection(seat);
                    }
                });

                // JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                // wrapper.setBorder(BorderFactory.createLineBorder(Universal.BACKGROUND_BLACK, 1));
                // wrapper.add(btn);
                seatIndex++;
                panel.add(parent.createSeatBorderWrapper(btn), gbc);
            }
        }

        return panel;
    }

    public void loadBookedSeats() {
        List<Ticket> bookedTickets = TicketService.getTicketsBySchedule(parent.getSelectedSchedule());
        for(Ticket tk: bookedTickets) {
            int carIndex = tk.getCarNo();
            int seatIndex = tk.getSeatIndex();
            String seatStatus = tk.getStatus();
            JToggleButton btn = seats.get(carIndex).get(seatIndex);

            if(tk.getUserId() == parent.getUser().getUserId()) {
                parent.markSeatMine(btn);
            }
            else if(seatStatus.equals("booked")) {
                parent.markSeatBooked(btn);
            }
            else if(seatStatus.equals("boarded")) {
                parent.markSeatBoarded(btn);
            }
        }
    }

}
