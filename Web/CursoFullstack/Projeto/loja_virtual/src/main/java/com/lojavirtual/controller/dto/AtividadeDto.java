package com.lojavirtual.controller.dto;

import java.io.Serializable;

public class AtividadeDto implements Serializable{
	private String text;
	
	private String code;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
}
