import java.util.HashMap; 
import java.util.Map; 
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject;
import org.json.simple.parser.*; 
import java.io.File;
import java.nio.file.Files;
import java.io.FileInputStream;

public class TestCode{
    public static void main(String[] args){                
        try{
            String api = "https://de.api.labs.sophos.com";
            String api_endpoint = "analysis/file/static/v1/reports";
            String corelation_id = null;
            HashMap<String, String> params_map = new HashMap<String, String>();
            HashMap<String, String> file_orjob_id = new HashMap<String, String>();
            file_orjob_id.put("sha256", "a0e46d62b90aad2eb28bda87b90656cb817fe2c72aadb6f3155ccf1f66505bd4");
            // String file_ = "/home/devaljain/Anviron/AnViron/test/TestCode.java";            
            APIWrapper api_wrapper = new APIWrapper(api, api_endpoint, corelation_id, params_map, file_orjob_id);            
            HashMap<String, String> report_map = api_wrapper.get_file_report();
            System.out.println(report_map.get("a0e46d62b90aad2eb28bda87b90656cb817fe2c72aadb6f3155ccf1f66505bd4"));

            
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}