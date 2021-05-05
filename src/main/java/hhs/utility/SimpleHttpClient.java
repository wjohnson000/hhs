package hhs.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

/**
 * A thin wrapper around the apache "HttpClient" framework.  It defines three methods for doing a GET, and one each
 * for POST, PUT, and DELETE.  The "GET" multiple methods differ only in the content-type returned (JSON, XML or
 * plain text).  Both the PUT method requires a JSON body, for the POST method a JSON body is optional.
 *  * <p/>
 * 
 * When called with the "verbose" flag, all URLs and responses will be logged to 'System.out'.
 * 
 * @author wjohnson000
 *
 */
public class SimpleHttpClient {

    static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(5);
        httpConnManager.setDefaultMaxPerRoute(5);
    }

    public static String doGetJSON(String url, Map<String, String> headers) {
        return doGetJSON(url, headers, false);
    }

    public static String doGetJSON(String url, Map<String, String> headers, boolean verbose) {
        return doGetGeneric(url, headers, "application/json", verbose);
    }

    public static String doGetXML(String url, Map<String, String> headers) {
        return doGetXML(url, headers, false);
    }

    public static String doGetXML(String url, Map<String, String> headers, boolean verbose) {
        return doGetGeneric(url, headers, "text/xml", verbose);
    }

    public static String doGetText(String url, Map<String, String> headers) {
        return doGetText(url, headers, false);
    }

    public static String doGetText(String url, Map<String, String> headers, boolean verbose) {
        return doGetGeneric(url, headers, "text/plain", verbose);
    }

    public static String doGetHTML(String url, Map<String, String> headers) {
        return doGetHTML(url, headers, false);
    }

    public static String doGetHTML(String url, Map<String, String> headers, boolean verbose) {
        return doGetGeneric(url, headers, "text/html", verbose);
    }

    protected static String doGetGeneric(String url, Map<String, String> headers, String acceptType, boolean verbose) {
        logRequest("GET", url, headers, verbose);

        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", acceptType);
        headers.entrySet().forEach(hdr -> httpGet.addHeader(hdr.getKey(), hdr.getValue()));

        try (CloseableHttpResponse response = client.execute(httpGet);
                InputStream ios = response.getEntity().getContent()) {
            logResponse(response, verbose, null);
            String html = IOUtils.toString(ios, StandardCharsets.UTF_8);
            EntityUtils.consumeQuietly(response.getEntity());
            return html;
        } catch (Exception ex) {
            logResponse(null, verbose, ex);
            return null;
        }
    }

    public static String doPostJson(String url, String body, Map<String, String> headers) {
        return doPostJson(url, body, headers, false);
    }

    public static String doPostJson(String url, String body, Map<String, String> headers, boolean verbose) {
        logRequest("POST", url, headers, verbose);

        // POST the request, return the "LOCATION" header
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Accept", "application/json");
            headers.entrySet().forEach(hdr -> httpPost.addHeader(hdr.getKey(), hdr.getValue()));
            if (body != null) {
                if (verbose) {
                    System.out.println("BODY:\n" + body);
                }
                StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                logResponse(response, verbose, null);
                Header header = response.getFirstHeader("LOCATION");
                return (header == null) ? null : header.getValue();
            }
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public static String doPutJson(String url, String body, Map<String, String> headers) {
        return doPutJson(url, body, headers, false);
    }

    public static String doPutJson(String url, String body, Map<String, String> headers, boolean verbose) {
        logRequest("PUT", url, headers, verbose);

        // PUT the request, but don't show any concern about the response
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(url);
            httpPut.addHeader("Accept", "application/json");
            headers.entrySet().forEach(hdr -> httpPut.addHeader(hdr.getKey(), hdr.getValue()));
            StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPut)) {
                logResponse(response, verbose, null);
                Header header = response.getFirstHeader("LOCATION");
                return (header == null) ? null : header.getValue();
            }
        } catch (IOException ex) {
            logResponse(null, verbose, ex);
            return ex.getMessage();
        }
    }

    /**
     * Submit a "DELETE" request to delete a resource, returning true if it was deleted
     * 
     * @param url URL to hit
     * @param headers request headers to be set (should NOT include "Accept")
     * @return
     */
    public static boolean doDelete(String url, Map<String, String> headers) {
        return doDelete(url, headers, false);
    }

    public static boolean doDelete(String url, Map<String, String> headers, boolean verbose) {
        logRequest("DELETE", url, headers, verbose);

        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);

        HttpDelete httpDelete = new HttpDelete(url);
        headers.entrySet().forEach(hdr -> httpDelete.addHeader(hdr.getKey(), hdr.getValue()));
        try (CloseableHttpResponse response = client.execute(httpDelete)) {
            logResponse(response, verbose, null);
            return response.getStatusLine().getStatusCode() < 300;
        } catch (IOException ex) {
            logResponse(null, verbose, ex);
            return false;
        }
    }

    private static void logRequest(String method, String url, Map<String, String> headers, boolean verbose) {
        if (verbose) {
            System.out.println(">>>>> " + method + " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("  url: " + url);
            headers.entrySet().forEach(hdr -> {
                if (hdr.getKey().equalsIgnoreCase(HttpHeaders.AUTHORIZATION)) {
                    System.out.println("  hdr: " + hdr.getKey() + ": " + hdr.getValue().subSequence(0, Math.min(20, hdr.getValue().length())));
                } else {
                    System.out.println("  hdr: " + hdr);
                }
            });
        }
    }

    private static void logResponse(CloseableHttpResponse response, boolean verbose, Exception ex) {
        if (verbose) {
            if (response != null) {
                System.out.println("Response ...");
                System.out.println("  stt: " + response.getStatusLine());
//                Arrays.stream(response.getAllHeaders()).forEach(hdr -> System.out.println("  hdr: " + hdr));
            }
            if (ex != null) {
                System.out.println("Failure ...");
                System.out.println("  ex.message: " + ex.getMessage());
            }
        }
    }
}
