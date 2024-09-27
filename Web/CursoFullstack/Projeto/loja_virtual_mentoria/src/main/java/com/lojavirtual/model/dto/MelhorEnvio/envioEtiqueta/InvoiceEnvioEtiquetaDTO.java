package com.lojavirtual.model.dto.MelhorEnvio.envioEtiqueta;

import java.io.Serializable;

public class InvoiceEnvioEtiquetaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}