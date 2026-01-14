package com.mrt.admin.reports.cards;

import java.awt.BasicStroke;
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
import javax.swing.JPanel;

import com.mrt.Universal;
import com.mrt.admin.reports.models.LinePoint;
import com.mrt.factory.UIFactory;

class LineChart extends JPanel {

    List<LinePoint> data;
    public LineChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(323, 200));
    }

    public void loadData(List<LinePoint> data) {
        this.data = data;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(data.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padding = 30;
        int width = getWidth() - padding * 2;
        int height = getHeight() - padding * 2;
        int dataWidth = width - 20;
        int dataHeight = height - 20;

        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, padding, padding, padding + height);
        g2d.drawLine(padding, padding + height, padding + width, padding + height);

        int arrowWidth = 10;
        int arrowHeight = 5;
        g2d.fillPolygon(
            new int[] {padding, padding - arrowWidth/2, padding + arrowWidth/2},
            new int[] {padding - arrowHeight, padding, padding},
            3
        );
        g2d.fillPolygon(
            new int[] {padding + width + arrowHeight, padding + width, padding + width},
            new int[] {padding + height, padding + height - arrowWidth/2, padding + height + arrowWidth/2},
            3
        );

        int maxY = 0, stepX = dataWidth / (data.size() - 1);
        int prevX = -1, prevY = -1;
        for(LinePoint lp: data) {
            maxY = Math.max(maxY, lp.getValue());
        }
        int yDiv = 8;
        Color transparentBlack = new Color(0, 0, 0, 50);
        for(int i = 1; i <= yDiv; i++) {
            int y = padding + (int) (height - (double) dataHeight / (double) yDiv * (double) i);

            g2d.setColor(Color.BLACK);
            g2d.drawLine(padding - 3, y, padding + 3, y);
            g2d.setColor(transparentBlack);
            g2d.drawLine(padding + 10, y, padding + dataWidth, y);
        }

        for(int i = 0; i < data.size(); i++) {
            LinePoint lp = data.get(i);
            
            int x = padding + i * stepX;
            int y = padding + (height - (int) ((double) lp.getValue() / (double) maxY * (double) dataHeight));
            
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke());
            if(i > 0) {
                g2d.drawLine(x, padding + height + 3, x, padding + height - 3);
            }
            
            g2d.setColor(Universal.PASTEL_BLUE);
            g2d.setStroke(new BasicStroke(2f));
            if(i > 0) {
                g2d.drawLine(prevX, prevY, x, y);
            }
            g2d.fillOval(x - 4, y - 4, 8, 8);

            prevX = x;
            prevY = y;
        }
    }
}

public class LineChartCard extends JPanel implements DataCard {

    private Supplier<List<LinePoint>> dataSupplier;
    private List<LinePoint> data;
    private LineChart lineChart = new LineChart();
    private String title;

    public LineChartCard(String title, Supplier<List<LinePoint>> dataSupplier) {
        setLayout(new BorderLayout());
        setOpaque(false);

        this.dataSupplier = dataSupplier;
        this.title = title;
        loadData();

        add(createTitlePanel(), BorderLayout.NORTH);
        add(lineChart, BorderLayout.CENTER);

        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panel.setOpaque(false);
        panel.add(UIFactory.createBoldLabel(title, 22));
        return panel;
    }

    public void loadData() {
        try {
            data = dataSupplier.get();
            lineChart.loadData(data);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
