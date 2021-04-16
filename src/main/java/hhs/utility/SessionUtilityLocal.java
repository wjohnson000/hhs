package hhs.utility;

import java.util.Arrays;
import java.util.List;

import org.familysearch.homelands.persistence.model.DbItem;
import org.familysearch.homelands.svc.model.Event;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * Connect to the local Cassandra (DataStax DSE) running in Docker ...
 * 
 * @author wjohnson000
 *
 */
public abstract class SessionUtilityLocal {

    private static final int    clusterPort    = 9042;
    private static final String clusterAddress = "127.0.0.1";

    public static Session connect() {
        Cluster cluster = Cluster.builder()
                    .addContactPoint(clusterAddress)
                    .withPort(clusterPort)
                    .build();

        return cluster.connect("hhs");
    }

    public static void printEvent(String id, DbItem item) {
        printEvent(id, Arrays.asList(item));
    }

    public static void printEvent(String id, List<DbItem> items) {
        System.out.println("\nDbItem for: '" + id + "'");
        if (items == null  ||  items.isEmpty()) {
            System.out.println("NULL ...");
        } else {
            System.out.println("ID: '" + items.get(0).getId() + "' --> " + items.get(0).getType() + " . " + items.get(0).getSubtype());
            System.out.println(" V: " + items.get(0).getVisibility());
            System.out.println(" V: " + items.get(0).getVersionInfo());
            System.out.println(" S: " + items.get(0).getSystemInfo());
            for (DbItem item : items) {
                System.out.println(" C: " + item.getLanguage() + " . " + item.getContent());
            }
        }
    }

    public static void printEvent(String id, Event event) {
        System.out.println("\nEvent for: '" + id + "'");
        if (event == null) {
            System.out.println("NULL ...");
        } else {
            System.out.println("ID: '" + event.getId() + "' --> " + event.getSubtype());

            if (event.getRevisionInfo() != null) {
                System.out.println("  ri.vers: " + event.getRevisionInfo().getVersion());
                System.out.println("  ri.cusr: " + event.getRevisionInfo().getCreateUser());
                System.out.println("  ri.cdat: " + event.getRevisionInfo().getCreateDate());
            }

            System.out.println("  ev.visi: " + event.getVisibility());
            System.out.println("  ev.syst: " + event.getSystemInfo());
            System.out.println("  ev.vers: " + event.getVersionInfo());
            System.out.println("  ev.coll: " + event.getCollectionInfo());

            for (String lang : event.getLanguages()) {
                System.out.println("  ev." + lang + ":   " + event.getContentByLanguage(lang));
            }
        }
    }

    public static String makeJson(String... values) {
        StringBuilder buff = new StringBuilder();

        buff.append("{ ");
        for (int i=0;  i<values.length;  i+=2) {
            if (buff.length() > 3) {
                buff.append(", ");
            }

            buff.append('"').append(values[i]).append('"').append(": ");
            try {
                int iValue = Integer.parseInt(values[i+1]);
                buff.append(iValue);
            } catch(NumberFormatException ex) {
                buff.append('"').append(values[i+1]).append('"');
            }
        }
        buff.append(" }");

        return buff.toString();
    }
}
