package hhs.client;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import org.familysearch.homelands.core.persistence.CassandraOps;
import org.familysearch.homelands.core.persistence.ItemSearchFilter;
import org.familysearch.homelands.core.persistence.dao.*;
import org.familysearch.homelands.core.persistence.model.PagedResults;
import org.familysearch.homelands.core.persistence.util.IdGenerator;
import org.familysearch.homelands.core.persistence.util.IdGeneratorImpl;
import org.familysearch.homelands.core.svc.FileService;
import org.familysearch.homelands.core.svc.InMemoryFileServiceImpl;
import org.familysearch.homelands.core.svc.ItemService;
import org.familysearch.homelands.core.svc.model.Item;
import org.familysearch.homelands.lib.common.model.ItemType;
import org.familysearch.homelands.lib.common.model.VisibilityType;
import org.familysearch.homelands.lib.common.web.client.PlaceServiceClient;
import org.familysearch.homelands.lib.common.web.client.PlaceServiceClientImpl;
import org.familysearch.homelands.lib.common.web.client.WebClientWrapper;
import org.familysearch.paas.binding.register.Environment;
import org.familysearch.paas.binding.register.Region;
import org.familysearch.paas.binding.register.ServiceLocator;
import org.familysearch.paas.binding.register.ServiceLocatorConfig;
import org.familysearch.paas.binding.register.Site;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import com.datastax.oss.driver.api.core.CqlSession;

import hhs.utility.SessionUtilityAWS;
import reactor.util.retry.Retry;

/**
 * Test the "search" feature of the item service.  The service locator is hard-coded to talk to "DEV".
 * 
 * @author wjohnson000
 *
 */
public class TestItemServiceSearch {

    public static void main(String...args) throws Exception {
        CqlSession   cqlSession = SessionUtilityAWS.connect();
        CassandraOps cassandraOps = new CassandraOps(cqlSession);
        System.out.println("OPS: " + cassandraOps);

        IdGenerator idGenerator = new IdGeneratorImpl(cassandraOps);
        ItemDao itemDao = new ItemDaoImpl(cassandraOps, idGenerator);
        ItemSearchDao itemSearchDao = new ItemSearchDaoImpl(cassandraOps);
        CollectionDao collectionDao = new CollectionDaoImpl(cassandraOps, idGenerator);
        FileService fileService = new InMemoryFileServiceImpl();

        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.DEV, Site.DEV, Region.US_EAST_1);
        ServiceLocator locator = new ServiceLocator(config);
        WebClientWrapper wcWrapper = new WebClientWrapper(webClient());
        PlaceServiceClient psClient = new PlaceServiceClientImpl(locator, "ws.place.standards.service", null, wcWrapper);

        ItemService itemService = new ItemService(itemDao, itemSearchDao, collectionDao, fileService, psClient);

        ItemSearchFilter isf = new ItemSearchFilter();
        isf.setStart(0);
        isf.setCount(25);
        isf.setStartYear(1920);
        isf.setEndYear(1921);

        PagedResults<Item> results = itemService.search(isf, ItemType.EVENT, VisibilityType.PUBLIC);
        System.out.println("RES: " + results.getResults().size());
        System.out.println("TOT: " + results.getTotal());
        results.getResults().forEach(res -> System.out.println("ID: " + res.getItemData().getId() + " .. " + res.getItemData().getType() + " .. " + res.getItemData().getTitle()));

        isf.setStart(25);
        results = itemService.search(isf, ItemType.EVENT, VisibilityType.PUBLIC);
        System.out.println("RES: " + results.getResults().size());
        System.out.println("TOT: " + results.getTotal());
        results.getResults().forEach(res -> System.out.println("ID: " + res.getItemData().getId() + " .. " + res.getItemData().getType() + " .. " + res.getItemData().getTitle()));

        isf.setStart(50);
        results = itemService.search(isf, ItemType.EVENT, VisibilityType.PUBLIC);
        System.out.println("RES: " + results.getResults().size());
        System.out.println("TOT: " + results.getTotal());
        results.getResults().forEach(res -> System.out.println("ID: " + res.getItemData().getId() + " .. " + res.getItemData().getType() + " .. " + res.getItemData().getTitle()));

        isf.setStart(75);
        results = itemService.search(isf, ItemType.EVENT, VisibilityType.PUBLIC);
        System.out.println("RES: " + results.getResults().size());
        System.out.println("TOT: " + results.getTotal());
        results.getResults().forEach(res -> System.out.println("ID: " + res.getItemData().getId() + " .. " + res.getItemData().getType() + " .. " + res.getItemData().getTitle()));

        System.exit(0);
    }

    static WebClient webClient() {
        return WebClient.builder().filter((request, next) -> next.exchange(request).doOnNext(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                Map<String, String> respHeaders = clientResponse.headers().asHttpHeaders().entrySet().stream()
                        .collect(Collectors.toMap(ee -> ee.getKey(), ee -> ee.getValue().stream().collect(Collectors.joining(", "))));

                System.out.println("5XX HTTP Status. http_status=" + clientResponse.statusCode() + " warning_header={}" + respHeaders.get(HttpHeaders.WARNING));
                throw new RuntimeException("http call failed with http_status=" + clientResponse.statusCode().value());
            }
        }).retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)))).build();
    }
}
