package hhs.core.cleanup;

import java.util.*;
import java.util.stream.StreamSupport;

import org.familysearch.homelands.lib.common.util.JsonUtility;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;
import com.fasterxml.jackson.databind.JsonNode;

import hhs.utility.CassandraUtility;
import hhs.utility.SessionUtilityAWS;

/**
 * Delete all items that are NOT associated with any collection.  This uses the "item" table only, otherwise it is
 * similar to {@link DeleteItemsNoCollection}.
 * 
 * @author wjohnson000
 *
 */
public class DeleteItemsNoCollectionII {

    final static String  selectCollection = "SELECT * FROM hhs.collectiondata";
    final static String  selectItemAll    = "SELECT * FROM hhs.item";
    final static String  deleteItem1      = "DELETE FROM hhs.item WHERE id = '%s' AND type = '%s'";

    final static Set<String> collectionIds = new TreeSet<>();

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        collectionIds.addAll(getAllCollections(cqlSession));
        collectionIds.remove("MMM3-G4V");
        System.out.println("Collections: " + collectionIds);

        List<String> deleteStmts = getDeleteStmts(cqlSession);
        System.out.println("StmtCount: " + deleteStmts.size());

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
        String id     = row.getString("id");
        String type   = row.getString("type");
        String collId = null;

        try {
            String details    = row.getString("details");
            JsonNode json     = JsonUtility.parseJson(details);
            if (json != null) {
                collId = JsonUtility.getStringValue(json, "collectionId");
            }
        } catch(Exception ex) {
            System.out.println(id + "|" + type + "|" + ex.getMessage());
        }

        if (collId != null  &&  ! collectionIds.contains(collId)) {
            deleteStmts.add(String.format(deleteItem1, id, type));
        } else {
            System.out.println(id + "|" + type + "|" + collId);
        }
    }
}
