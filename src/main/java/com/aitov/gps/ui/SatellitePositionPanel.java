package com.aitov.gps.ui;

import com.aitov.gps.provider.Satellite;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Satellites position panel, paints satellites on the sky
 *
 * @author Alexander Aitov
 */
public class SatellitePositionPanel extends JPanel {

    public static final int BIG_DIAMETER = 200;
    public static final int SMALL_DIAMETER = 100;
    public static final Color GREEN_COLOR = new Color(0, 160, 0);
    private java.util.List<Satellite> satellites = new ArrayList<>();

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font(getFont().getName(), Font.PLAIN, 7));

        // draw big and small circle
        Rectangle bigRectangle = new Rectangle(getWidth() / 2 - BIG_DIAMETER / 2, 5, BIG_DIAMETER, BIG_DIAMETER);
        g.drawOval(getWidth() / 2 - BIG_DIAMETER / 2, 5, BIG_DIAMETER, BIG_DIAMETER);
        g.drawOval(getWidth() / 2 - SMALL_DIAMETER / 2, BIG_DIAMETER / 2 - SMALL_DIAMETER / 2 + 5, SMALL_DIAMETER, SMALL_DIAMETER);

        // draw cross lines
        g.drawLine(getWidth() / 2, 5, getWidth() / 2, 5 + BIG_DIAMETER);
        g.drawLine(getWidth() / 2 - BIG_DIAMETER / 2, 5 + BIG_DIAMETER / 2, getWidth() / 2 + BIG_DIAMETER / 2, 5 + BIG_DIAMETER / 2);

        // draw satellites

        for (Satellite info : satellites) {
            if (info.getAzimuth() != 0 && info.getElevation() != 0) {
                Point point = translateToPointOnCircle(bigRectangle, info.getAzimuth(), info.getElevation());
                Color satelliteColor = info.getNoise() >= 30 ? GREEN_COLOR : Color.LIGHT_GRAY;
                g.setColor(satelliteColor);
                g.fillOval(point.x - 16 / 2, point.y - 16 / 2, 16, 16);
                g.setColor(Color.BLACK);
                g.drawOval(point.x - 16 / 2, point.y - 16 / 2, 16, 16);
                int offsetX = info.getId().length() > 2 ? 6 : 4;
                g.drawString(String.valueOf(info.getId()), point.x - offsetX, point.y + 3);
            }
        }

    }

    public void updateSatellites(List<Satellite> satellites) {
        if (satellites != null) {
            this.satellites.clear();
            this.satellites.addAll(satellites);
            getParent().repaint();
        }
    }


    private Point translateToPointOnCircle(Rectangle rectangle, int azimuth, int elevation) {
        double radian = ((double) azimuth / 180.0 * Math.PI);
        double dX = Math.sin(radian);
        double dY = -Math.cos(radian);
        double ratio = (rectangle.width / 2.0) / 90.0;

        int x = (int) rectangle.getCenterX() + (int) (dX * (90.0 - (double) elevation) * ratio);
        int y = (int) rectangle.getCenterY() + (int) (dY * (90.0 - (double) elevation) * ratio);
        return new Point(x, y);
    }
}
