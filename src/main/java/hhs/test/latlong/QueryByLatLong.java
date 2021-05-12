package hhs.test.latlong;

import com.datastax.dse.driver.api.core.data.geometry.Point;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import hhs.utility.SessionUtilityAwsDev;

/**
 * Run a bunch of queries against the Cassandra database (after running {@link LoadLatLongValues} to load the data) to see
 * what you can see.  For more information, see the Confluence page:
 * <br>https://fhconfluence.churchofjesuschrist.org/pages/viewpage.action?pageId=188296164
 * 
 * <p>NOTE: the pipe (|) delimiter in the Solr query is an artificial value.  It will be replaced by a double-quote (") before
 * the query actually happens!
 * 
 * @author wjohnson000
 *
 */
public class QueryByLatLong {

    static final String LAT_LONG_QUERY = "SELECT * FROM hhs.rep_location_search where solr_query = ' { |q|: |*:*|, |fq|: |lat_long:\\|IsWithin(BUFFER(POINT(%f %f), %f))\\||, |paging|: |driver| } ' LIMIT 200";
    static final double BLGTN_LAT  = 39.1653;
    static final double BLGTN_LONG = -86.5264;

    static final double[] distances = {
        3.7,
        7.3,
        12.7,
        17.2,
        25.5
    };

    public static void main(String...args) {
        CqlSession cqlSession = SessionUtilityAwsDev.connect();
        System.out.println("SESS: " + cqlSession);

        boolean more = true;
        double  degree = 0.001;
        while (more) {
            System.out.println("\n=================================================================");
            System.out.println("degrees=" + degree);
            String query = String.format(LAT_LONG_QUERY, BLGTN_LAT, BLGTN_LONG, degree).replace('|', '"');
            System.out.println("qy: " + query);

            int count = 0;
            ResultSet rset = cqlSession.execute(query);
            for (Row row : rset) {
                count++;
                String repId = row.getString("rep_id");
                // TODO: do something with "repId" ...
            }
            System.out.println("count: " + count);

            degree += 0.001;
            more = (count < 180  &&  degree < 2.0);
        }

        for (double distance : distances) {
            double degreeX = LatLong2Degrees.degreeKM(BLGTN_LAT, BLGTN_LONG, distance) / 5.0;
            System.out.println("\n=================================================================");
            System.out.println("km=" + distance + " --> degrees=" + degreeX);
            String query = String.format(LAT_LONG_QUERY, BLGTN_LAT, BLGTN_LONG, degreeX).replace('|', '"');
            System.out.println("qy: " + query);

            ResultSet rset = cqlSession.execute(query);
            for (Row row : rset) {
                String repId = row.getString("rep_id");
                Object what  = row.getObject("lat_long");
                Point latLong = (Point)what;
                System.out.println("Rep: " + repId + " --> " + latLong.X() + "," + latLong.Y());
            }
        }

        System.exit(0);
    }
}
