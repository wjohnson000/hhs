package hhs.core.cleanup;

import java.util.ArrayList;
import java.util.List;
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
public class DeleteNameCollection {

    final static String  delCollectionId  = "MMM7-3VB";
    final static String  selectNameAll    = "SELECT id, collectionid FROM hhs.name WHERE collectionId = '%s'";
    final static String  deleteCollection = "DELETE FROM hhs.collectiondata WHERE id = '%s'";
    final static String  deleteName       = "DELETE FROM hhs.name WHERE id = '%s'";
    final static String  deleteNameSearch = "DELETE FROM hhs.name_search WHERE nameid = '%s'";

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAwsDev.connect();
        System.out.println("SESS: " + cqlSession);

        List<String> deleteStmts = getDeleteStmts(cqlSession);
        deleteStmts.add(String.format(deleteCollection, delCollectionId));
        System.out.println("StmtCount: " + deleteStmts.size());

        CassandraUtility.executeBatch(cqlSession, deleteStmts, 50);

        cqlSession.close();
        System.exit(0);
    }

    static List<String> getDeleteStmts(CqlSession cqlSession) throws Exception {
        List<String> deleteStmts = new ArrayList<>();

        ResultSet rset = cqlSession.execute(String.format(selectNameAll, delCollectionId));
        StreamSupport.stream(PagingIterableSpliterator.builder(rset).withChunkSize(1024).build(), true)
                                             .forEach(row -> addDelStatements(deleteStmts, row));

        return deleteStmts;
    }

    static void addDelStatements(List<String> deleteStmts, Row row) {
        String id     = row.getString("id");
        String collId = row.getString("collectionid");

        if (delCollectionId.equals(collId)) {
            deleteStmts.add(String.format(deleteName, id));
            deleteStmts.add(String.format(deleteNameSearch, id));
        }
    }
}
