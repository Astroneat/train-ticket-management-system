package com.mrt.factory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerDateModel;
import javax.swing.border.Border;

import com.mrt.Universal;

public class UIFactory {

    public static Font createDefaultPlainFont(int size) {
        return new Font(Universal.defaultFontFamily, Font.PLAIN, size);
    }
    public static Font createDefaultBoldFont(int size) {
        return new Font(Universal.defaultFontFamily, Font.BOLD, size);
    }
    public static Border createDefaultBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
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

    public static JLabel createItalicLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Universal.defaultFontFamily, Font.ITALIC, size));
        return label;
    }

    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(createDefaultPlainFont(14));
        field.setBorder(createDefaultBorder());
        return field;
    }

    public static JTextField createTextField() {
        return createTextField(0);
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(createDefaultPlainFont(14));
        field.setBorder(createDefaultBorder());
        return field;
    }

    public static JPasswordField createPasswordField() {
        return createPasswordField(0);
    }

    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(createDefaultPlainFont(14));
        btn.setFocusable(false);
        return btn;
    }

    public static JButton createButton() {
        return createButton("");
    }

    public static JLabel createImageLabel(String imageFileDir, Dimension iconDim) {
        JLabel label = new JLabel();
        try {
            ImageIcon imgIcon = new ImageIcon(imageFileDir);
            Image scaledImg = imgIcon.getImage().getScaledInstance((int) iconDim.getHeight(), (int) iconDim.getWidth(), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImg));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return label;
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

    public static JToggleButton createToggleButton(String text) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(createDefaultPlainFont(14));
        btn.setFocusable(false);
        return btn;
    }

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(createDefaultPlainFont(14));
        comboBox.setFocusable(false);
        return comboBox;
    }

    public static <T> JComboBox<T> createComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(createDefaultPlainFont(14));
        comboBox.setFocusable(false);
        return comboBox;
    }

    public static JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(createDefaultPlainFont(14));
        checkBox.setFocusable(false);
        return checkBox;
    }

    public static JCheckBox createCheckBox() {
        return createCheckBox("");
    }

    public static JSpinner createDateTimeSpinner(String pattern, LocalDateTime defaultTime) {
        Date date = Date.from(defaultTime.atZone(ZoneId.systemDefault()).toInstant());
        SpinnerDateModel spinnerModel = new SpinnerDateModel(date, null, null, Calendar.MINUTE);
        JSpinner spinner = new JSpinner(spinnerModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, pattern);
        spinner.setFont(new Font(Universal.defaultFontFamily, Font.PLAIN, 14));
        editor.getTextField().setBorder(createDefaultBorder());
        spinner.setEditor(editor);
        return spinner;
    }

    public static JSpinner createDateTimeSpinner(String pattern) {
        return createDateTimeSpinner(pattern, (new Date().toInstant()).atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static JTextArea createTextArea(int rows, int cols) {
        JTextArea textArea = new JTextArea(rows, cols);
        textArea.setFont(createDefaultPlainFont(14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(createDefaultBorder());
        return textArea;
    }

    public static JTextArea createTextArea() {
        return createTextArea(0, 0);
    }
}
