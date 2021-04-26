/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.test.latlong;

import java.util.*;
import java.util.stream.Collectors;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BatchableStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;

import hhs.utility.SessionUtilityAwsDev;

/**
 * Load a bunch of LAT/LONG values into the "hhs.rep_location_search" table.  There are four sets of data
 * to test against.  All are relative to Bloomington, IN: repId=4049054, Lat/Long=39.1653,-86.5264
 * <ul>
 *   <li>3.7KM -- reps with 3.7 KM of Bloomington IN</li>
 *   <li>7.3 KM -- reps within 7.3 KM of Bloomington, IN</li>
 *   <li>12.7 KM -- reps within 12.7 KM of Bloomington, IN</li>
 *   <li>17.2 KM -- reps within 17.2 KM of Bloomington, IN</li>
 * </ul>
 * 
 * NOTE: this presupposes the existence of a "hhs.rep_location_search" table and Solr index.  See
 * https://fhconfluence.churchofjesuschrist.org/pages/viewpage.action?pageId=188296164 for details.
 *  * 
 * @author wjohnson000
 *
 */
public class LoadLatLongValues {

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAwsDev.connect();
        System.out.println("SESS: " + cqlSession);

        List<String> insertStmts = new ArrayList<>(LatLongConstants.INSERT_REPS);
        for (int i=0;  i<insertStmts.size();  i+=50) {
            int start = i;
            int end   = Math.min(insertStmts.size(), i+49);
            List<String> deleteChunk = insertStmts.subList(start, end);

            System.out.println("... insert from " + start + " to " + end + " --> " + deleteChunk.size());
            executeBatch(cqlSession, deleteChunk);
        }

        cqlSession.close();
        System.exit(0);

    }

    static void executeBatch(CqlSession cqlSession, List<String> statements) {
        if (! statements.isEmpty()) {
            List<BatchableStatement<?>> batchableStatements =
                statements.stream()
                          .map(stmt -> new SimpleStatementBuilder(stmt).build())
                          .collect(Collectors.toList());

            BatchStatement batch =
                  BatchStatement.builder(BatchType.LOGGED)
                                .addStatements(batchableStatements)
                                .build();

            try {
                ResultSet rset = cqlSession.execute(batch);
                System.out.println("   RSET1: " + rset.wasApplied());
            } catch(Exception ex) {
                try {
                    ResultSet rset = cqlSession.execute(batch);
                    System.out.println("   RSET2: " + rset.wasApplied());
                } catch(Exception exx) {
                    System.out.println("Unable to do this silliness -- EXX: " + exx.getMessage());
                    statements.forEach(System.out::println);
                }
            }
        }
    }
}
