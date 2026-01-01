package com.mrt.factory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.mrt.Universal;

public class UIFactory {

    private static Font createDefaultPlainFont(int size) {
        return new Font(Universal.defaultFontFamily, Font.PLAIN, size);
    }
    private static Font createDefaultBoldFont(int size) {
        return new Font(Universal.defaultFontFamily, Font.BOLD, size);
    }
    
    public static JLabel createPlainLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(createDefaultPlainFont(size));
        return label;
    }

    public static JLabel createBoldLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(createDefaultBoldFont(size));
        return label;
    }

    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(createDefaultPlainFont(14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    public static JTextField createTextField() {
        return createTextField(0);
    }

    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(createDefaultPlainFont(14));
        btn.setFocusable(false);
        return btn;
    }

    public static JButton createIconButton(String imageFileDir, Dimension iconDim) {
        JButton btn = new JButton();
        try {
            ImageIcon imgIcon = new ImageIcon(imageFileDir);
            Image scaledImg = imgIcon.getImage().getScaledInstance((int) iconDim.getHeight(), (int) iconDim.getWidth(), Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaledImg));
        } catch(Exception e) {
            e.printStackTrace();
        }
        btn.setFocusable(false);
        return btn;
    }

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(createDefaultPlainFont(14));
        return comboBox;
    }

    public static <T> JComboBox<T> createComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(createDefaultPlainFont(14));
        return comboBox;
    }

    public static JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(createDefaultPlainFont(14));
        return checkBox;
    }
}
