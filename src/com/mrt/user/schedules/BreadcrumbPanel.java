package com.mrt.user.schedules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.mrt.factory.UIFactory;

public class BreadcrumbPanel extends JPanel {
    
    private List<JButton> btnList;

    public BreadcrumbPanel(SchedulesPanel parent) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setOpaque(false);

        btnList = new ArrayList<>();
        for(BookingStep step: BookingStep.values()) {
            JButton btn = createStepBtn(step);
            btnList.add(btn);
            add(btn);
            if(step.ordinal() < BookingStep.values().length - 1) {
                JButton arrow = UIFactory.createIconButton("src/com/mrt/img/caret-right.png", new Dimension(20, 20));
                arrow.setPreferredSize(new Dimension(10, 25));
                arrow.setVerticalAlignment(SwingConstants.TOP);
                arrow.setBorder(null);
                arrow.setContentAreaFilled(false);
                arrow.setEnabled(false);
                arrow.setFocusable(false);
                add(arrow);
            }
        }
    }

    private JButton createStepBtn(BookingStep step) {
        JButton btn = UIFactory.createButton(step.getLabel());
        btn.setFont(UIFactory.createDefaultPlainFont(16));
        btn.setEnabled(false);
        btn.setBorderPainted(false);
        btn.addActionListener(e -> {
            // parent.showStep(step);
        });
        return btn;
    }

    public void setCurrentStep(BookingStep step) {
        boolean pastCurrent = false;
        for(BookingStep curStep: BookingStep.values()) {
            JButton btn = btnList.get(curStep.ordinal());

            if(curStep == step) {
                btn.setEnabled(true);
                btn.setFont(UIFactory.createDefaultBoldFont(16));
                btn.setForeground(Color.BLUE);
                pastCurrent = true;
            }
            else if(!pastCurrent) {
                btn.setEnabled(true);
                btn.setFont(UIFactory.createDefaultPlainFont(16));
                btn.setForeground(Color.BLUE);
            }
            else {
                btn.setEnabled(false);
                btn.setFont(UIFactory.createDefaultPlainFont(16));
            }
        }
    }
}
