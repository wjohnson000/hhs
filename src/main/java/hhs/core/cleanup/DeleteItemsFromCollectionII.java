package hhs.core.cleanup;

import java.util.*;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;

import hhs.utility.CassandraUtility;
import hhs.utility.SessionUtilityAWS;

/**
 * Delete items from one or more collections.
 * 
 * <p>NOTE: the collection itself may not exist, so the items are floating free in space.
 * 
 * <p>NOTE: this will not delete "QUIZ" items!
 * 
 * <p>NOTE: this is similar to {@link DeleteItemsFromColletion}, which pulls item IDS from the "item" table.
 *
 * <p>NOTE: if you want to delete any item that is not associated with an existing collection, run
 * <ul>
 *   <li>DeleteItemsNoollection -- if you don't know the collection ID</li>
 *   <li>DeleteItemsNoollectionII -- if you don't know the collection ID</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class DeleteItemsFromCollectionII {

    final static String selectItemAll = "SELECT * FROM hhs.item_search";
    final static String deleteItem1   = "DELETE FROM hhs.item WHERE id = '%s' AND type = '%s'";
    final static String deleteItem2   = "DELETE FROM hhs.item_search WHERE itemId = '%s'";

    final static Set<String> collectionIds = new HashSet<>();
    static {
        collectionIds.add("MMMS-8LV");
    }

    final static Set<String> ignoreIds     = new TreeSet<>();
    static int count = 0;

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        List<String> deleteStmts = getDeleteStmts(cqlSession);
        System.out.println("ItemCount: " + count);
        System.out.println("StmtCount: " + deleteStmts.size());
        CassandraUtility.executeBatch(cqlSession, deleteStmts, 50);

        cqlSession.close();
        System.exit(0);
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
        List<String> tags = row.getList("tags", String.class);

        if (! "QUIZ".equals(type)  &&  collectionIds.contains(collId)) {
            deleteStmts.add(String.format(deleteItem1, id, type));
            deleteStmts.add(String.format(deleteItem2, id));
        } else {
            System.out.println(id + "|" + type + "|" + collId + "|" + tags);
        }
    }
}
