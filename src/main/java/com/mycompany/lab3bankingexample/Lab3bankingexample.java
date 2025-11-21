package com.mycompany.lab3bankingexample;

import com.mycompany.lab3bankingexample.GUI.LoginGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Lab3bankingexample {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginGUI());
    }

}
