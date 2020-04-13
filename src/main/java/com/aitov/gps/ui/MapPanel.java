package com.aitov.gps.ui;

import com.aitov.gps.provider.Position;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * World map panel with current position (if available)
 *
 * @author Alexander Aitov
 */
public class MapPanel extends JPanel {
    private final static int IMAGE_WIDTH = 320;
    private final static int IMAGE_HEIGHT = 160;
    public static final int OFFSET = 5;
    public static final int POINT_DIAMETER = 3;
    public static final String WORLD_MAP_FILE_NAME = "world_map.png";

    private Position position;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image mapImage = readImage();
        if (mapImage != null) {
            g.drawImage(mapImage, OFFSET, OFFSET, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            if (position != null) {
                Point coordinates = convertToImageCoordinates(position);
                g.drawLine(OFFSET + coordinates.x - POINT_DIAMETER, OFFSET + coordinates.y,
                        OFFSET + coordinates.x + POINT_DIAMETER, OFFSET + coordinates.y);
                g.drawLine(OFFSET + coordinates.x, OFFSET + coordinates.y - POINT_DIAMETER,
                        OFFSET + coordinates.x, OFFSET + coordinates.y + POINT_DIAMETER);
            }
        }
    }

    private Point convertToImageCoordinates(Position position) {
        double xFactor = IMAGE_WIDTH / 360.0;
        double yFactor = IMAGE_HEIGHT / 180.0;

        double dX = position.getLongitude() * xFactor;
        double dY = position.getLatitude() * yFactor;
        int x = (int) (IMAGE_WIDTH / 2 + dX);
        int y = (int) (IMAGE_HEIGHT / 2 - dY);
        return new Point(x, y);
    }

    public void updatePosition(Position position) {
        this.position = position;
        getParent().repaint();
    }

    private BufferedImage readImage() {
        try {
            return ImageIO.read(getClass().getClassLoader().getResourceAsStream(WORLD_MAP_FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
