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
            String api_endpoint = "analysis/file/dynamic/v1";
            String corelation_id = null;
            HashMap<String, String> params_map = new HashMap<String, String>();
            HashMap<String, String> file_orjob_id = new HashMap<String, String>();
            file_orjob_id.put("file", "/home/devaljain/magneto/CLEAN_EXE/cb3be826125ed00556daf6b37ee96ddce096bf7aa78214aa8c9777dc0a30587b");
            // String file_ = "/home/devaljain/Anviron/AnViron/test/TestCode.java";            
            APIWrapper api_wrapper = new APIWrapper(api, api_endpoint, corelation_id, params_map, file_orjob_id);            
            HashMap<String, String> report_map = api_wrapper.get_file_report();
            System.out.println(report_map.get("/home/devaljain/magneto/CLEAN_EXE/cb3be826125ed00556daf6b37ee96ddce096bf7aa78214aa8c9777dc0a30587b"));
            
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}