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
            String api_endpoint = "analysis/file/static/v1";
            String corelation_id = null;
            HashMap<String, String> params_map = new HashMap<String, String>();
            HashMap<String, String> file_orjob_id = new HashMap<String, String>();
            file_orjob_id.put("file", "/home/devaljain/magneto/CLEAN_DOC/1665545a512f826bc94f5272f5dece159c3a43df6e2a1d031916fb98830aa45a");
            // String file_ = "/home/devaljain/Anviron/AnViron/test/TestCode.java";            
            APIWrapper api_wrapper = new APIWrapper(api, api_endpoint, corelation_id, params_map, file_orjob_id);            
            HashMap<String, String> report_map = api_wrapper.get_file_report();
            System.out.println(report_map.get("/home/devaljain/magneto/CLEAN_DOC/1665545a512f826bc94f5272f5dece159c3a43df6e2a1d031916fb98830aa45a"));
            
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}