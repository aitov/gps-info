package com.aitov.gps.ui;

import com.aitov.gps.provider.*;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Data controller for window elements, listening gps data events
 *
 * @author Alexander Aitov
 */
public class GpsDataController implements GpsDataListener {
    // update timeout for coordinates label, enough time for copy text from text field
    public static final int UPDATE_TIMEOUT = 10 * 1000;
    public static final String LOADING_TEXT = "Loading...";
    public static final String NOT_AVAILABLE_TEXT = "Not Available";

    private JTextField coordinatesLabel;
    private JLabel altitudeLabel;
    private JLabel timeLabel;
    private JLabel speedLabel;
    private SatelliteSignalPanel satelliteSignalPanel;
    private SatellitePositionPanel satellitePositionPanel;
    private MapPanel mapPanel;
    private long lastCoordinatesUpdate;

    private InputStream inputStream;
    private SerialPort serialPort;
    private GpsDataProvider gpsDataProvider;

    public GpsDataController(JTextField coordinatesLabel, JLabel altitudeLabel, JLabel timeLabel, JLabel speedLabel, SatelliteSignalPanel satelliteSignalPanel,
                             SatellitePositionPanel satellitePositionPanel, MapPanel mapPanel) {
        // set implementation of marine api library
        gpsDataProvider = new MarineApiDataProvider(this);
        this.coordinatesLabel = coordinatesLabel;
        this.altitudeLabel = altitudeLabel;
        this.timeLabel = timeLabel;
        this.speedLabel = speedLabel;
        this.satelliteSignalPanel = satelliteSignalPanel;
        this.satellitePositionPanel = satellitePositionPanel;
        this.mapPanel = mapPanel;

    }

    public void connectToPort(String portName) {
        try {
            SerialPort sp = getSerialPort(portName);
            if (sp != null) {
                coordinatesLabel.setText(LOADING_TEXT);
                inputStream = sp.getInputStream();
                gpsDataProvider.startReading(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void disconnectFromPort() {
        gpsDataProvider.stopReading();
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serialPort.close();
        cleanupView();
    }

    private void cleanupView() {
        satellitePositionPanel.updateSatellites(Collections.emptyList());
        satelliteSignalPanel.updateSatellites(Collections.emptyList());
        mapPanel.updatePosition(null);
        coordinatesLabel.setText(NOT_AVAILABLE_TEXT);
        timeLabel.setText("");
        speedLabel.setText("");
        altitudeLabel.setText("");
    }

    public void updatePosition(Position position) {
        long currentTime = System.currentTimeMillis();
        if (lastCoordinatesUpdate == 0 || LOADING_TEXT.equals(coordinatesLabel.getText())
                || (currentTime - lastCoordinatesUpdate) > UPDATE_TIMEOUT) {
            lastCoordinatesUpdate = currentTime;
            coordinatesLabel.setText(formatCoordinates(position));
        }
        altitudeLabel.setText(position.getAltitude() + " m");
        timeLabel.setText(formatDate(position.getDate()));
        speedLabel.setText(String.valueOf(String.format("%.2f km/h", position.getSpeed())));
        mapPanel.updatePosition(position);
    }

    public void updateSatelliteInfo(List<Satellite> satellites) {
        satelliteSignalPanel.updateSatellites(satellites);
        satellitePositionPanel.updateSatellites(satellites);
    }

    public String[] getAvailablePorts() {
        List<String> ports = new ArrayList<>();
        Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
        while (e.hasMoreElements()) {
            CommPortIdentifier id = (CommPortIdentifier) e.nextElement();
            if (id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                ports.add(id.getName());
            }
        }
        return ports.toArray(new String[0]);
    }

    private SerialPort getSerialPort(String serialPortName) {
        try {
            Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();

            while (e.hasMoreElements()) {
                CommPortIdentifier id = (CommPortIdentifier) e.nextElement();

                if (id.getName().equals(serialPortName) && id.getPortType() == CommPortIdentifier.PORT_SERIAL) {

                    serialPort = id.open("SerialPort", 30);

                    serialPort.setSerialPortParams(4800, SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    serialPort.enableReceiveTimeout(1000);
                    serialPort.enableReceiveThreshold(0);

                    InputStream is = serialPort.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader buf = new BufferedReader(isr);

                    System.out.println("Scanning port " + serialPort.getName());

                    // try each port few times before giving up
                    for (int i = 0; i < 5; i++) {
                        try {
                            String data = buf.readLine();
                            if (gpsDataProvider.validData(data)) {
                                System.out.println("NMEA Data found!");
                                return serialPort;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    is.close();
                    isr.close();
                    buf.close();
                    serialPort.close();
                }
            }

            System.out.println("NMEA data was not found..");
            throw new RuntimeException("NMEA data was not found");

        } catch (IOException | PortInUseException | UnsupportedCommOperationException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String formatCoordinates(Position position) {
        DecimalFormat df = new DecimalFormat("00.0000000");
        StringBuilder sb = new StringBuilder();
        sb.append(df.format(Math.abs(position.getLatitude())));
        sb.append(" ");
        sb.append(position.getLatitude() >= 0.0 ? "N" : "S");
        sb.append(", ");
        df.applyPattern("000.0000000");
        sb.append(df.format(Math.abs(position.getLongitude())));
        sb.append(" ");
        sb.append(position.getLongitude() >= 0.0 ? "E" : "W");
        return sb.toString();
    }

    public String formatDate(LocalDateTime date) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(date, ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).format(formatter);
    }
}
