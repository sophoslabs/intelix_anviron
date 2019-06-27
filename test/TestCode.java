import java.util.HashMap; 
import java.util.Map; 
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject;
import org.json.simple.parser.*; 

public class TestCode{
    public static void main(String[] args){
        // try{
        // HashMap<String, String> map = AccessToken.get_client_secret("secrets.env");
        // AccessToken access_token = new AccessToken(map.get("client"), map.get("secret"));
        //     String token = access_token.get_access_token();            
        //     Object obj = new JSONParser().parse(token);            
        //     JSONObject jo = (JSONObject) obj;
        //     String acc_token = String.valueOf(jo.get("access_token"));
        //     access_token.setAccessToken(acc_token);
        //     access_token.setTokenDateTime();            
        //     System.out.println(access_token.isTokenExpired());

        // }catch (Exception e) {
        //     System.out.println(e.toString());
        // }
        try{
        String api_url = "https://de.api.labs.sophos.com";
        String api_endpoint = "lookup/files/v1";
        String[] file_ = {"ffa26ef63b4a507fecc82978f7ad6f46b618e362e23948a427637a3c5231d667"};
        String http_method = "GET";
        HashMap<String, String> params_map = new HashMap<String, String>();
        // params_map.put("X-Correlation-ID", "My-Unique-ID");
        String content_type = null;
        String cor_id = "ID_1";
        
        HashMap<String, String> map = AccessToken.get_client_secret("secrets.env");
        AccessToken access_token = new AccessToken(map.get("client"), map.get("secret"));
        String token = access_token.generate_access_token();

        GetFileReport getFileReport = new GetFileReport(api_url, api_endpoint, http_method, params_map, file_, content_type, cor_id);
        HashMap<String, String> report_map = getFileReport.makeApiRequests(access_token);
        
        System.out.println(report_map.get("ffa26ef63b4a507fecc82978f7ad6f46b618e362e23948a427637a3c5231d667"));
        }catch(Exception e){
            System.out.println(e.toString());
        }

    }
}