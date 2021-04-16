/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin.ui.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.familysearch.homelands.lib.common.util.JsonUtility;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import std.wlj.hhs.admin.ui.model.FolderNode;
import std.wlj.hhs.admin.ui.model.FolderType;

/**
 * @author wjohnson000
 *
 */
public final class AdminClient {

    private static final String DEV_BASE_URL  = "http://admin.homelands.service.dev.us-east-1.dev.fslocal.org";
    private static final String PROD_BASE_URL = "prod --> http://admin.homelands.service.prod.us-east-1.prod.fslocal.org";

    private AdminClient() { }

    public static byte[] readFile(FolderNode file, String sessionId, boolean isProd) {
        String[] chunks = getFileParts(file);
        if (chunks == null) {
            return null;
        }

        try {
            List<String> stepIds = getStepId(file, sessionId, isProd);
            for (String stepId : stepIds) {
                String url = makeUrl(isProd ? PROD_BASE_URL : DEV_BASE_URL, "collection", chunks[1], "import", chunks[2], "step", stepId, "file", file.getId());
                ClientResponse response = getResponse(url, sessionId, MediaType.APPLICATION_OCTET_STREAM);
                if (response.rawStatusCode() == 200) {
                  return response.toEntity(byte[].class).block().getBody();
                }
            }
        } catch(Exception ex) {
            System.out.println("OOPS! " + ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }

    public static List<String> getStepId(FolderNode file, String sessionId, boolean isProd) {
        String[] chunks = getFileParts(file);
        if (chunks == null) {
            return Collections.emptyList();
        }

        try {
            List<String> ids = new ArrayList<>();

            String url = makeUrl(isProd ? PROD_BASE_URL : DEV_BASE_URL, "collection", chunks[1], "import", chunks[2], "step");
            ClientResponse response = getResponse(url, sessionId, MediaType.APPLICATION_JSON);
            String   body   = response.toEntity(String.class).block().getBody();
            JsonNode jNode = JsonUtility.parseJson(body);
            List<JsonNode> steps = JsonUtility.getArrayValueAsNodes(jNode, "steps");
            for (JsonNode step : steps) {
                Integer stepId = JsonUtility.getIntValue(step, "id");
                if (stepId != null) {
                    ids.add(String.valueOf(stepId));
                }
            }
            return ids;
        } catch(Exception ex) {
            System.out.println("OOPS: " + ex);
        }

        return Collections.emptyList();
    }

    static String[] getFileParts(FolderNode file) {
        if (file.getType() != FolderType.FILE) {
            return null;
        }

        String[] chunks = file.getPath().split("/");
        return (chunks.length < 5) ? null : chunks;
    }

    static String makeUrl(String... parts) {
        return Arrays.stream(parts).collect(Collectors.joining("/"));
    }

    static ClientResponse getResponse(String url, String sessionId, MediaType mediaType) {
        WebClient client = WebClientHelper.getClient(); 
        return client.get()
                     .uri(url)
                     .accept(mediaType)
                     .headers(addHeaders(Collections.singletonMap("Authorization", "Bearer " + sessionId)))
                     .exchange()
                     .block();
    }

    /**
     * Return a {@link Consumer} that will add extra headers if they're not already defined.  If the list of
     * headers is null or empty, return a consumer that does nothing.
     *
     * @param headers headers to add
     * @return Consumer Headers
     */
    static Consumer<HttpHeaders> addHeaders(Map<String, String> headers) {
        if (headers == null  ||  headers.isEmpty()) {
            return httpH -> { };
        } else {
            return httpH -> {
                headers.forEach((key, value) -> {
                    if (!httpH.containsKey(key)) {
                        httpH.add(key, value);
                    }
                });
            };
        }
    }
}
