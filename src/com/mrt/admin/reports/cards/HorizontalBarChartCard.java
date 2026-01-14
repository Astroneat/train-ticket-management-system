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
import javax.swing.JPanel;

import com.mrt.Universal;
import com.mrt.admin.reports.models.BarData;
import com.mrt.factory.UIFactory;

class BarChart extends JPanel {

    private List<BarData> data;
    public BarChart() {
        setPreferredSize(new Dimension(500, 400));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(data.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int topPadding = 30;
        int bottomPadding = 30;
        int leftPadding = 100;
        int rightPadding = 30;
        int width = getWidth() - leftPadding - rightPadding;
        int height = getHeight() - topPadding - bottomPadding;
        int dataWidth = width - 30;
        int dataHeight = height - 30;

        g2d.setColor(Color.BLACK);
        g2d.drawLine(leftPadding, topPadding, leftPadding, topPadding + height);
        g2d.drawLine(leftPadding, topPadding + height, leftPadding + width, topPadding + height);

        int arrowWidth = 10;
        int arrowHeight = 5;
        g2d.fillPolygon(
            new int[] {leftPadding, leftPadding - arrowWidth/2, leftPadding + arrowWidth/2},
            new int[] {topPadding - arrowHeight, topPadding, topPadding},
            3
        );
        g2d.fillPolygon(
            new int[] {leftPadding + width + arrowHeight, leftPadding + width, leftPadding + width},
            new int[] {topPadding + height, topPadding + height - arrowWidth/2, topPadding + height + arrowWidth/2},
            3
        );

        int maxX = 0, stepY = dataHeight / data.size();
        for(BarData bd: data) {
            maxX = Math.max(maxX, bd.getValue());
        }
        int xDiv = 10;
        Color transparentBlack = new Color(0, 0, 0, 50);
        for(int i = 1; i <= xDiv; i++) {
            int x = leftPadding + (int) ((double) dataWidth / (double) xDiv * (double) i);

            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, topPadding + height - 3, x, topPadding + height + 3);
            g2d.setColor(transparentBlack);
            g2d.drawLine(x, topPadding, x, topPadding + height - 10);
        }

        g2d.setFont(UIFactory.createDefaultPlainFont(14));
        for(int i = 0; i < data.size(); i++) {
            BarData bd = data.get(i);
            
            // int x = padding + i * stepX;
            // int y = padding + (height - (int) ((double) lp.getValue() / (double) maxY * (double) dataHeight));
            int y = topPadding + (height - dataHeight) + i * stepY;
            int xWidth = (int) ((double) bd.getValue() / (double) maxX * (double) dataWidth);
            int rectHeight = 16;

            g2d.setColor(Universal.PASTEL_BLUE);
            g2d.fillRect(leftPadding, y - rectHeight/2, xWidth, rectHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawString(bd.getValue() + "", leftPadding + xWidth + 10, y + rectHeight/4);
            g2d.drawString(bd.getLabel(), leftPadding - 70, y + rectHeight/4);
        }
    }

    public void loadData(List<BarData> data) {
        this.data = data;
    }
}

public class HorizontalBarChartCard extends JPanel implements DataCard {
    
    private Supplier<List<BarData>> dataSupplier;
    private List<BarData> data;
    private BarChart barChart = new BarChart();
    private String title;

    public HorizontalBarChartCard(String title, Supplier<List<BarData>> dataSupplier) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        this.dataSupplier = dataSupplier;
        this.title = title;
        loadData();

        add(createTitlePanel(), BorderLayout.NORTH);
        add(barChart, BorderLayout.CENTER);
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
            barChart.loadData(data);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
