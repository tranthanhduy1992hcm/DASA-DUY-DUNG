/*
 * Write something
 */

package core;

import core.net.DriveRequest;
import core.net.Param;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.scribe.model.Token;

/**
 *
 * @author Duy-Du~ng
 */
public class TestURI {
    private static final String ROOT_META = "https://www.googleapis.com/drive/v2/files";
    private static final String MAX_RESULTS = "maxResults";
    private static final String RESULTS_LIMIT_VALUE = "1000";
    
        public static void main(String[] args) throws URISyntaxException, IOException{
		//Token of Google Drive API
            Token token = new Token("ya29.HAEkbZQbfYjFC1E08NFzQoQSR_dyVacP3_7QbjYe_aLI8o1Ho__QeuDPFL5konGt_YZPcoOsl96jwQ","8dkUycJV9kS9Rkd9mRo1_-sN");
      
            //String ID = "4354dfhgdhfg";
            String ID = "0B7pgi-vO8o3RYmN0ZU4tN1NhY28";
			URI uri;
        uri = Param.create()
                .setUrl(ROOT_META)
                .setParam(MAX_RESULTS, RESULTS_LIMIT_VALUE)  
                .buildURI();
        String str = uri.toString();
        str = setquery(str, ID);
        str = settoken(str, token.getToken().toString());
        System.out.println(str);
    }
        
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
