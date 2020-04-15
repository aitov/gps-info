package com.aitov.gps.provider;

import java.io.InputStream;

/**
 * Abstract GPS data provider
 */
public interface GpsDataProvider {
    boolean validData(String sentence);

    void startReading(InputStream inputStream);

    void stopReading();

    GpsDataListener getDataListener();
}
