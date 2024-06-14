package com.example.demo;

import util.ValidaCNPJ;
import util.ValidaCPF;

public class TesteCpfCnpj {

	public static void main(String[] args) {
		boolean isCnpj = ValidaCNPJ.isCNPJ("34.638.348/0001-20");
		
		System.out.println(isCnpj);
		
		
		boolean isCpf = ValidaCPF.isCPF("70459580132");
		if (isCpf) {
			System.out.println(ValidaCPF.imprimeCPF("70459580132"));
		}
		else {
			System.out.println("CPF FALSO");
		}
		
		
		
		
	}
}
