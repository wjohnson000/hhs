/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin.ui.client;

import java.time.Duration;

import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.util.retry.Retry;

/**
 * @author wjohnson000
 *
 */
public final class WebClientHelper {

    private static WebClient onlyClient = makeClient();

    public static WebClient getClient() {
        return onlyClient;
    }

    private static WebClient makeClient() {
        return WebClient.builder()
                        .filter((request, next) -> next.exchange(request).doOnNext(clientResponse -> {
                            if (clientResponse.statusCode().is5xxServerError()) {
                                System.out.println("Unable to complete the request ...");
                            }
                        }).retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(3))))
                        .exchangeStrategies(ExchangeStrategies.builder()
                                                .codecs(configurer -> configurer
                                                          .defaultCodecs()
                                                          .maxInMemorySize(16 * 1024 * 1024))
                                                        .build())
                        .build();
    }

    private WebClientHelper() { }
}
