/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.client;

import hhs.client.helper.DummyClientFactory;
import hhs.client.helper.DummyDaoFactory;

import java.util.Arrays;

import org.familysearch.homelands.admin.client.ClientFactory;
import org.familysearch.homelands.admin.importer.step.item.RawMultiLangTimelineToCanonicalStep;
import org.familysearch.homelands.admin.parser.timeline.TimelineParser;
import org.familysearch.homelands.admin.persistence.dao.DaoFactory;

/**
 * @author wjohnson000
 *
 */
public class TestRawMultiLangTimelineToCanonicalStep {

    public static void main(String... args) {
        DaoFactory daoFactory = DummyDaoFactory.get();
        ClientFactory clientFactory = DummyClientFactory.get("C:/temp/hhs-files");

        RawMultiLangTimelineToCanonicalStep step = new RawMultiLangTimelineToCanonicalStep(0, "wjohnson000", daoFactory, clientFactory, null);
        step.executePreTasks();
        step.processFiles(Arrays.asList("Discovery Costa Rica English.xlsx", "Discovery Costa Rica Spanish.xlsx"), new TimelineParser());

        System.exit(0);
    }
}
