package com.mrt.frames;
import javax.swing.JFrame;

import com.mrt.user.schedules.Page;

public interface MyFrame {
    public void goToPage(Page page);
    public void logout();
    public JFrame getJFrame();
}
