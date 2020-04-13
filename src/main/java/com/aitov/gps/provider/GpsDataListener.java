package com.aitov.gps.provider;

import java.util.List;

/**
 * Listener of gps events from GPS receiver
 *
 * @author Alexander Aitov
 */
public interface GpsDataListener {
    void updateSatelliteInfo(List<Satellite> satellites);

    void updatePosition(Position position);
}
