package hhs.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BatchableStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;

/**
 * Common Cassandra utility methods.
 * 
 * @author wjohnson000
 *
 */
public class CassandraUtility {

    /**
     * Execute a bunch of statements in batches, given a target batch size.
     * 
     * @param cqlSession CqlSession
     * @param statements INSERT, UPDATE and/or DELETE statements
     * @param batchSize target batch size
     */
    public static void executeBatch(CqlSession cqlSession, List<String> statements, int batchSize) {
        for (int i=0;  i<statements.size();  i+=batchSize) {
            int start = i;
            int end   = Math.min(statements.size(), i+49);
            List<String> deleteChunk = statements.subList(start, end);
            
            System.out.println("... execute from " + start + " to " + end + " --> " + deleteChunk.size());
            CassandraUtility.executeBatch(cqlSession, deleteChunk);
        }
    }

    /**
     * Execute a bunch of statements in batch.  NOTE: they can be INSERT, UPDATE or DELETE statements, but there
     * is no support for batching SELECT statements!.
     * 
     * @param statements
     */
    public static void executeBatch(CqlSession cqlSession, List<String> statements) {
        if (! statements.isEmpty()) {
            List<BatchableStatement<?>> batchableStatements =
                statements.stream()
                          .map(stmt -> new SimpleStatementBuilder(stmt).build())
                          .collect(Collectors.toList());

            BatchStatement batch =
                  BatchStatement.builder(BatchType.LOGGED)
                                .addStatements(batchableStatements)
                                .build();

            try {
                ResultSet rset = cqlSession.execute(batch);
                System.out.println("   RSET1: " + rset.wasApplied());
            } catch(Exception ex) {
                try {
                    ResultSet rset = cqlSession.execute(batch);
                    System.out.println("   RSET2: " + rset.wasApplied());
                } catch(Exception exx) {
                    System.out.println("Unable to do this silliness -- EXX: " + exx.getMessage());
                    statements.forEach(System.out::println);
                }
            }
        }
    }

}
