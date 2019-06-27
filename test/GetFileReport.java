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
import java.io.File;
import java.nio.file.Files;
import java.io.FileInputStream;

public class GetFileReport {
    String api_url;
    String api_endpoint;
    String url;
    String http_method;    
    HashMap<String, String> params;    
    String file;
    String content_type;
    String correlation_id;
    String job_id;
    String sha256;

    public GetFileReport(String api_url, String api_endpoint, String http_method, HashMap<String, String> params, 
                          String file, String content_type, String correlation_id, String job_id, String sha256){
        this.api_url = api_url;
        this.api_endpoint = api_endpoint;
        this.url = this.api_url + "/" + this.api_endpoint;
        this.http_method = http_method;        
        this.params = params;        
        this.file = file;
        this.content_type = content_type;   
        this.correlation_id = correlation_id;
        this.job_id = job_id; 
        this.sha256 = sha256;
    }
    
    public String getAuthorization(AccessToken access_token) throws Exception{
        if(access_token == null){
            HashMap<String, String> map = AccessToken.get_client_secret("secrets.env");
            AccessToken access_token2 = new AccessToken(map.get("client"), map.get("secret"));
            String token = access_token2.generate_access_token();            
            access_token = access_token2;
        }
        else if(access_token.isTokenExpired()){
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

        String local_url = this.url;        
        if(this.api_endpoint.indexOf("lookup") != -1 && this.sha256 != null){
            local_url += "/" + this.sha256;
        }

        if(this.api_endpoint.indexOf("analysis") != -1 && this.job_id != null){
            local_url += "/" + this.job_id;
        }
        
        if(this.api_endpoint.indexOf("analysis") != -1 && this.sha256 != null){
            this.params.put("sha256", this.sha256);
        }

        if(this.api_endpoint.indexOf("analysis") != -1 && this.file != null && this.http_method == "POST"){
            // File fileToUpload = new File(this.file);
            // FileInputStream fis = new FileInputStream(fileToUpload);            
            // byte[] byteArr = new byte[(int) fileToUpload.length()];
            // fis.read(byteArr);
            this.params.put("file", this.file);
        }

        URL url;
        if(http_method == "GET" && !this.params.isEmpty()){
            String url_str = local_url + "?" + get_params(this.params);
            URL temp_url = new URL(url_str);                
            url = temp_url;
        }else{
            URL temp_url = new URL(local_url); 
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
            if (http_conn.getResponseCode() == 400){
                data = "Report not present";
            }else{
                data = "";
            }
            System.out.println(e.toString());
            
        }finally{
            http_conn.disconnect();
        }        
        if(this.file != null){
            report_map.put(this.file, data);            
        }
        if(this.sha256 != null){
            report_map.put(this.sha256, data);
        }
        if(this.job_id != null){
            report_map.put(this.job_id, data);
        }
        return report_map;

    }    
    
}