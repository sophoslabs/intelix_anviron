import java.util.HashMap; 
import java.util.Map; 
import org.json.simple.JSONObject;
import org.json.simple.parser.*; 
import java.util.concurrent.TimeUnit;

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
        HashMap<String, String> report_map = new HashMap<String, String> ();
        access_token.generate_access_token();
        if(this.http_method == "GET"){
            GetFileReport getFileReport = new GetFileReport(this.api_url, this.api_endpoint, this.http_method, this.params_map, this.file, this.content_type,this.correlation_id, this.job_id, this.sha256);
            report_map = getFileReport.makeApiRequests(access_token);
        }else{
            POSTFile post_file = new POSTFile(this.api_url, this.api_endpoint, this.file, this.correlation_id);               
            String resp = post_file.submitFile(access_token);
            report_map.put(this.file, resp);
            // JSONParser parser = new JSONParser();
            // JSONObject json = (JSONObject) parser.parse(resp);
            // String jobStatus = (String) json.get("jobStatus");
            // this.job_id = (String) json.get("jobId");  
            // System.out.println(jobStatus);
            // while(jobStatus == "IN_PROGRESS"){
            //     TimeUnit.SECONDS.sleep(30);
            //     if(this.api_endpoint.indexOf("reports") == -1){
            //         this.api_endpoint = this.api_endpoint + "/reports";
            //     }
            //     this.http_method = "GET";
            //     this.content_type = "application/json";
            //     GetFileReport getFileReport = new GetFileReport(this.api_url, this.api_endpoint, this.http_method, this.params_map, this.file, this.content_type,this.correlation_id, this.job_id, this.sha256);
            //     report_map = getFileReport.makeApiRequests(access_token);
            //     json = (JSONObject) parser.parse(report_map.get(this.job_id));
            //     jobStatus = (String) json.get("jobStatus");
            //     report_map.put(this.file, report_map.get(this.job_id));
            // }           
            
        }
        return report_map;

    }
}