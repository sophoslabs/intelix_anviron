import java.util.HashMap; 
import java.util.Map; 

public class APIWrapper {
    String api_url;
    String api_endpoint;
    String file;
    String job_id;
    String sha256;
    HashMap<String, String> file_orjobid;
    String content_type;
    HashMap<String,String> params_map;
    String http_method;
    String correlation_id;

    public APIWrapper(String api_url, String api_endpoint, String correlation_id, HashMap<String, String> params_map, HashMap<String, String> file_orjobid) {
        this.api_url = api_url;
        this.api_endpoint = api_endpoint;
        this.correlation_id = correlation_id;
        this.params_map = params_map;
        this.file_orjobid = file_orjobid;
        this.http_method = http_method;
        this.job_id = file_orjobid.get("job_id");
        this.sha256 = file_orjobid.get("sha256");
        this.file = file_orjobid.get("file");
        
        if(api_endpoint.indexOf("lookup") != -1){
            set_params_lookup_apis(api_url, api_endpoint, correlation_id, params_map);
        }else if (api_endpoint.indexOf("analysis") != -1){
            set_params_analysis_apis(api_url, api_endpoint, correlation_id, params_map, file_orjobid);
        }

    }

    public void set_params_lookup_apis(String api_url, String api_endpoint, String correlation_id, HashMap<String, String> params_map){
        this.http_method = "GET";
        this.content_type = "application/json";
    }

    public void set_params_analysis_apis(String api_url, String api_endpoint, String correlation_id, 
                                     HashMap<String, String> params_map, HashMap<String, String> file_orjobid){
        
        if(api_endpoint.indexOf("reports") == -1){
            this.http_method = "POST";
            this.content_type = "multipart/form-data";      
        } else{
            this.http_method = "GET";
            this.content_type = "application/json";            
        }
            
    }

    public HashMap<String, String> get_file_report() throws Exception{
        HashMap<String, String> map = AccessToken.get_client_secret("secrets.env");
        AccessToken access_token = new AccessToken(map.get("client"), map.get("secret"));
        access_token.generate_access_token();
        GetFileReport getFileReport = new GetFileReport(this.api_url, this.api_endpoint, this.http_method,this.params_map, this.file, this.content_type,this.correlation_id, this.job_id, this.sha256);
        return  getFileReport.makeApiRequests(access_token);
    }
}