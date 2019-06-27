import java.util.HashMap; 
import java.util.Map; 

import java.io.DataOutputStream; 
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.Charset;
import java.io.FileReader;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import org.json.simple.JSONObject;
import org.json.simple.parser.*; 

public class GetFileReport {
    String api_url;
    String api_endpoint;
    String url;
    String http_method;    
    HashMap<String, String> params;    
    String[] fileList;
    String content_type;
    String correlation_id;

    public GetFileReport(String api_url, String api_endpoint, String http_method, HashMap<String, String> params, 
                          String[] fileList, String content_type, String correlation_id){
        this.api_url = api_url;
        this.api_endpoint = api_endpoint;
        this.url = this.api_url + "/" + this.api_endpoint;
        this.http_method = http_method;        
        this.params = params;        
        this.fileList = fileList;
        this.content_type = content_type;   
        this.correlation_id = correlation_id;     
    }
    
    public String getAuthorization(AccessToken access_token) throws Exception{
        if(access_token == null || access_token.isTokenExpired()){
            HashMap<String, String> map = AccessToken.get_client_secret("secrets.env");
            AccessToken access_token2 = new AccessToken(map.get("client"), map.get("secret"));
            String token = access_token2.generate_access_token();            
            access_token = access_token2;
        }
        
        return access_token.getAccessToken();
    }

    public String get_params(Map<String, String> params) throws Exception{
        String params_str = "";
        if (params.isEmpty()){
            return "";
        }
        for (Map.Entry<String, String> entry: params.entrySet()) {
            params_str += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
        }
        params_str = params_str.replace(params_str.substring(params_str.length() - 1), "");
        return params_str;
    }

    public HashMap<String, String> makeApiRequests(AccessToken access_token) throws Exception{
        HashMap<String, String> report_map = new HashMap<String, String>();
        for (String file_ : fileList){
            URL url;
            if(http_method == "GET"){
                String url_str = this.url + "/" + file_ + "?" + get_params(this.params);
                URL temp_url = new URL(url_str);                
                url = temp_url;
            }else{
                URL temp_url = new URL(this.url + "/" + file_); 
                url = temp_url;
            }
            Proxy webproxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.labs.localsite.sophos", 8080));
            HttpURLConnection http_conn = (HttpURLConnection) url.openConnection(webproxy);
            String authorization = getAuthorization(access_token);

            http_conn.setRequestMethod(this.http_method);
            http_conn.setRequestProperty("Accept-Charset", "UTF-8");        
            http_conn.setRequestProperty("Authorization", authorization);
            if(this.content_type != null){
                http_conn.setRequestProperty("Content-Type", this.content_type);  
            }
            if(this.correlation_id != null){
                http_conn.setRequestProperty("X-Correlation-ID", this.correlation_id);  
            }

            if(http_method == "POST"){
                String postData = get_params(this.params);  
                http_conn.setDoOutput(true);
                try(DataOutputStream wr = new DataOutputStream(http_conn.getOutputStream())) {            
                    wr.write(postData.getBytes("UTF-8"));
                }
            }

            String data = "";        
            try{
                InputStream inputStream = http_conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    data = data + line;                
                }
            }catch (Exception e) {
                System.out.println(e.toString());
                data = "";
            }finally{
                http_conn.disconnect();
            }
            report_map.put(file_, data);
        }
        return report_map;
    }

}