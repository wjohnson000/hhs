package hhs.core.cleanup;

import java.util.*;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;

import hhs.utility.CassandraUtility;
import hhs.utility.SessionUtilityAwsDev;

/**
 * Delete all items that are NOT associated with any collection.  This uses the "item_search" table, and
 * deletes entries in both the "item_search" and "item" tables.
 * 
 * @author wjohnson000
 *
 */
public class DeleteItemsNoCollection {

    final static String  selectCollection = "SELECT * FROM hhs.collectiondata";
    final static String  selectItemAll    = "SELECT * FROM hhs.item_search";
    final static String  deleteItem1      = "DELETE FROM hhs.item WHERE id = '%s' AND type = '%s'";
    final static String  deleteItem2      = "DELETE FROM hhs.item_search WHERE itemId = '%s'";

    static int keepCount = 0;
    final static Set<String> collectionIds = new TreeSet<>();

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAwsDev.connect();
        System.out.println("SESS: " + cqlSession);

        collectionIds.addAll(getAllCollections(cqlSession));
        System.out.println("Collections: " + collectionIds);

        List<String> deleteStmts = getDeleteStmts(cqlSession);
        System.out.println("Keep.Count: " + keepCount);
        System.out.println("Dele.Count: " + deleteStmts.size());
        CassandraUtility.executeBatch(cqlSession, deleteStmts, 50);

        cqlSession.close();
        System.exit(0);
    }

    static Set<String> getAllCollections(CqlSession cqlSession) throws Exception {
        Set<String> ids = new HashSet<>();
        ResultSet rset = cqlSession.execute(selectCollection);
        for (Row row : rset) {
            ids.add(row.getString("id"));
        }

        return ids;
    }

    static List<String> getDeleteStmts(CqlSession cqlSession) throws Exception {
        List<String> deleteStmts = new ArrayList<>();

        ResultSet rset = cqlSession.execute(selectItemAll);
        StreamSupport.stream(PagingIterableSpliterator.builder(rset).withChunkSize(512).build(), true)
                                             .forEach(row -> addDelStatements(deleteStmts, row));

        return deleteStmts;
    }

    static void addDelStatements(List<String> deleteStmts, Row row) {
        String id         = row.getString("itemId");
        String type       = row.getString("type");
        String collId     = row.getString("collectionId");

        if (! collectionIds.contains(collId)) {
            deleteStmts.add(String.format(deleteItem1, id, type));
            deleteStmts.add(String.format(deleteItem2, id));
        } else {
            keepCount++;
//            System.out.println(id + "|" + type + "|" + collId);
        }
    }
}
