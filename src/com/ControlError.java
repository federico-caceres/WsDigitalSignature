package com;

import java.io.File;

public class ControlError 
{
	public static String ControlKeyStore(int firmante)
	{
		File archivo = new File("C:/eclipse/KeyStore/"+firmante+".p12");
		String resultado;
		if (!archivo.exists()) 
		{
			resultado ="Error: No se encontro la firma digital del usuario en el servidor, contacte con el Administrador del Sistema";
		    System.out.println("No existe el archivo .p12 en el keystore");
		}else
		{
			System.out.println("Archivo .p12 correcto \n");
			resultado ="ok";
		}
		return resultado;
	}
	public static String ControlEstadoRecibo(int estado_recibo)
	{
		String resultado;
		if(estado_recibo != 1 && estado_recibo != 2)
		{
			System.out.println("Error: El parametro de estado de recibo es diferente de 1 (firma empresa) o 2 (firma empleado)");
			resultado= "Error: El parametro de estado de recibo es diferente de 1 (firma empresa) o 2 (firma empleado)";
		}
		else
		{
			System.out.println("Estado recibo correcto \n");
			resultado ="ok";
		}
		return resultado;
	}
	public static String ControlTipoFirma(int tipo_firma)
	{
		String resultado;
		if(tipo_firma != 1 && tipo_firma != 2)
		{
			System.out.println("\n Error: El parametro de tipo firma es diferente de 1 (firma unitaria) o 2 (firma masiva)");
			resultado= "Error: El parametro de tipo firma es diferente de 1 (firma unitaria) o 2 (firma masiva)";
		}
		else
		{
			System.out.println("\nTipo de firma correcto \n");
			resultado ="ok";
		}
		return resultado;
	}
}
