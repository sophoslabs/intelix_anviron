import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.HashMap; 
import java.util.Map; 
import java.net.Proxy;
import java.net.InetSocketAddress;


public class POSTFile{
    String api_url;
    String api_endpoint;
    String url;
    String file;
    String content_type;
    String correlation_id;
    String http_method;

    public POSTFile(String api_url, String api_endpoint, String file, String correlation_id){
        this.api_url = api_url;
        this.api_endpoint = api_endpoint;
        this.url = api_url + "/" + api_endpoint;
        this.file = file;
        this.content_type = "multipart/form-data";
        this.http_method = "POST";
        this.correlation_id = correlation_id;
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

    public String submitFile(AccessToken access_token) throws Exception{
        File file_obj = new File(this.file); 
        String authorization = getAuthorization(access_token);
        Proxy webproxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.labs.localsite.sophos", 8080));
        OkHttpClient client = new OkHttpClient.Builder().proxy(webproxy).build();
        //OkHttpClient client = new OkHttpClient.Builder().build();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", "filename",
            RequestBody.create(MediaType.parse("application/octet-stream"), file_obj)).build();

        Request request = new Request.Builder().header("Authorization", authorization).url(this.url).post(requestBody).build();

        try (Response response = client.newCall(request).execute()){
            if(!response.isSuccessful()) return "NA";
            return (response.body().string());
        }

    }

}