package hhs.core.collection;

import org.familysearch.homelands.lib.common.util.JsonUtility;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.fasterxml.jackson.databind.JsonNode;

import hhs.utility.SessionUtilityAwsDev;

/**
 * List details about all collections in DEV.  This does a Cassandra query.
 * 
 * @author wjohnson000
 *
 */
public class ListCollections {

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAwsDev.connect();
        System.out.println("SESS: " + cqlSession);

        ResultSet rset = cqlSession.execute("SELECT * FROM hhs.collectiondata LIMIT 500");
        for (Row row : rset) {
            String details = row.getString("details");
            JsonNode node = JsonUtility.parseJson(details);
            String description = JsonUtility.getStringValue(node, "description");
            JsonNode attributionNode = JsonUtility.getJsonNode(node, "attribution");
            String attribution = (attributionNode == null) ? "???" : JsonUtility.getStringValue(attributionNode, "en");

            StringBuilder buff = new StringBuilder();
            buff.append(row.getString("id"));
            buff.append("|").append(row.getString("name"));
            buff.append("|").append(row.getInstant("createdate"));
            buff.append("|").append(row.getSet("type", String.class));
            buff.append("|").append(description);
            buff.append("|").append(attribution);
            System.out.println(buff.toString());
        }

        cqlSession.close();
        System.exit(0);
    }
}
