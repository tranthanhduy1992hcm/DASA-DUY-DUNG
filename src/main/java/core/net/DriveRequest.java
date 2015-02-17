/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core.net;

import core.pojos.DriveItem;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.scribe.model.Token;
/**
 *
 * @author Duy-Du~ng
 */
public class DriveRequest {

    private final String ROOT_META = "https://www.googleapis.com/drive/v2/files";
//    private final String ROOT_GET = "https://api-content.dropbox.com/1/files/dropbox";
//    private final String ROOT_PUT = "https://api-content.dropbox.com/1/files_put/dropbox/";

    private final String MAX_RESULTS = "maxResults";
    private final String RESULTS_LIMIT_VALUE = "1000";

    // access token
    private final Token token;

    public DriveRequest(Token token) {
        this.token = token;
    }

    /**
     * Retrieves file and folder metadata
     * @param path The relative path to the file or folder
     * @return JSON String
     * @throws URISyntaxException Invalid URI
     * @throws IOException Bad request
     */
    public String list(String ID) throws URISyntaxException, IOException {     
        URI uri;
        uri = Param.create()
                .setUrl(ROOT_META)
                .setParam(MAX_RESULTS, RESULTS_LIMIT_VALUE)  
                .buildURI();
        String struri = uri.toString();
        struri = setquery(struri, ID);
        struri = settoken(struri, token.getToken().toString());
        System.out.println(struri);
        return Request.Get(struri)
                .execute()
                .returnContent().asString();
    }
    
    
//    /**
//     * Downloads a file
//     * @param path The relative path to the file you want to retrieve
//     * @param out Output stream to write downloaded file
//     * @throws URISyntaxException
//     * @throws IOException 
//     */
//    public void download(String path, OutputStream out) throws URISyntaxException, IOException {
//
//        URI uri = Param.create()
//                .setUrl(combine(ROOT_GET, path))
//                .setToken(token)
//                .buildURI();
//        byte[] data = Request.Get(uri)
//                .execute().returnContent().asBytes();
//        out.write(data);
//    }
//    
//    /**
//     * Uploads a file
//     * @param path The full path to the file you want to write to
//     * @param file The upload file
//     * @return JSON String
//     * @throws URISyntaxException
//     * @throws IOException 
//     */
//    public String upload(String path, File file) throws URISyntaxException, IOException {
//        URI uri = Param.create()
//                .setUrl(combine(ROOT_PUT, path))
//                .setToken(token)
//                .buildURI();
//        return Request.Put(uri)
//                .bodyFile(file, ContentType.APPLICATION_OCTET_STREAM)
//                .execute()
//                .returnContent().asString();
//    }

    /**
     * Combine root and path
     * @param root Root path of Dropbox. Using constant value in class DropboxRequest
     * @param path Path to file or folder
     * @return 
     */
        //q='me'+in+owners+and+'root'+in+parents
    private static String setquery(String root, String ID) {
            ID = "&q='me'+in+owners+and+'"+ID+"'+in+parents";
        return root + ID;
    }
    
        private static String settoken(String root, String token) {
            token = "&access_token="+token;
        return root + token;
    }
    
}
