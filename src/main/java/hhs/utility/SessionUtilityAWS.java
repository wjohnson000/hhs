package hhs.utility;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

/**
 * Connect to the DEV Cassandra (DataStax DSE) running in Docker.  NOTE: this utility class prompts the user
 * for the database password.  Thus no password is visible in the GIT project!
 * 
 * @author wjohnson000
 *
 */
public final class SessionUtilityAWS {

    private static final int      clusterPort    = 9042;
    private static final String[] clusterAddress = { "10.37.120.128", "10.37.121.133", "10.37.122.67" };

    public static CqlSession connect() {
        String password = JOptionPane.showInputDialog(null, "Password:");
        if (password == null) {
            return null;
        }

        List<InetSocketAddress> contactPoints =
                Arrays.stream(clusterAddress)
                      .map(ip -> new InetSocketAddress(ip, clusterPort))
                      .collect(Collectors.toList());

        DriverConfigLoader config =
                DriverConfigLoader.programmaticBuilder()
                    .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(5))
                    .startProfile("slow")
                    .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(30))
                    .endProfile()
                    .build();
 
        return CqlSession.builder()
                    .withKeyspace(CqlIdentifier.fromCql("hhs"))
                    .addContactPoints(contactPoints)
                    .withAuthCredentials("cassandra", password)
                    .withLocalDatacenter("us-east_core")
                    .withConfigLoader(config)
                    .build();
    }
}
