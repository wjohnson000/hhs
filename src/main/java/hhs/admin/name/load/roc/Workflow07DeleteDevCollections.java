package hhs.admin.name.load.roc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;

import hhs.utility.CassandraUtility;
import hhs.utility.SessionUtilityAwsDev;

/**
 * Delete all names in a collection, and the collection itself, directly against Cassandra.
 * 
 * @author wjohnson000
 *
 */
public class Workflow07DeleteDevCollections {

    final static Set<String> delCollectionIds = new TreeSet<>();
    static {
        delCollectionIds.add("MMM7-3YL");
        delCollectionIds.add("MMMQ-171");
        delCollectionIds.add("MMM7-3Y2");
        delCollectionIds.add("MMMS-7HK");
        delCollectionIds.add("MMM9-X7D");
    }

    final static String selectNameAll    = "SELECT id, collectionid FROM hhs.name WHERE collectionId = '%s'";
    final static String deleteCollection = "DELETE FROM hhs.collectiondata WHERE id = '%s'";
    final static String deleteName       = "DELETE FROM hhs.name WHERE id = '%s'";
    final static String deleteNameSearch = "DELETE FROM hhs.name_search WHERE nameid = '%s'";

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAwsDev.connect();
        System.out.println("SESS: " + cqlSession);

        List<String> deleteStmts = new ArrayList<>();
        for (String collId : delCollectionIds) {
            deleteStmts.addAll(getDeleteStmts(collId, cqlSession));
            deleteStmts.add(String.format(deleteCollection, collId));
        }
        System.out.println("StmtCount: " + deleteStmts.size());
        CassandraUtility.executeBatch(cqlSession, deleteStmts, 50);

        cqlSession.close();
        System.exit(0);
    }

    static List<String> getDeleteStmts(String collectionId, CqlSession cqlSession) throws Exception {
        List<String> deleteStmts = new ArrayList<>();

        ResultSet rset = cqlSession.execute(String.format(selectNameAll, collectionId));
        StreamSupport.stream(PagingIterableSpliterator.builder(rset).withChunkSize(1024).build(), true)
                                             .forEach(row -> addDelStatements(deleteStmts, row, collectionId));

        return deleteStmts;
    }

    static void addDelStatements(List<String> deleteStmts, Row row, String collectionId) {
        String id     = row.getString("id");
        String collId = row.getString("collectionid");  // This is simply a sanity check!

        if (collectionId.equals(collId)) {
            deleteStmts.add(String.format(deleteName, id));
            deleteStmts.add(String.format(deleteNameSearch, id));
        }
    }
}
