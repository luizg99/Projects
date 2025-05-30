package com.lojavirtual.model.dto.MelhorEnvio.envioEtiqueta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OptionsEnvioEtiquetaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String insurance_value;

    private boolean receipt;
    private boolean own_hand;
    private boolean reverse;
    private boolean non_commercial;

    private InvoiceEnvioEtiquetaDTO invoice = new InvoiceEnvioEtiquetaDTO();

    private String platform;

    private List<TagsEnvioEtiquetaDto> tags = new ArrayList<>();

    public String getInsurance_value() {
        return insurance_value;
    }

    public void setInsurance_value(String insurance_value) {
        this.insurance_value = insurance_value;
    }

    public boolean isReceipt() {
        return receipt;
    }

    public void setReceipt(boolean receipt) {
        this.receipt = receipt;
    }

    public boolean isOwn_hand() {
        return own_hand;
    }

    public void setOwn_hand(boolean own_hand) {
        this.own_hand = own_hand;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public boolean isNon_commercial() {
        return non_commercial;
    }

    public void setNon_commercial(boolean non_commercial) {
        this.non_commercial = non_commercial;
    }

    public InvoiceEnvioEtiquetaDTO getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceEnvioEtiquetaDTO invoice) {
        this.invoice = invoice;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<TagsEnvioEtiquetaDto> getTags() {
        return tags;
    }

    public void setTags(List<TagsEnvioEtiquetaDto> tags) {
        this.tags = tags;
    }
}