package com.mrt.admin.reports.cards;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mrt.factory.UIFactory;

public class KPICard extends JPanel implements DataCard {

    private Supplier<Object> dataSupplier;
    private JLabel titleLabel;
    private JLabel valueLabel;

    public KPICard(String title, Color cardColor, Supplier<Object> dataSupplier) {
        this.dataSupplier = dataSupplier;

        // setOpaque(false);
        setLayout(new BorderLayout(0, 8));
        setBackground(cardColor);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        titleLabel = UIFactory.createPlainLabel(title, 20);
        valueLabel = UIFactory.createBoldLabel("", 40);

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void loadData() {
        try {
            valueLabel.setText(dataSupplier.get().toString());
        } catch(Exception e) {
            valueLabel.setText("N/A");
        }
    }
}
