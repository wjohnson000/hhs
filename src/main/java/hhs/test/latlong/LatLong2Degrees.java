package hhs.test.latlong;

/**
 * Play around with methods that can convert miles or kilometers to degrees at different latitudes
 * and longitudes.  Obviously longitude doesn't really come in to play.
 * 
 * @author wjohnson000
 *
 */
public final class LatLong2Degrees {

    private static final double PI_HALF     = Math.PI / 2.0;

    public static final double KM_TO_MILES = 0.621371;
    public static final double MILES_TO_KM = 1.0 / KM_TO_MILES;

    public static final double CIRCUMFERENCE_KM = 40_075.0;
    public static final double CIRCUMFERENCE_MI = CIRCUMFERENCE_KM * KM_TO_MILES;

    private LatLong2Degrees() {
        // Private constructor to hide default one
    }

    public static double oneDegreeKM(double latitude, double longitude) {
        validateInput(latitude, longitude);
        return Math.cos(Math.abs(latitude * PI_HALF / 90)) * CIRCUMFERENCE_KM / 360;
    }

    public static double oneDegreeMI(double latitude, double longitude) {
        validateInput(latitude, longitude);
        return Math.cos(Math.abs(latitude * PI_HALF / 90)) * CIRCUMFERENCE_MI / 360;
    }

    public static double degreeKM(double latitude, double longitude, double radius) {
        double oneDegree = oneDegreeKM(latitude, longitude);
        double distance = radius / oneDegree;
        return (Math.sqrt(2 * distance * distance));
    }

    public static double degreeMI(double latitude, double longitude, double radius) {
        double oneDegree = oneDegreeMI(latitude, longitude);
        double distance = radius / oneDegree;
        return (Math.sqrt(2 * distance * distance));
    }

    private static void validateInput(double latitude, double longitude) {
        if (latitude < -90.0  ||  latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90.0 and 90.0");
        } else if (longitude < -180.0  ||  longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180.0 and 180.0");
        }
    }
}
