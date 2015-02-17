package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.api.DriveApi;
import core.net.DriveRequest;
import core.net.Json;
import core.pojos.DriveDir;
import core.pojos.DriveItem;

import core.pojos.DriveParent;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import java.util.ArrayList;


import java.util.List;
import java.util.Scanner;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
/**
 * Hello world!
 *
 */
public class DriveApiExample {
    
//Token[ya29.HAEkbZQbfYjFC1E08NFzQoQSR_dyVacP3_7QbjYe_aLI8o1Ho__QeuDPFL5konGt_YZPcoOsl96jwQ , ]
    private static final String NETWORK_NAME = "Google Drive";
    private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/drive/v2/files";
//    private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/drive/v2/files?maxResults=50&q='me'+in+owners+and+'root'+in+parents&key=ya29.1.AADtN_W77eq-NEdZMRufxcCMpDP-4f0HGQs53ZvuCF0fOLc8z3qJD661bNVaTvtTVimJzoEP";
//    https://www.googleapis.com/drive/v2/files?maxResults=50&q='me'+in+owners+and+'root'+in+parents&key={YOUR_API_KEY}
    private static final Token EMPTY_TOKEN = null;

    public static void main(String[] args) throws IOException, URISyntaxException {
	//registe the Google Drive API key
	//For register API, I used the API key follow
	//Project name: DASA-13
        String CLIENT_ID = "925073408929-0kb4si54i6orq2qpplqa4eu7s2gt7p38.apps.googleusercontent.com";
        String CLIENT_SECRET = "8dkUycJV9kS9Rkd9mRo1_-sN";
        String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";	//redirect_uri = localhost

        OAuthService service = new ServiceBuilder()
                .provider(DriveApi.class)
                .apiKey(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .callback(REDIRECT_URI)	//localhost
                .scope("https://www.googleapis.com/auth/drive")
                .build();
        
        Token accessToken = getToken(service);
        
//        getFiles(service, accessToken);
        
        DriveRequest dr = new DriveRequest(accessToken);
        //String ls = dr.list("0B_vA8Zj1ofHCRnp4c3NieGVfMEU");
        //System.out.println(ls);

    }
    
    private static Token getToken(OAuthService service) {
        Scanner in = new Scanner(System.in);
        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        System.out.println("Got the Authorization URL!");
        System.out.println(authorizationUrl);
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        Verifier verifier = new Verifier(in.nextLine());

        System.out.println();
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();
        return accessToken;
    }
    
    private static void getFiles(OAuthService service, Token accessToken) throws FileNotFoundException, IOException {
        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
//        request.addQuerystringParameter("method", "flickr.test.login");
        service.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getBody());
       

        FileOutputStream fos = new FileOutputStream("D:\\test.json");
        fos.write(response.getBody().getBytes());
        fos.flush();
        fos.close();

        Json json2list = new Json();
        List<DriveItem> ToDriveList = Json.ToDriveList(response.getBody());
        System.out.println("\n TEST tat ca file cung voi ID!!!!");
        for(int i = 0; i < ToDriveList.size(); i++){
            List<DriveItem> Parent2ListItem = Parent2ListItem(ToDriveList,ToDriveList.get(i).getDriveParents());
        System.out.print(" file ID: "+ ToDriveList.get(i).getId()+", Name file: "+ ToDriveList.get(i).getTitle() +", number parent :"+ ToDriveList.get(i).getDriveParents().size() +", mime type :"+ ToDriveList.get(i).getMimeType() +", list parents :" );
        showParentItems(Parent2ListItem);
        System.out.println();
        }
        
        System.out.println("\n test thu muc goc! :");                
        List<DriveItem> rl = Rootlist(ToDriveList);
        showRootListItems(rl);
        
        System.out.println("\n Test thu muc goc :");
        List<DriveItem> fl = Childlist(ToDriveList, ToDriveList.get(2));
        showRootListItems(fl);
        
        System.out.println("\n Test thu muc test :");
        List<DriveItem> f2 = Childlist(ToDriveList, ToDriveList.get(1));
        showRootListItems(f2);
        
        System.out.println("\n Test thu muc test2 :");
        List<DriveItem> f3 = Childlist(ToDriveList, ToDriveList.get(0));
        showRootListItems(f3);

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with Scribe! :)");
        
        

      }

    public static DriveItem SearchItembyID(List<DriveItem> items, String ID) {
        DriveItem item = new DriveItem();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(ID)) {
                item = items.get(i);
            }
        }
        return item;
    }
    
    public static List<DriveItem> Parent2ListItem(List<DriveItem> items, List<DriveParent> parents){
        List<DriveItem> parentList = new ArrayList<>();
        for(int i = 0; i < parents.size(); i++){
           parentList.add( SearchItembyID(items, parents.get(i).getId()) );
        }
        return parentList;
    }
    
    public static void showParentItems(List<DriveItem> items){
            for (int i = 0; i < items.size(); i++) {
                System.out.print(items.get(i).getTitle()+ ", ");
            }
    }
    
    public static void showRootListItems(List<DriveItem> items){
            for (int i = 0; i < items.size(); i++) {
                System.out.println(items.get(i).getTitle());
            }
    }
    
    
    
    //all must be list item
    //1/ Create Root Folder
    // create the root folder that have the file inside have no parent or parent = null
    //the children (folders) have foldertype(mime) and the parent is root  
    public static List<DriveItem> Rootlist(List<DriveItem> items){
        List<DriveItem> temp = new ArrayList<>();
        for(int i = 0; i < items.size() ; i++){
            if( (items.get(i).getDriveParents().size() != 1) || (items.get(i).getMimeType().equals("application/vnd.google-apps.folder") && items.get(i).getDriveParents().get(0).getIsRoot()) && items.get(i).getUserPermission().getRole().equals("owner")  ){
            temp.add(items.get(i));
            } 
        }
        return temp;
    }
    
    //all must be list item
    //2/ Create Folder
    // when choose folder serch list that have that ID of this folder
    public static List<DriveItem> Childlist(List<DriveItem> items, DriveItem item) {
        List<DriveItem> temp = new ArrayList<>();
        if (item.getMimeType().equals("application/vnd.google-apps.folder")) {
            for (int i = 0; i < items.size(); i++) {
                if ((items.get(i).getDriveParents().size() != 0) && (items.get(i).getUserPermission().getRole().equals("owner")) ) {
                    if (items.get(i).getDriveParents().get(0).getId().equals(item.getId())) {
                        temp.add(items.get(i));
                    }
                }
            }
        }
        return temp;
    }



    

}
