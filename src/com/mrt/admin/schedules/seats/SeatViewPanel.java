package com.mrt.admin.schedules.seats;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mrt.Universal;
import com.mrt.admin.schedules.SchedulesPanel;
import com.mrt.factory.UIFactory;
import com.mrt.models.Schedule;
import com.mrt.models.Seat;
import com.mrt.models.Train;
import com.mrt.services.TrainService;

public class SeatViewPanel extends JPanel {

    private SchedulesPanel parent;
    private Schedule selectedSchedule;

    private CarPanel carPanel;
    private SeatInfoPanel seatInfoPanel;
    private Seat selectedSeat;

    public SeatViewPanel(SchedulesPanel parent) {
        this.parent = parent;
    }

    public void onShow() {
        selectedSchedule = parent.getSelectedSchedule();

        removeAll();
                
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        // JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        // titlePanel.setOpaque(false);
        // titlePanel.add(UIFactory.createBoldLabel("<html>Viewing seats for route: <font color='#00b8ff'>" + RouteService.getRouteById(selectedSchedule.getRouteId()).getRouteSummary() + "</font></html>", 20));
        // add(titlePanel);
        
        seatInfoPanel = new SeatInfoPanel(this);

        Train train = TrainService.getTrainById(selectedSchedule.getTrainId());
        carPanel = new CarPanel(this, train.getSeatCapacity());

        add(carPanel);
        add(seatInfoPanel);
        add(createButtonsPanel());
    }

    public void refreshSeatInfo() {
        seatInfoPanel.refreshSeatStatus();
    }
    public void refreshCarPanel() {
        carPanel.refresh();
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panel.setOpaque(false);

        JButton backBtn = UIFactory.createButton("Back");
        backBtn.setPreferredSize(new Dimension(100, 36));
        backBtn.addActionListener(e -> {
            parent.showPage(SchedulesPanel.SEARCH);
        });
        panel.add(backBtn, BorderLayout.WEST);

        return panel;
    }

    public JButton createSeatLabel(String text) {
        JButton btn = UIFactory.createButton(text);
        applySeatButtonStyle(btn);
        return btn;
    }

    public JButton createSeatButton() {
        JButton btn = UIFactory.createButton();
        applySeatButtonStyle(btn);
        btn.setOpaque(true);
        return btn;
    }

    public void applySeatButtonStyle(JButton btn) {
        btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btn.setBorderPainted(false);
        btn.setBackground(Universal.PASTEL_WHITE);
        btn.setPreferredSize(new Dimension(30, 30));        
    }

    public void markSeatBooked(JButton btn) {
        btn.setBackground(Universal.PASTEL_RED);
    }
    public void markSeatBoarded(JButton btn) {
        btn.setBackground(Universal.PASTEL_PURPLE);
    }
    public void markSeatUnoccupied(JButton btn) {
        btn.setBackground(Universal.PASTEL_WHITE);
    }

    public JPanel createSeatBorderWrapper(JButton btn) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBorder(BorderFactory.createLineBorder(Universal.BACKGROUND_BLACK, 1));
        wrapper.add(btn);
        return wrapper;
    }

    public void setSelectedSeat(Seat seat) {
        this.selectedSeat = seat;
        refreshSeatInfo();
    }
    public Seat getSelectedSeat() {
        return selectedSeat;
    }

    public Schedule getSelectedSchedule() {
        return selectedSchedule;
    }
}
