package com.aitov.gps.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main window with panels
 *
 * @author Alexander Aitov
 */
public class GpsWindow {
    public static final String CONNECT_TEXT = "Connect";
    public static final String DISCONNECT_TEXT = "Disconnect";
    private JPanel mainPanel;
    private JTextField coordinatesLabel;
    private JLabel altitudeLabel;
    private JLabel timeLabel;
    private JPanel statusPanel;
    private JButton startButton;
    private JLabel speedLabel;
    private JPanel positionPanel;
    private JPanel mapPanel;
    private JComboBox<String> availablePorts;
    private GpsDataController controller;

    public GpsWindow() {
        controller = new GpsDataController(coordinatesLabel, altitudeLabel, timeLabel, speedLabel,
                (SatelliteSignalPanel) statusPanel, (SatellitePositionPanel) positionPanel, (MapPanel) mapPanel);
        ComboBoxModel<String> model = new DefaultComboBoxModel<>(controller.getAvailablePorts());
        availablePorts.setModel(model);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (CONNECT_TEXT.equals(startButton.getText())) {
                    String portName = (String) availablePorts.getSelectedItem();
                    if (portName != null && !portName.isEmpty()) {
                        try {
                            controller.connectToPort(portName);
                            startButton.setText(DISCONNECT_TEXT);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(mainPanel.getParent(), ex.getMessage());
                        }
                    }
                } else {
                    controller.disconnectFromPort();
                    startButton.setText(CONNECT_TEXT);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GpsWindow");
        GpsWindow window = new GpsWindow();
        frame.setContentPane(window.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(700, 550);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    private void createUIComponents() {
        statusPanel = new SatelliteSignalPanel();
        positionPanel = new SatellitePositionPanel();
        mapPanel = new MapPanel();
    }
}
