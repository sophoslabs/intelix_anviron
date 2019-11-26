/*
 * #  Copyright (c) 2019. Sophos Limited
 * #
 * #  Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 */

package com.sophos.anviron.util.main;

import com.sophos.anviron.MainActivity;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.HashMap;
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

            AccessToken access_token2 = new AccessToken(MainActivity.api_client, MainActivity.api_secret);
            access_token = access_token2;
        }
        else if(access_token.isTokenExpired()){
                HashMap<String, String> map = AccessToken.get_client_secret("secrets.env");
                AccessToken access_token2 = new AccessToken(map.get("client"), map.get("secret"));
                access_token = access_token2;
        }
        return access_token.getAccessToken();
    }

    public String submitFile(AccessToken access_token) throws Exception{
        File file_obj = new File(this.file); 
        String authorization = getAuthorization(access_token);
//        Proxy webproxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.labs.localsite.sophos", 8080));
//        OkHttpClient client = new OkHttpClient.Builder().proxy(webproxy).build();
        OkHttpClient client = new OkHttpClient.Builder().build();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", "filename",
            RequestBody.create(MediaType.parse("application/octet-stream"), file_obj)).build();

        Request request = new Request.Builder().header("Authorization", authorization).url(this.url).post(requestBody).build();

        try (Response response = client.newCall(request).execute()){
            if(!response.isSuccessful()) return "NA";
            return (response.body().string());
        }

    }

}