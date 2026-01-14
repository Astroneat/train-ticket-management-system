package com.mrt.admin.reports;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.mrt.Universal;
import com.mrt.admin.reports.cards.DataCard;
import com.mrt.admin.reports.cards.HorizontalBarChartCard;
import com.mrt.admin.reports.cards.KPICard;
import com.mrt.admin.reports.cards.LineChartCard;
import com.mrt.admin.reports.cards.PieChartCard;
import com.mrt.frames.Page;
import com.mrt.services.CurrencyService;

public class ReportsPanel extends JPanel implements Page {

    private List<DataCard> dataCards = new ArrayList<>();

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Universal.BACKGROUND_WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // contentPanel.add(createKPIPanel());
        contentPanel.add(createRow(
            new KPICard(
                "Total Tickets Sold", 
                Universal.PASTEL_YELLOW,
                () -> Analytics.totalTicketsSold()
            ),
            new KPICard(
                "Total Revenue", 
                Universal.PASTEL_GREEN,
                () -> CurrencyService.formatVnd(Analytics.totalRevenue())
            ),
            new KPICard(
                "Active Schedules",
                Universal.PASTEL_BLUE,
                () -> Analytics.activeSchedules()
            ),
            new KPICard(
                "Cancelled Schedules",
                Universal.PASTEL_RED,
                () -> Analytics.cancelledSchedules()
            )
        ));
        contentPanel.add(createRow(
            new PieChartCard("Ticket Status Distribution", () -> Analytics.ticketStatusStat())
        ));
        contentPanel.add(createRow(
            new LineChartCard("Tickets Sold Per Day", () -> Analytics.ticketsSoldPerDayLastNDays(10)),
            new LineChartCard("Revenue Per Day", () -> Analytics.revenueSoldPerDayLastNDays(10))
        ));
        contentPanel.add(createRow(
            new HorizontalBarChartCard("Tickets Sold Per Route (Top 10)", () -> Analytics.routePopularity(10))
        ));


        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createRow(DataCard... cards) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);

        for(DataCard dc: cards) {
            dataCards.add(dc);
            panel.add((JPanel) dc);
        }
        return panel;
    }
    private void loadData() {
        for(DataCard dc: dataCards) dc.loadData();
    }

    public void refreshPage() {
        loadData();
    }
}
