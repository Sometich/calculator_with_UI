package calculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Calculator extends JFrame {
    Function functions = new Function();

    public Calculator() {
        initWindow();
    }

    private void initWindow() {
        setWindowProperties();
        this.add(functions.createDisplayPanel());
        this.add(createButtonPanel());
        this.setVisible(true);
    }

    private void setWindowProperties() {
        this.setTitle("Calculator");
        this.setSize(new Dimension(250, 550));
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(6, 4, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        for (Buttons button : Buttons.values()) {
            buttonPanel.add(createButton(button));
        }
        return buttonPanel;
    }

    private JButton createButton(final Buttons button) {
        JButton jButton = new JButton(button.getText());
        jButton.setName(button.getName());
        jButton.setVisible(button.isVisible());
        jButton.setBackground(button.getColor());
        jButton.setFont(new Font("SANS_SERIF", Font.BOLD, 16));
        jButton.setMargin(new Insets(5, 5, 5, 5));
        jButton.addActionListener(button.getListener());
        return jButton;
    }
}