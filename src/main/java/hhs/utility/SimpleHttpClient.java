package hhs.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
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
 * plain text).  Both the POST and PUT methods require a JSON body.
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
        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createDefault();

        // Do a GET and parse the results
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", "application/json");
            headers.entrySet().forEach(hdr -> httpGet.addHeader(hdr.getKey(), hdr.getValue()));

            try (CloseableHttpResponse response = client.execute(httpGet);
                    InputStream ios = response.getEntity().getContent()) {
                String json = IOUtils.toString(ios, StandardCharsets.UTF_8);
                System.out.println("RESP.json: "  + response.getStatusLine());
                EntityUtils.consumeQuietly(response.getEntity());
                return json;
            }
        } catch(NullPointerException ex) {
            return null;
        } catch(Exception ex) {
            System.out.println("Url failed [" + url + "] --> " + ex.getMessage());
            return null;
        }
    }

    public static String doGetXML(String url, Map<String, String> headers) {
        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "text/xml");
        headers.entrySet().forEach(hdr -> httpGet.addHeader(hdr.getKey(), hdr.getValue()));

        try (CloseableHttpResponse response = client.execute(httpGet);
                InputStream ios = response.getEntity().getContent()) {
            String html = IOUtils.toString(ios, StandardCharsets.UTF_8);
            System.out.println("RESP.xml: "  + response.getStatusLine());
            EntityUtils.consumeQuietly(response.getEntity());
            return html;
        } catch (Exception ex) {
            System.out.println("Url failed [" + url + "] --> " + ex.getMessage());
            return null;
        }
    }

    public static String doGetText(String url, Map<String, String> headers) {
        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "text/plain");
        headers.entrySet().forEach(hdr -> httpGet.addHeader(hdr.getKey(), hdr.getValue()));

        try (CloseableHttpResponse response = client.execute(httpGet);
                InputStream ios = response.getEntity().getContent()) {
            String html = IOUtils.toString(ios, StandardCharsets.UTF_8);
            System.out.println("RESP.text: "  + response.getStatusLine());
            EntityUtils.consumeQuietly(response.getEntity());
            return html;
        } catch (Exception ex) {
            System.out.println("Url failed [" + url + "] --> " + ex.getMessage());
            return null;
        }
    }

    public static String doGetHTML(String url, Map<String, String> headers) {
        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "text/html");
        headers.entrySet().forEach(hdr -> httpGet.addHeader(hdr.getKey(), hdr.getValue()));

        try (CloseableHttpResponse response = client.execute(httpGet);
                InputStream ios = response.getEntity().getContent()) {
            String html = IOUtils.toString(ios, StandardCharsets.UTF_8);
            System.out.println("RESP.html: "  + response.getStatusLine());
            EntityUtils.consumeQuietly(response.getEntity());
            return html;
        } catch (Exception ex) {
            System.out.println("Url failed [" + url + "] --> " + ex.getMessage());
            return null;
        }
    }

    public static String doPostJson(String url, String body, Map<String, String> headers) {
        // POST the request, return the "LOCATION" header
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Accept", "application/json");
            headers.entrySet().forEach(hdr -> httpPost.addHeader(hdr.getKey(), hdr.getValue()));
            StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                Header header = response.getFirstHeader("LOCATION");
                return (header == null) ? null : header.getValue();
            }
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }

    public static String doPutJson(String url, String body, Map<String, String> headers) {
        // PUT the request, but don't show any concern about the response
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(url);
            httpPut.addHeader("Accept", "application/json");
            headers.entrySet().forEach(hdr -> httpPut.addHeader(hdr.getKey(), hdr.getValue()));
            StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
            httpPut.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(httpPut)) {
                Header header = response.getFirstHeader("LOCATION");
                return (header == null) ? null : header.getValue();
            }
        } catch (IOException ex) {
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
        // Closing this would also close the underlying Http-Connection-Manager, which would be unfortunate
        CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);

        HttpDelete httpDelete = new HttpDelete(url);
        headers.entrySet().forEach(hdr -> httpDelete.addHeader(hdr.getKey(), hdr.getValue()));
        
        try (CloseableHttpResponse response = client.execute(httpDelete)) {
            return response.getStatusLine().getStatusCode() < 300;
        } catch (IOException ex) {
            System.out.println("DELETE failed [" + url + "] --> " + ex.getMessage());
            return false;
        }
    }

}
