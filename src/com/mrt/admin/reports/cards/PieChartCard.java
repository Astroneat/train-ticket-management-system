package com.mrt.admin.reports.cards;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mrt.Universal;
import com.mrt.admin.reports.models.TicketStatus;
import com.mrt.factory.UIFactory;

class PieChart extends JPanel {
    private List<TicketStatus> data;

    public PieChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(200, 200));
    }

    public void loadData(List<TicketStatus> data) {
        this.data = data;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(data.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 20 * 2;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        int sum = 0, accum_angle = 0;
        for(TicketStatus ts: data) sum += ts.getCount();
        for(TicketStatus ts: data) {
            String status = ts.getStatus();

            int angle = (int) Math.round(360.0 * ts.getCount() / sum);
            g2d.setColor(colorFromStatus(status));
            g2d.fillArc(x, y, size, size, accum_angle - 90, angle);
            accum_angle += angle;
        }
    }

    public static Color colorFromStatus(String status) {
        switch(status) {
            case "booked":
                return Universal.PASTEL_BLUE;
            case "boarded":
                return Universal.PASTEL_PURPLE;
            case "cancelled":
                return Universal.PASTEL_RED;
            case "expired":
                return Universal.PASTEL_GREY;
            default:
                return Universal.PASTEL_GREY;
        }   
    }
}

public class PieChartCard extends JPanel implements DataCard {

    private Supplier<List<TicketStatus>> dataSupplier;
    private List<TicketStatus> data;
    private PieChart chart = new PieChart();
    private String title;

    public PieChartCard(String title, Supplier<List<TicketStatus>> dataSupplier) {
        setLayout(new BorderLayout());
        setOpaque(false);

        this.dataSupplier = dataSupplier;
        this.title = title;
        loadData();

        add(createTitlePanel(), BorderLayout.NORTH);
        add(chart, BorderLayout.CENTER);
        add(createLegendPanel(), BorderLayout.EAST);

        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setMaximumSize(getPreferredSize());
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setOpaque(false);
        panel.add(UIFactory.createBoldLabel(title, 22));
        return panel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(Box.createVerticalGlue());
        for(TicketStatus ts: data) {
            String status = ts.getStatus();
            int count = ts.getCount();

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
            row.setOpaque(false);

            JButton icon = UIFactory.createButton();
            icon.setBorder(null);
            // icon.setBorderPainted(false);
            icon.setOpaque(true);
            icon.setEnabled(false);
            icon.setBackground(PieChart.colorFromStatus(status));
            icon.setPreferredSize(new Dimension(20, 20));
            row.add(icon);
            
            row.add(UIFactory.createPlainLabel(status + " (" + count + ")", 14));
            row.setMaximumSize(new Dimension(1000, row.getPreferredSize().height));
            panel.add(row);
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    public void loadData() {
        try {
            data = dataSupplier.get();
            chart.loadData(data);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
