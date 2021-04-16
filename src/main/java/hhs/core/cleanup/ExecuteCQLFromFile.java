package hhs.core.cleanup;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.datastax.oss.driver.api.core.CqlSession;
import hhs.utility.CassandraUtility;
import hhs.utility.SessionUtilityAWS;

/**
 * Execute CQL statements (such as a bunch of "DELETE" commands) from a file.  The statements are batched in groups
 * of 50 for performance reasons.
 * 
 * @author wjohnson000
 *
 */
public class ExecuteCQLFromFile {

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-G4B.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-P2V.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-PLQ.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-G4V.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-PGS.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-RMZ.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-P26.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-PK5.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-P2N.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-PL7.cql");

        cqlSession.close();
        System.exit(0);
    }

    static void executeFromFile(CqlSession cqlSession, String filepath) throws Exception {
        // Read in the list of delete statements
        List<String> deleteStmts = Files.readAllLines(Paths.get(filepath), StandardCharsets.UTF_8);
        System.out.println("StmtCount: " + deleteStmts.size());

        CassandraUtility.executeBatch(cqlSession, deleteStmts, 50);
    }
}
