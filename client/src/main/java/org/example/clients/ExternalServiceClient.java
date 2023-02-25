package org.example.clients;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public record ExternalServiceClient(String baseUrl, OkHttpClient okHttpClient)  {

    public Integer getServiceStatus() {
        Request request = new Request.Builder()
                .get()
                .url(baseUrl)
                .build();

        Call call = okHttpClient.newCall(request);

        try(Response response = call.execute()) {
            return response.code();
        } catch (IOException e) {
            throw new ExternalServiceException(e);
        }
    }

}
