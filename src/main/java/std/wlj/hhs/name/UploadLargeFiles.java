/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Upload (POST) large files to "homelands-admin" endpoint.  Hopefully ...
 * 
 * @author wjohnson000
 *
 */
public class UploadLargeFiles {

    /**
     * A generic method to execute any type of Http Request and constructs a response object
     * @param requestBase the request that needs to be exeuted
     * @return server response as <code>String</code>
     */
    private static String executeRequest(HttpRequestBase requestBase){
        int timeout = 120;
        RequestConfig config = RequestConfig.custom()
                                .setConnectTimeout(timeout * 1000)
                                .setConnectionRequestTimeout(timeout * 1000)
                                .setSocketTimeout(timeout * 1000).build();

        try(CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            HttpResponse response = client.execute(requestBase);
            return response.getStatusLine().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Method that builds the multi-part form data request
     * @param urlString the urlString to which the file needs to be uploaded
     * @param file the actual file instance that needs to be uploaded
     * @param fileName name of the file, just to show how to add the usual form parameters
     * @param fileDescription some description for the file, just to show how to add the usual form parameters
     * @return server response as <code>String</code>
     */
    public String executeMultiPartRequest(String urlString, File file, String fileName, String fileDescription) {

        HttpPost postRequest = new HttpPost (urlString);
        try {
            MultipartEntityBuilder mpEntityBuilder = MultipartEntityBuilder.create();
            InputStream inputStream = new FileInputStream(file);
            mpEntityBuilder.addBinaryBody("file", inputStream, ContentType.APPLICATION_OCTET_STREAM, fileName);
            postRequest.setEntity(mpEntityBuilder.build());
            return executeRequest (postRequest);
        } catch (Exception ex){
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    public static void main(String args[]) {
        String url = "http://admin.homelands.service.dev.us-east-1.dev.fslocal.org/backup";
//        String url = "http://localhost:8080/hhs-admin/backup";
        UploadLargeFiles fileUpload = new UploadLargeFiles ();
        File file = new File ("C:/temp/wlj-test-006.txt");
//        File file = new File ("C:/D-drive/homelands/names/Names-and-Definitions-from-missionaries.xlsx");
//        File file = new File ("C:/D-drive/homelands/names/Names-and-Their-Meanings-Export.xlsx");

        if (file.exists()) {
            String response = fileUpload.executeMultiPartRequest(url, file, file.getName(), "File Upload test POST");
            System.out.println("Response : " + response);
        }
    }  

}
