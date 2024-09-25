package com.lojavirtual;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;

import com.lojavirtual.security.ApiTokenIntegracao;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TesteApiMelhorEnvio {
	
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX + "api/v2/me/shipment/companies")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("User-Agent", "luizfasam@gmail.com")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());

    }
	
}
