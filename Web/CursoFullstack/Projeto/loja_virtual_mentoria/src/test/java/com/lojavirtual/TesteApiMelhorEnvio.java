package com.lojavirtual;

import java.io.IOException;

import com.lojavirtual.security.ApiTokenIntegracao;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TesteApiMelhorEnvio {
	
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"options\":{\"receipt\":true,\"own_hand\":true,\"reverse\":true,\"non_commercial\":true}}");
        Request request = new Request.Builder()
          .url("https://sandbox.melhorenvio.com.br/api/v2/me/cart")
          .post(body)
          .addHeader("Accept", "application/json")
          .addHeader("Content-Type", "application/json")
          .addHeader("Authorization", "Bearer token" + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BX)
          .addHeader("User-Agent", "luizfasam@gmail.com")
          .build();

        Response response = client.newCall(request).execute();
    	
        System.out.print(response.body().string());
    }
    
}
