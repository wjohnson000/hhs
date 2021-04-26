/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.utility;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

/**
 * @author wjohnson000
 *
 */
public class SessionUtilityAwsDev {

    private static final int      clusterPort    = 9042;
    private static final String[] clusterAddress = { "10.37.120.128", "10.37.121.133", "10.37.122.67" };


    private static Properties cassandraProps = new Properties();
    static {
        try {
            cassandraProps.load(new FileInputStream(new File("C:/Users/wjohnson000/.cassandra-db.props")));
        } catch (Exception e) {
            System.out.println("Unable to load Cassandra properties ... can't proceed ...");
        }
    }

    public static CqlSession connect() {
        String[] credentials = getCredentials();
        if (credentials == null) {
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
                    .withAuthCredentials(credentials[0], credentials[1])
                    .withLocalDatacenter("us-east_core")
                    .withConfigLoader(config)
                    .build();
    }

    static String[] getCredentials() {
        String username = cassandraProps.getProperty("hhs.dev.cassandra.username");
        String password = cassandraProps.getProperty("hhs.dev.cassandra.password");

        if (StringUtils.isBlank(username)) {
            username = JOptionPane.showInputDialog(null, "Username:");
        }
        if (StringUtils.isBlank(password)) {
            password = JOptionPane.showInputDialog(null, "Password:");
        }

        if (StringUtils.isBlank(username)  ||  StringUtils.isBlank(password)) {
            return null;
        } else {
            return new String[] { username, password };
        }
    }
}
