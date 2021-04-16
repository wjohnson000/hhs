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
 * Delete all quizzes that were created during acceptance tests, but not correctly cleaned up.  The test quizzes can
 * be identified by one of the tags listed below.  NOTE: it would be preferable if all "test" quizzes has a tag that
 * contained the word "test" in it.  Shame on "upda-ted" and "usopenmenschampion"!.
 * 
 * @author wjohnson000
 *
 */
public class DeleteTestQuizzesAndAnswers {

    static final String selectQuizI = "SELECT * FROM hhs.item WHERE id='%s' AND type='QUIZ' ";
    static final String selectQuizS = "SELECT * FROM hhs.item_search WHERE solr_query = 'tags:quiz' LIMIT 2600 ";

    static final String deleteQuizI = "DELETE FROM hhs.item WHERE id='%s' AND type='%s'";
    static final String deleteQuizS = "DELETE FROM hhs.item_search WHERE itemid='%s'";

    static final Set<String> testTags = new TreeSet<>();
    static {
        testTags.add("testchurchhistory");
        testTags.add("testnfl");
        testTags.add("testworldpopulation");
        testTags.add("teststanleycup");
        testTags.add("teststanleycupa");
        testTags.add("testcanadagolf");
        testTags.add("testcanadafootball");
        testTags.add("testworldseries");
        testTags.add("testnba");
        testTags.add("testncaabaseball");
        testTags.add("testncaabasketball");
        testTags.add("testncaafootball");
        testTags.add("testuspopulation");
        testTags.add("testushistory");
        testTags.add("testtemple");
        testTags.add("testworldcup");
        testTags.add("testworldcupa");
        testTags.add("upda-ted");
        testTags.add("usopenmenschampion");
    }

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        Set<String> quizIds = getAllQuizzes(cqlSession);
        System.out.println("IDS: " + quizIds.size());

        List<String> deleteStmts = new ArrayList<>();
        for (String quizId : quizIds) {
            deleteStmts.add(String.format(deleteQuizI, quizId, "QUIZ"));
            deleteStmts.add(String.format(deleteQuizS, quizId));
            if (deleteStmts.size() > 50) {
                CassandraUtility.executeBatch(cqlSession, deleteStmts);
                deleteStmts.clear();
            }
        }
        CassandraUtility.executeBatch(cqlSession, deleteStmts);

        System.exit(0);
        
    }

    static Set<String> getAllQuizzes(CqlSession cqlSession) throws Exception {
        Set<String> quizIds = new TreeSet<>();

        ResultSet rset = cqlSession.execute(selectQuizS);
        StreamSupport.stream(PagingIterableSpliterator.builder(rset).withChunkSize(512).build(), true)
                .forEach(row -> addQuizIds(quizIds, row));

        return quizIds;
    }


    static void addQuizIds(Set<String> quizIds, Row row) {
        String       id   = row.getString("itemid");
        List<String> tags = row.getList("tags", String.class);
        if (tags.stream().anyMatch(tag -> testTags.contains(tag))) {
            List<String> langs = row.getList("languages", String.class);
            quizIds.add(id);
            System.out.println("DELX: " + id + " --> " + tags + " --> " + langs);
        } else {
            System.out.println("KEEP: " + id + " --> " + tags);
        }
    }
}
