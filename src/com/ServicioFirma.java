package com;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import com.itextpdf.text.DocumentException;

public class ServicioFirma 
{
	public	String func(int tipo_firma, int firmante, int estado_recibo, String id_recibo, String pass) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, DocumentException, GeneralSecurityException, InvocationTargetException
	{		
		System.out.println(
		"Tipo firma: "+tipo_firma +
		"Firmante: "+firmante+
		"Estado recibo: "+estado_recibo+
		"ID recibo: "+id_recibo+
		"Pin: "+pass);
		if(tipo_firma == 1)
		{
			try
			{
				LogicaFirma.FirmaDigital(firmante, estado_recibo, id_recibo, pass);
			}catch (IOException | DocumentException | GeneralSecurityException e1)
			{
				e1.printStackTrace();			
			}	
			return "Se realizo firma unitaria por usuario con CI: "+firmante+" ID Recibo: "+id_recibo;
		}
		else
		{
			String[] resultado = id_recibo.split(",");
			
			for (int i=0; i<resultado.length;i++)
			{
				try
				{
					LogicaFirma.FirmaDigital(firmante, estado_recibo, resultado[i], pass);
					System.out.println("Se firmo el siguiente recibo: "+resultado[i]);
				}catch (IOException | DocumentException | GeneralSecurityException e1)
				{
					e1.printStackTrace();			
				}				
			}
			//System.out.println(id_recibo);
			System.out.println("Se realizo firma masiva");
			
			/*
			String sha1 = "";
			String value ="1111";
			
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-1");
		        digest.reset();
		        digest.update(value.getBytes("utf8"));
		        sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
			} catch (Exception e){
				e.printStackTrace();
			}
			System.out.println( sha1 );
			*/
			return "Se realizo firma masiva";
		}
	}
}
