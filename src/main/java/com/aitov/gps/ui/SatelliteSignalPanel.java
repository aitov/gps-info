package com.aitov.gps.ui;

import com.aitov.gps.provider.Satellite;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Satellite signal quality panel
 *
 * @author Alexander Aitov
 */
public class SatelliteSignalPanel extends JPanel {
    public static final int MAX_BAR_HEIGHT = 180;
    public static final Color GREEN_COLOR = new Color(0, 160, 0);
    public static final int OFFSET = 5;
    public static final int MIN_SIGNAL_VALUE = 6;
    public static final Color LIGHT_BLUE_COLOR = new Color(51, 153, 255);
    public static final int MAX_SATELILTES = 20;
    private List<Satellite> satellites = new ArrayList<>();


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        FontMetrics metrics = g.getFontMetrics(this.getFont());
        int textHeight = metrics.getHeight();
        int width = getWidth() - 10;

        // empty rectangle for bars and satellite ids
        g.setColor(Color.WHITE);
        g.fillRect(OFFSET, OFFSET, width, MAX_BAR_HEIGHT + textHeight + OFFSET);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(OFFSET, OFFSET, width, MAX_BAR_HEIGHT + textHeight + OFFSET);


        // satellite status
        int barWith = 0;
        if (satellites.size() > 0) {
            barWith = width / (Math.min(satellites.size(), MAX_SATELILTES));
        }

        int i = 0;
        for (Satellite satellite : satellites) {
            // limit to 20 satellites
            if (i > MAX_SATELILTES) {
                break;
            }
            int delta = MAX_BAR_HEIGHT / 60;
            int barHeight = satellite.getNoise() * delta;

            // draw current signal bar
            Color barColor = satellite.getNoise() >= 30 ? GREEN_COLOR : Color.LIGHT_GRAY;
            g.setColor(barColor);
            g.fillRect((barWith * i) + OFFSET, MAX_BAR_HEIGHT - barHeight + OFFSET, barWith, barHeight);
            g.setColor(Color.BLACK);
            g.drawRect((barWith * i) + OFFSET, MAX_BAR_HEIGHT - barHeight + OFFSET, barWith, barHeight);

            // draw full bar
            g.drawRect((barWith * i) + OFFSET, OFFSET, barWith, MAX_BAR_HEIGHT);

            // draw signal value
            if (satellite.getNoise() >= MIN_SIGNAL_VALUE) {
                int textWith = metrics.stringWidth(String.valueOf(satellite.getNoise()));
                g.setColor(Color.WHITE);
                g.drawString(String.valueOf(satellite.getNoise()), barWith * i + OFFSET + barWith / 2 - textWith / 2, MAX_BAR_HEIGHT - barHeight + OFFSET + textHeight);
            }

            // rectangle for satellite ids
            g.setColor(LIGHT_BLUE_COLOR);
            g.fillRect((barWith * i) + OFFSET, OFFSET + MAX_BAR_HEIGHT, barWith, textHeight + OFFSET);
            g.setColor(Color.BLACK);
            g.drawRect((barWith * i) + OFFSET, OFFSET + MAX_BAR_HEIGHT, barWith, textHeight + OFFSET);

            // draw satellite id
            int textWith = metrics.stringWidth(String.valueOf(satellite.getId()));
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(satellite.getId()), barWith * i + OFFSET + barWith / 2 - textWith / 2, MAX_BAR_HEIGHT + OFFSET + textHeight);
            i++;
        }
    }

    public void updateSatellites(List<Satellite> satellites) {
        if (satellites != null) {
            this.satellites.clear();
            this.satellites.addAll(satellites);
            getParent().repaint();
        }
    }
}
