package hhs.client;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.familysearch.homelands.lib.common.util.WebResponse;
import org.familysearch.homelands.lib.common.web.client.PlaceServiceClient;
import org.familysearch.homelands.lib.common.web.client.PlaceServiceClientImpl;
import org.familysearch.homelands.lib.common.web.client.WebClientWrapper;
import org.familysearch.homelands.lib.common.web.util.PlaceClientHelper;
import org.familysearch.paas.binding.register.Environment;
import org.familysearch.paas.binding.register.Region;
import org.familysearch.paas.binding.register.ServiceLocator;
import org.familysearch.paas.binding.register.ServiceLocatorConfig;
import org.familysearch.paas.binding.register.Site;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.util.retry.Retry;

/**
 * Test the methods of the {@link PlaceServiceClient}.  Un-comment out the desired "ServiceLocatorConfig"
 * line to set the environment.
 *  
 * @author wjohnson000
 *
 */
public class TestPlaceServiceClient {

    public static void main(String...args) {
//        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);
        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.TEST, Site.BETA, Region.US_EAST_1);
//        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.DEV, Site.INTEG, Region.US_EAST_1);
//        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.DEV, Site.DEV, Region.US_EAST_1);

        ServiceLocator locator = new ServiceLocator(config);
        WebClientWrapper wcWrapper = new WebClientWrapper(webClient());
        PlaceServiceClient psClient = new PlaceServiceClientImpl(locator, "ws.place.standards.service", null, wcWrapper);

        testFindPlaceRep(psClient, 221);
        testFindPlaceRep(psClient, 324);

        testFindReplacementPlaceRepId(psClient, 221);
        testFindReplacementPlaceRepId(psClient, 510796);
        testFindReplacementPlaceRepId(psClient, 111111111);

        testExtractDeletedPlaceRepIds(psClient, 111);
        testExtractDeletedPlaceRepIds(psClient, 765);
//        testFindDeletedPlaceRepIds(psClient, "3472");  // > 100 reps
        testExtractDeletedPlaceRepIds(psClient, 11002283);

        testExtractJurisdiction(psClient, 111);
        testExtractJurisdiction(psClient, 765);
        testExtractJurisdiction(psClient, 3472);
        testExtractJurisdiction(psClient, 11002283);
    }

    static void testFindPlaceRep(PlaceServiceClient client, int repId) {
        System.out.println("\n>>> FindPlaceRep >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        WebResponse<String> webResp = client.findPlaceRep(repId, false);
        System.out.println("  CODE: " + webResp.getStatus());
        System.out.println("  body: " + webResp.getBody());
    }

    static void testFindReplacementPlaceRepId(PlaceServiceClient client, int repId) {
        System.out.println("\n>>> FindReplacementPlaceRepId >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        WebResponse<String> webResp = client.findPlaceRep(repId, false);
        Integer newRepId = PlaceClientHelper.extractPlaceRepId(webResp.getBody());
        System.out.println("  RepId: " + repId);
        System.out.println("  NewId: " + newRepId);
    }

    static void testExtractDeletedPlaceRepIds(PlaceServiceClient client, int repId) {
        System.out.println("\n>>> ExtractDeletedPlaceRepIds >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        WebResponse<String> webResp = client.findPlaceRep(repId, true);
        Map<Integer, LocalDate> delIds = PlaceClientHelper.extractDeletedPlaceRepIds(webResp.getBody());
        System.out.println("  RepId: " + repId);
        System.out.println("  Count: " + delIds.size());
        delIds.entrySet().forEach(ee -> System.out.println("  Entry: " + ee));
    }

    static void testExtractJurisdiction(PlaceServiceClient client, int repId) {
        System.out.println("\n>>> ExtractJurisdictionChain >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        WebResponse<String> webResp = client.findPlaceRep(repId, false);
        List<Integer> chain = PlaceClientHelper.extractJurisdictionChain(webResp.getBody());
        System.out.println("  RepId: " + repId);
        System.out.println("  Juris: " + chain);
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
