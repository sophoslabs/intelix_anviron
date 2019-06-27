import java.util.HashMap; 
import java.util.Map; 
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject;
import org.json.simple.parser.*; 

public class TestCode{
    public static void main(String[] args){
        try{
            HashMap<String, String> map = AccessToken.get_client_secret("secrets.env");
            AccessToken access_token = new AccessToken(map.get("client"), map.get("secret"));
            String token = access_token.get_access_token();            
            Object obj = new JSONParser().parse(token);            
            JSONObject jo = (JSONObject) obj;
            String acc_token = String.valueOf(jo.get("access_token"));
            access_token.setAccessToken(acc_token);
            access_token.setTokenDateTime();            
            System.out.println(access_token.isTokenExpired());

        }catch (Exception e) {
            System.out.println(e.toString());
        }
        
    }
}