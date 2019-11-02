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
		">>>Parametros de firma para recibos<<< "+
		"\n-Tipo firma: "+tipo_firma+// 1=firma unitaria y 2=firma masiva
		"\n-Firmante: "+firmante+ //número de ci del firmante
		"\n-Estado recibo: "+estado_recibo+ // 1=firma empresa y 2=firma empleado
		"\n-ID recibo: "+id_recibo+ // identificador del recibo
		"\n-Contraseña: "+pass); // contraseña de firma
		
		//la variable respuesta sirve para saber si existen errores en los parametros de firma, si no hay errores se mantiene en "ok"
		String respuesta="ok";
		
		//Control de error en parametro tipo de firma (error posible solo para desarrollador)
		if (ControlError.ControlTipoFirma(tipo_firma) != "ok")
		{
			return ControlError.ControlTipoFirma(tipo_firma);
		}
		//Control de error en parametro estado recibo (error posible solo para desarrollador)
		if (ControlError.ControlEstadoRecibo(estado_recibo) != "ok")
		{
			return ControlError.ControlEstadoRecibo(estado_recibo);
		}
		//Control existencia archivo de firma .p12 en el Keystore
		if (ControlError.ControlKeyStore(firmante) != "ok")
		{
			return ControlError.ControlKeyStore(firmante);
		}
		
		//Si tipo firma es igual a 1 se realiza firma unitaria
		if(tipo_firma == 1)
		{
			
			try//Se intenta crear la firma del recibo
			{
				//respuesta = ControlError.ControlKeyStore(firmante);
				LogicaFirma.FirmaDigital(firmante, estado_recibo, id_recibo, pass);
				System.out.println("\n Se firmo el siguiente recibo: "+id_recibo);
				return respuesta;
			}catch (IOException | DocumentException | GeneralSecurityException e1)
			{
				System.out.println("\n getMessage: "+e1.getMessage());
				System.out.println("\n getCause: "+e1.getCause());
				e1.printStackTrace();	
				String MensajError =e1.getMessage().toString();
				if(MensajError == "keystore password was incorrect") //Se verifica si hay error en la contraseña de firma
				{
					respuesta = "La contraseña ingresada es incorrecta, no se completo la firma";
					System.out.println("\n Contraseña de firma incorrecta!");
					return respuesta;
				}
			}
		}
		else//Si tipo firma es diferente de 1 se realiza firma masiva
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
					System.out.println("\n getMessage: "+e1.getMessage());
					System.out.println("\n getCause: "+e1.getCause());
					//e1.printStackTrace();	
					String MensajError =e1.getMessage().toString();
					if (i==0)//Solo se debe verificar si la contraseña es correcta la primera vez del ciclo
					{
						if(MensajError == "keystore password was incorrect") //Se verifica si hay error en la contraseña de firma
						{
							respuesta = "La contraseña ingresada es incorrecta, no se completo la firma";
							System.out.println("\n Contraseña de firma incorrecta!");
							return respuesta;
						}
					}
								
				}				
			}
			System.out.println("Se realizo firma masiva por el usuario con CI: "+firmante);
			return respuesta;
		}
		//este error solo es posible si no se realiza ningun retorno desde las condiciones anteriores
		return "Ocurrio un error en el serivicio de firma";
	}
}
