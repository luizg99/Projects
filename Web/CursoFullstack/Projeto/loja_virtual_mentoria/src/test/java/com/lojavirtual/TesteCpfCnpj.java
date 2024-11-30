package com.lojavirtual;

import com.lojavirtual.util.ValidaCNPJ;
import com.lojavirtual.util.ValidaCPF;

public class TesteCpfCnpj {

	public static void main(String[] args) {
		boolean isCnpj = ValidaCNPJ.isCNPJ("68.737.526/0001-92");
		System.out.println(isCnpj);
		
		boolean isCpf = ValidaCPF.isCPF("06221179122");
		System.out.println(isCpf);
		
	}
	
}
