package hhs.test.latlong;

/**
 * Test the {@LatLong2Degrees} class.
 * @author wjohnson000
 *
 */
public class LatLong2DegreesTest {

    public static void main(String... args) {
        System.out.println("OneDegKM @  0.0, 0.0: " + LatLong2Degrees.oneDegreeKM(0.0, 0.0));
        System.out.println("OneDegMI @  0.0, 0.0: " + LatLong2Degrees.oneDegreeMI(0.0, 0.0));

        System.out.println("OneDegKM @ 40.0, 0.0: " + LatLong2Degrees.oneDegreeKM(40.0, 0.0));
        System.out.println("OneDegMI @ 40.0, 0.0: " + LatLong2Degrees.oneDegreeMI(40.0, 0.0));

        System.out.println("OneDegKM @ 50.0, 0.0: " + LatLong2Degrees.oneDegreeKM(50.0, 0.0));
        System.out.println("OneDegMI @ 50.0, 0.0: " + LatLong2Degrees.oneDegreeMI(50.0, 0.0));

        System.out.println("OneDegKM @ 89.0, 0.0: " + LatLong2Degrees.oneDegreeKM(89.0, 0.0));
        System.out.println("OneDegMI @ 89.0, 0.0: " + LatLong2Degrees.oneDegreeMI(89.0, 0.0));

        System.out.println("DegreeKM @  0.0, 0.0: " + LatLong2Degrees.degreeKM(0.0, 0.0, 10.0));
        System.out.println("DegreeMI @  0.0, 0.0: " + LatLong2Degrees.degreeMI(0.0, 0.0, 6.0));

        System.out.println("DegreeKM @ 40.0, 0.0: " + LatLong2Degrees.degreeKM(40.0, 0.0, 10.0));
        System.out.println("DegreeMI @ 40.0, 0.0: " + LatLong2Degrees.degreeMI(40.0, 0.0, 6.0));
    }
}
