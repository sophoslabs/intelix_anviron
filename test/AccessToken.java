import java.io.DataOutputStream; 
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap; 
import java.util.Map; 
import java.util.Date;
import java.nio.charset.Charset;
import java.io.FileReader;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.*; 

public class AccessToken {
    String client;
    String secret;
    String url;
    String grant_type;
    String content_type;
    String access_token;
    Date token_creation_time;

    public AccessToken(String client, String secret){
        this.client = client;
        this.secret = secret ;       
        this.url = "https://api.labs.sophos.com/oauth2/token";
        this.grant_type = "client_credentials";
        this.content_type = "application/x-www-form-urlencoded;charset=UTF-8";
    }

    public String getEncoding() throws Exception{
        return Base64.getEncoder().encodeToString(
            (this.client + ":" + this.secret).getBytes("UTF-8")
        );
    }

    public void setAccessToken(String token){
        this.access_token = token;
    }

    public String getAccessToken(){
        return this.access_token;
    }

    public void setTokenDateTime(){
        this.token_creation_time = new Date();
    }

    public boolean isTokenExpired(){
        Date curr_time = new Date();
        long HOUR = 3600 * 1000;
        Date compDate = new Date(this.token_creation_time.getTime() + 1 * HOUR);
        if(compDate.compareTo(curr_time) > 0){
            return false;
        }else {
            return true;
        }
    }

    public String get_auth(String encoding){
        return "Basic " + encoding;
    }

    public String get_params() throws Exception{
        return String.format("grant_type=%s", URLEncoder.encode(this.grant_type, "UTF-8"));
    }

    public static HashMap<String, String> get_client_secret(String filepath) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        HashMap<String, String> creds = new HashMap<>(); 
        String st;
        while((st = br.readLine()) != null){
            String[] key_val = st.split("=");            
            creds.put(key_val[0], key_val[1]);
        }
        return creds;
    }

    public String generate_access_token() throws Exception{
        URL url = new URL(this.url);                
        Proxy webproxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.labs.localsite.sophos", 8080));
        HttpURLConnection http_con = (HttpURLConnection) url.openConnection(webproxy);
        String encoding = getEncoding();
        String auth = get_auth(encoding);
        http_con.setDoOutput(true);   
        http_con.setRequestMethod("POST");
        http_con.setRequestProperty("Accept-Charset", "UTF-8");        
        http_con.setRequestProperty("Authorization", auth);
        http_con.setRequestProperty("Content-Type", this.content_type);        
        String postData = get_params();        
        try(DataOutputStream wr = new DataOutputStream(http_con.getOutputStream())) {            
            wr.write(postData.getBytes("UTF-8"));
        }
             
        String data = "";        
        try{
            InputStream inputStream = http_con.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                data = data + line;                
            }
        }catch (Exception e) {
            System.out.println(e.toString());
            data = "";
        }finally{
            http_con.disconnect();
        }

        Object obj = new JSONParser().parse(data);
        JSONObject jo = (JSONObject) obj;
        String acc_token = String.valueOf(jo.get("access_token"));
        setAccessToken(acc_token);
        setTokenDateTime();
        System.out.println(acc_token);

        return acc_token;

    }

}