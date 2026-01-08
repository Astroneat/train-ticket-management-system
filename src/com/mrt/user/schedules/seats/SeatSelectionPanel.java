package com.mrt.user.schedules.seats;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.mrt.Universal;
import com.mrt.factory.UIFactory;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.Train;
import com.mrt.models.User;
import com.mrt.services.TrainService;
import com.mrt.user.schedules.BookingPage;
import com.mrt.user.schedules.BookingStep;
import com.mrt.user.schedules.SchedulesPanel;

public class SeatSelectionPanel extends JPanel implements BookingPage {

    private SchedulesPanel parent;
    private User user;
    private Schedule selectedSchedule;
    private List<Seat> selectedSeats;

    private CarPanel carPanel;
    private SeatInfoPanel seatInfoPanel;
    private JButton nextBtn;

    public SeatSelectionPanel(SchedulesPanel parent) {
        this.parent = parent;
        this.user = parent.getUser();
    }

    public void onShow() {
        selectedSchedule = parent.getSelectedSchedule();
        selectedSeats = new ArrayList<>();

        removeAll();
                
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        // JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        // titlePanel.setOpaque(false);
        // titlePanel.add(UIFactory.createBoldLabel("<html>Viewing seats for route: <font color='#00b8ff'>" + RouteService.getRouteById(selectedSchedule.getRouteId()).getRouteSummary() + "</font></html>", 20));
        // add(titlePanel);
        
        Train train = TrainService.getTrainById(selectedSchedule.getTrainId());
        carPanel = new CarPanel(this, train.getSeatCapacity());
        add(carPanel);

        seatInfoPanel = new SeatInfoPanel(this);
        add(seatInfoPanel);

        add(createButtonsPanel());
    }

    public void addSeatToSelection(Seat seat) {
        selectedSeats.add(seat);
        refreshSeatStatus();
    }
    public void removeSeatFromSelection(Seat seat) {
        selectedSeats.remove(seat);
        refreshSeatStatus();
    }

    private void refreshSeatStatus() {
        if(selectedSeats.isEmpty()) {
            nextBtn.setEnabled(false);
        }
        else {
            nextBtn.setEnabled(true);
        }

        seatInfoPanel.refreshSeatStatus();
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panel.setOpaque(false);

        JButton backBtn = UIFactory.createButton("Back");
        backBtn.setPreferredSize(new Dimension(100, 36));
        backBtn.addActionListener(e -> {
            parent.showStep(BookingStep.SEARCH);
        });
        panel.add(backBtn, BorderLayout.WEST);

        nextBtn = UIFactory.createButton("Next");
        nextBtn.setPreferredSize(new Dimension(100, 36));
        nextBtn.setEnabled(false);
        nextBtn.addActionListener(e -> {
            parent.setSelectedSeats(selectedSeats);
            parent.showStep(BookingStep.SUMMARY);
        });
        panel.add(nextBtn, BorderLayout.EAST);

        return panel;
    }

    public JButton createSeatLabel(String text) {
        JButton btn = UIFactory.createButton(text);
        applySeatButtonStyle(btn);
        return btn;
    }

    public JToggleButton createSeatToggleButton() {
        JToggleButton btn = UIFactory.createToggleButton("");
        applySeatButtonStyle(btn);
        btn.setOpaque(true);
        return btn;
    }

    public void applySeatButtonStyle(AbstractButton btn) {
        btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btn.setBorderPainted(false);
        btn.setBackground(Universal.PASTEL_WHITE);
        btn.setPreferredSize(new Dimension(30, 30));        
    }

    public void markSeatBooked(JToggleButton btn) {
        btn.setEnabled(false);
        btn.setBackground(Universal.PASTEL_RED);
    }
    public void markSeatBoarded(JToggleButton btn) {
        btn.setEnabled(false);
        btn.setBackground(Universal.PASTEL_PURPLE);
    }
    public void markSeatMine(JToggleButton btn) {
        btn.setEnabled(false);
        btn.setBackground(Universal.PASTEL_CYAN);
    }

    public JPanel createSeatBorderWrapper(JToggleButton btn) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBorder(BorderFactory.createLineBorder(Universal.BACKGROUND_BLACK, 1));
        wrapper.add(btn);
        return wrapper;
    }

    public Schedule getSelectedSchedule() {
        return selectedSchedule;
    }
    public User getUser() {
        return user;
    }
    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }
}
