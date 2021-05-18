package hhs.client;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.homelands.lib.common.model.RequestHeaderData;
import org.familysearch.homelands.lib.common.util.WebResponse;
import org.familysearch.homelands.lib.common.webAsync.client.HomelandsCoreClientAsync;
import org.familysearch.homelands.lib.common.webAsync.client.WebClientWrapperAsync;
import org.familysearch.paas.binding.register.Environment;
import org.familysearch.paas.binding.register.Region;
import org.familysearch.paas.binding.register.ServiceLocator;
import org.familysearch.paas.binding.register.ServiceLocatorConfig;
import org.familysearch.paas.binding.register.Site;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Do a name search using the "HomelandsCoreClientAsync".  The "ServiceLocatorConfig" will determine which environment will be
 * hit.  The set-up is a bit involved, needing:
 * <ul>
 *   <li>A ServiceLocatorConfig</li>
 *   <li>A ServiceLocator based on the config</li>
 *   <li>A WebClientWrapper</li>
 *   <li>A homelandsCoreClient using the locator, service name and web-client wrapper</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class TestHomelandsCoreAsyncClientNames {

    public static void main(String...args) throws Exception {
        Map<String, String> blah = new HashMap<>();
        String what = blah.computeIfAbsent("what", kk -> "that");
        System.out.println("What: " + what);
        System.out.println(blah);
        if (what != null) {
            System.exit(0);;
        }

        String sessionId = "f0d56df6-d64d-4d07-80ef-2b33e98e4f55-integ";
        Set<String> names = getEsNames();
        System.out.println("Names.count=" + names.size());

        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);
        ServiceLocator locator = new ServiceLocator(config);

        WebClientWrapperAsync clientWrapper = new WebClientWrapperAsync(webClient());
        HomelandsCoreClientAsync hscWebClient = new HomelandsCoreClientAsync(locator, "core.homelands.service", "", clientWrapper);

        RequestHeaderData headerData = new RequestHeaderData("WLJ-private-laptop", "", sessionId);
//        Map<String, String> nameIds = NameHelper.readNames("MMMM-98L", names, "LAST", hscWebClient, "en", headerData);
//        nameIds.entrySet().forEach(System.out::println);

        searchName(hscWebClient, headerData, "Espinoza");
        searchName(hscWebClient, headerData, "Williams");
        searchName(hscWebClient, headerData, "DaCosta");
        searchName(hscWebClient, headerData, "Da Costa");
        searchName(hscWebClient, headerData, "Da%20Costa");
    }

    static void searchName(HomelandsCoreClientAsync hcsWebClient, RequestHeaderData requestData, String name) {
        Mono<WebResponse<Mono<String>>> responseMono = hcsWebClient.searchAll(name, "en", requestData);
        WebResponse<Mono<String>> response = responseMono.block();
        System.out.println("\n=============================================================");
        System.out.println("NAME: " + name);
        System.out.println("  ST: " + response.getStatus());
        System.out.println("  TX: " + response.getBody().block());
    }

    static Set<String> getEsNames() {
        Set<String> names = new TreeSet<>();
        names.add("Espinoza");
        names.add("Williams");
        names.add("Da Costa");
        return names;
    }

    static WebClient webClient() {
        return WebClient.builder().filter((request, next) -> next.exchange(request).doOnNext(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                Map<String, String> respHeaders = clientResponse.headers().asHttpHeaders().entrySet().stream()
                        .collect(Collectors.toMap(ee -> ee.getKey(), ee -> ee.getValue().stream().collect(Collectors.joining(", "))));

                System.out.println("5XX HTTP Status. http_status=" + clientResponse.statusCode() + "; warning_header=" + respHeaders.get(HttpHeaders.WARNING));
                throw new RuntimeException("http call failed with http_status=" + clientResponse.statusCode().value());
            }
        }).retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(1)))).build();
    }
}
