package com.aitov.gps.provider;

import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.util.SatelliteInfo;
import net.sf.marineapi.provider.PositionProvider;
import net.sf.marineapi.provider.SatelliteInfoProvider;
import net.sf.marineapi.provider.event.PositionEvent;
import net.sf.marineapi.provider.event.PositionListener;
import net.sf.marineapi.provider.event.SatelliteInfoEvent;
import net.sf.marineapi.provider.event.SatelliteInfoListener;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MarineAPI gps data provider implementation
 */
public class MarineApiDataProvider implements GpsDataProvider {
    // for BU-353S4 GPS receiver we need timeout more than 1 second, 5 seconds enough
    public static final int MESSAGE_TIMEOUT = 5000;
    // set to true for debug information
    private static final boolean DEBUG = false;

    private SentenceReader reader;
    private GpsDataListener listener;

    public MarineApiDataProvider(GpsDataListener listener) {
        this.listener = listener;
    }

    private void updateSatelliteInfo(SatelliteInfoEvent event) {
        if (event != null && listener != null) {
            List<Satellite> satellites = new ArrayList<>();
            for (SatelliteInfo info : event.getSatelliteInfo()) {
                Satellite satellite = new Satellite(info.getId(), info.getElevation(), info.getAzimuth(), info.getNoise());
                satellites.add(satellite);
            }
            listener.updateSatelliteInfo(satellites);
        }
    }


    private void updatePosition(PositionEvent event) {
        if (event != null && listener != null) {
            LocalDateTime date = LocalDateTime.of(event.getDate().getYear(), event.getDate().getMonth(),
                    event.getDate().getDay(), event.getTime().getHour(), event.getTime().getMinutes(), (int) event.getTime().getSeconds());
            Position position = new Position(date, event.getSpeed(), event.getPosition().getLatitude(),
                    event.getPosition().getLongitude(), event.getPosition().getAltitude());
            listener.updatePosition(position);
        }
    }

    @Override
    public void startReading(InputStream inputStream) {
        reader = new SentenceReader(inputStream);
        PositionProvider posProvider = new PositionProvider(reader);
        GpsPositionListener listener = new GpsPositionListener(this);
        posProvider.addListener(listener);
        SatelliteInfoProvider satelliteInfoProvider = new SatelliteInfoProvider(reader);
        satelliteInfoProvider.setTimeout(MESSAGE_TIMEOUT);
        GpsSatellitesListener satellitesListener = new GpsSatellitesListener(this);
        satelliteInfoProvider.addListener(satellitesListener);
        reader.start();
    }

    @Override
    public void stopReading() {
        reader.stop();
    }

    @Override
    public GpsDataListener getDataListener() {
        return listener;
    }

    @Override
    public boolean validData(String sentence) {
        return SentenceValidator.isValid(sentence);
    }

    private static class GpsPositionListener implements PositionListener {
        private MarineApiDataProvider provider;

        public GpsPositionListener(MarineApiDataProvider provider) {
            this.provider = provider;
        }

        @Override
        public void providerUpdate(PositionEvent event) {
            if (DEBUG) {
                System.out.println(event);
            }
            provider.updatePosition(event);
        }
    }

    private static class GpsSatellitesListener implements SatelliteInfoListener {
        private MarineApiDataProvider provider;

        public GpsSatellitesListener(MarineApiDataProvider provider) {
            this.provider = provider;
        }

        @Override
        public void providerUpdate(SatelliteInfoEvent event) {
            if (DEBUG) {
                logFormattedInfo(event);
            }
            provider.updateSatelliteInfo(event);
        }

        private void logFormattedInfo(SatelliteInfoEvent event) {
            System.out.println("-- GSV report --");
            for (SatelliteInfo si : event.getSatelliteInfo()) {
                String ptrn = "%s: %d, %d, %d";
                String msg = String.format(ptrn, si.getId(), si.getAzimuth(), si
                        .getElevation(), si.getNoise());
                System.out.println(msg);
            }
            System.out.println("-----");
        }
    }
}
