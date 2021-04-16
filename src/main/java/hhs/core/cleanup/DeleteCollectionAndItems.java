package hhs.core.cleanup;

import java.util.*;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;
import com.fasterxml.jackson.databind.JsonNode;

import hhs.utility.CassandraUtility;
import hhs.utility.SessionUtilityAWS;
import org.familysearch.homelands.lib.common.util.JsonUtility;

/**
 * Find all test collections -- those with "a very neat-o test" in their description -- and delete both the
 * collection and all items associated with it.  Items are deleted from both the "item" and "item_search" table.
 *
 * <p>NOTE: if a collection has been deleted, but there are dangling items, use one of these utilities:
 * <ul>
 *   <li>DeleteItemsNoollection -- if you don't know the collection ID</li>
 *   <li>DeleteItemsFromCollection -- if you know the collection ID</li>
 *   <li>DeleteItemsFromCollectionII -- if you know the collection ID</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class DeleteCollectionAndItems {

    final static String  selectCollections = "SELECT * FROM hhs.collectiondata";
    final static String  selectItemAll     = "SELECT * FROM hhs.item_search";
    final static String  deleteItem1       = "DELETE FROM hhs.item WHERE id = '%s' AND type = '%s'";
    final static String  deleteItem2       = "DELETE FROM hhs.item_search WHERE itemId = '%s'";
    final static String  deleteCollections = "DELETE FROM hhs.collectiondata WHERE id = '%s'";

    final static Set<String> keepCollectionIds   = new TreeSet<>();
    final static Set<String> deleteCollectionIds = new TreeSet<>();

    static int count = 0;

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        // Read in the list of collections
        getAllCollectionIds(cqlSession);
        System.out.println("Keep count: " + keepCollectionIds.size() + ";  delete count: " + deleteCollectionIds.size());

        List<String> deleteStmts = getDeleteStmts(cqlSession);
        deleteCollectionIds.forEach(cid -> deleteStmts.add(String.format(deleteCollections, cid)));

        System.out.println();
        System.out.println();
        System.out.println("ItemCount: " + count);
        System.out.println("StmtCount: " + deleteStmts.size());
        deleteStmts.forEach(System.out::println);

        for (int i=0;  i<deleteStmts.size();  i+=50) {
            int start = i;
            int end   = Math.min(deleteStmts.size(), i+49);
            List<String> deleteChunk = deleteStmts.subList(start, end);

            System.out.println("... delete from " + start + " to " + end + " --> " + deleteChunk.size());
            CassandraUtility.executeBatch(cqlSession, deleteChunk);
        }

        cqlSession.close();
        System.exit(0);
    }

    static void getAllCollectionIds(CqlSession cqlSession) throws Exception {
        ResultSet rset = cqlSession.execute("SELECT * FROM hhs.collectiondata");
        for (Row row : rset) {
            String details = row.getString("details");
            JsonNode node = JsonUtility.parseJson(details);
            String description = JsonUtility.getStringValue(node, "description");

            if (description.contains("a very neat-o test")) {
                deleteCollectionIds.add(row.getString("id"));
            } else {
                keepCollectionIds.add(row.getString("id"));
            }
        }
    }

    static List<String> getDeleteStmts(CqlSession cqlSession) throws Exception {
        List<String> deleteStmts = new ArrayList<>();

        ResultSet rset = cqlSession.execute(selectItemAll);
        StreamSupport.stream(PagingIterableSpliterator.builder(rset).withChunkSize(1024).build(), true)
                                             .forEach(row -> addDelStatements(deleteStmts, row));

        return deleteStmts;
    }

    static void addDelStatements(List<String> deleteStmts, Row row) {
        count++;
        String id         = row.getString("itemId");
        String type       = row.getString("type");
        String collId     = row.getString("collectionId");

        if (! "QUIZ".equals(type)  &&  (! keepCollectionIds.contains(collId))) {
            deleteStmts.add(String.format(deleteItem1, id, type));
            deleteStmts.add(String.format(deleteItem2, id));
        } else {
            System.out.println(id + "|" + type + "|" + collId);
        }
    }
}
