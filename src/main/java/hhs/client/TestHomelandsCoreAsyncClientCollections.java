package hhs.client;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.homelands.lib.common.model.RequestHeaderData;
import org.familysearch.homelands.lib.common.util.JsonUtility;
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

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * Do a collection search using the "HomelandsCoreClientAsync".  The "ServiceLocatorConfig" will determine which environment will be
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
public class TestHomelandsCoreAsyncClientCollections {

    public static void main(String...args) throws Exception {
        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);
        ServiceLocator locator = new ServiceLocator(config);

        WebClientWrapperAsync clientWrapper = new WebClientWrapperAsync(webClient());
        HomelandsCoreClientAsync hscWebClient = new HomelandsCoreClientAsync(locator, "core.homelands.service", "", clientWrapper);

        RequestHeaderData headerData = new RequestHeaderData("WLJ-private-laptop", "", "");
        Mono<WebResponse<Mono<String>>> responseMono = hscWebClient.getAllCollections(headerData);
        WebResponse<Mono<String>> response = responseMono.block();
        System.out.println("Status: " + response.getStatus());
        JsonNode collAllNode = JsonUtility.parseJson(response.getBody().block());
        List<JsonNode> collNodes = JsonUtility.getArrayValueAsNodes(collAllNode, "collections");
        System.out.println("Count:" + collNodes.size());
        collNodes.forEach(System.out::println);

        System.exit(0);
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
