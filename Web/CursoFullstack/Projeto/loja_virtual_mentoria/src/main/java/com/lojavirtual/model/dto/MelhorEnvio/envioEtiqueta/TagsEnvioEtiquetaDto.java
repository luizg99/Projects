package com.lojavirtual.model.dto.MelhorEnvio.envioEtiqueta;

import java.io.Serializable;

public class TagsEnvioEtiquetaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tag;
    private String url;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}