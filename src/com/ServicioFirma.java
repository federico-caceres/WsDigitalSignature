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
		">>>Parametros de firma recibos<<<"+
		"-Tipo firma: "+tipo_firma+// 1=firma unitaria y 2=firma masiva
		"-Firmante: "+firmante+ //número de ci del firmante
		"-Estado recibo: "+estado_recibo+ // 1=firma empresa y 2=firma empleado
		"-ID recibo: "+id_recibo+ // identificador del recibo
		"-Pin: "+pass); // contraseña de firma
		
		//Control de error en parametro estado recibo
		if(estado_recibo != 1 && estado_recibo != 2)
		{
			System.out.println("Error: El parametro de estado de recibo es diferente de 1 (firma empresa) o 2 (firma empleado)");
			return "Error: El parametro de estado de recibo es diferente de 1 (firma empresa) o 2 (firma empleado)";
		}
		
		if(tipo_firma == 1)//Si tipo firma es igual a 1 se realiza firma unitaria
		{
			try//Se intenta crear la firma del recibo
			{
				LogicaFirma.FirmaDigital(firmante, estado_recibo, id_recibo, pass);
				System.out.println("Se firmo el siguiente recibo: "+id_recibo);
			}catch (IOException | DocumentException | GeneralSecurityException e1)
			{
				e1.printStackTrace();			
			}	
			return "Se realizo firma unitaria por el usuario con CI: "+firmante+" ID Recibo: "+id_recibo;
		}
		else if (tipo_firma == 2)//Si tipo firma es igual a 2 se realiza firma masiva
		{
			String[] resultado = id_recibo.split(",");//se separa los ids de recibos en un vector
			
			for (int i=0; i<resultado.length;i++)//se recorre el vector y por cada id de recibo se ejecura la firma
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
			System.out.println("Se realizo firma masiva");
			return "Se realizo firma masiva por el usuario con CI: "+firmante;
		}else
		{
			System.out.println("Error: El parametro de tipo de firma es diferente de 1 (unitaria) o 2 (masiva)");
			return"Error: El parametro de tipo de firma es diferente de 1 (unitaria) o 2 (masiva)";
		}
	}
}
