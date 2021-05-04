package hhs.core.collection;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.familysearch.homelands.lib.common.util.JsonUtility;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;
import com.fasterxml.jackson.databind.JsonNode;

import hhs.utility.SessionUtilityAwsDev;

/**
 * List the details of all names in a collection
 * 
 * @author wjohnson000
 *
 */
public class ListNameDetails {

    final static String      collection    = "MMM7-3VB";
    final static String      selectNameAll = "SELECT * FROM hhs.name";

    final static Set<String> ignoreIds   = new TreeSet<>();
    static int count = 0;

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAwsDev.connect();
        System.out.println("SESS: " + cqlSession);

        listNameDetails(cqlSession);
        System.out.println("\nCOUNT: " + count);
        cqlSession.close();
        System.exit(0);
    }

    static void listNameDetails(CqlSession cqlSession) throws Exception {
        ResultSet rset = cqlSession.execute(selectNameAll);
        StreamSupport.stream(PagingIterableSpliterator.builder(rset).withChunkSize(1024).build(), true)
                                             .forEach(row -> showNameDetails(row));

    }

    static void showNameDetails(Row row) {
        count++;
        String collId  = row.getString("collectionid");
        if (collection.equals(collId)) {
            String id      = row.getString("id");
            String name    = row.getString("name");
            String type    = row.getString("nametype");
            String details = row.getString("details");
            
            List<String> variants = new ArrayList<>();
            try {
                JsonNode detailNode = JsonUtility.parseJson(details);
                JsonNode variantMapNode = JsonUtility.getJsonNode(detailNode, "nameVariants");
                Iterator<Map.Entry<String, JsonNode>> nameVariantIter = variantMapNode.fields();
                while(nameVariantIter.hasNext()) {
                    Map.Entry<String, JsonNode> entry = nameVariantIter.next();
                    JsonNode nameVariantsNode = entry.getValue();
                    if (nameVariantsNode != null) {
                        nameVariantsNode.forEach(nameVariantNode -> {
                            String vName = JsonUtility.getStringValue(nameVariantNode, "name");
                            String vId   = JsonUtility.getStringValue(nameVariantNode, "nameId");
                            variants.add(vName + "[" + vId + "]");
                        });
                    }
                }
            } catch(Exception ex) { }
            
            System.out.println(id + " | " + name + " | " + type + " | " + variants.stream().collect(Collectors.joining("|")));
        }
    }
}
