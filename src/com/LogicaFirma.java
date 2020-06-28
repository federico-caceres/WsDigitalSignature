package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CertificateUtil;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOnline;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import java.util.Date;

public class LogicaFirma 
{
	
    public static void FirmaDigital(int firmante, int estado_recibo, String id_recibo, String pass) throws IOException, DocumentException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, GeneralSecurityException 
    {
    	//variables para guardar la ubicación del directorio de origen y destino del recibo firmado
    	String origenrecibo = "";
    	String destinorecibo = "";

    	//certificado en formato p12 o pfx (debe contener llave privada, publica y certificado)
        String ubicacionp12 = "C:/xampp/htdocs/sgfrs/KeyStore/";
        File fContenedorp12 = new File(ubicacionp12,firmante+".p12");
        
        //Si estado recibo es igual 1 la firma lo esta realizando la empresa
        if (estado_recibo == 1)
        {
        	int var = id_recibo.indexOf("-")+1;        	
        	//ubicacion del recibo que se va firmar
        	origenrecibo = "C:/xampp/htdocs/sgfrs/public/recibos/pendientes/20"+id_recibo.substring(var+2,var+4)+"/"+id_recibo.substring(var,var+2)+"/"+id_recibo+".pdf";
            //ubicacion destino del recibo firmado
        	destinorecibo = "C:/xampp/htdocs/sgfrs/public/recibos/firmados_empresa/20"+id_recibo.substring(var+2,var+4)+"/"+id_recibo.substring(var,var+2)+"/"+id_recibo+".pdf";
        }
        else //Si estado recibo es diferente de 1 la firma lo esta realizando la empresa
        {     
            //variable var guarda posición del caracter "-" para utilizar funcion subtring para identificar año y mes del recibo
        	int var = id_recibo.indexOf("-")+1;        	
        	//ubicacion del recibo que se va firmar
        	origenrecibo = "C:/xampp/htdocs/sgfrs/public/recibos/firmados_empresa/20"+id_recibo.substring(var+2,var+4)+"/"+id_recibo.substring(var,var+2)+"/"+id_recibo+".pdf";
            //ubicacion destino del recibo firmado
        	destinorecibo = "C:/xampp/htdocs/sgfrs/public/recibos/firmados_empresa_empleados/20"+id_recibo.substring(var+2,var+4)+"/"+id_recibo.substring(var,var+2)+"/"+id_recibo+".pdf";
        }
        
        //Se agrega bouncyCastle al provider de java, si no se realiza, arroja un error
        Provider p = new BouncyCastleProvider();
        Security.addProvider(p);       
        
        //Se instancia un keystore de tipo pkcs12 para leer el contenedor p12 o pfx
        KeyStore ks = KeyStore.getInstance("pkcs12");
        
        //Se entrega la ruta y la clave del p12 o pfx
        ks.load(new FileInputStream(fContenedorp12.getAbsolutePath()), pass.toCharArray());
        //System.out.println(fContenedorp12.getAbsolutePath());
        
        //Se obtiene el nombre del certificado
        String alias = (String)ks.aliases().nextElement();
        
        //Se obtiene la llave privada
        PrivateKey pk = (PrivateKey)ks.getKey(alias, pass.toCharArray());
        
        //Se obtiene la cadena de certificados en base al nombre del certificado
        Certificate[] chain = ks.getCertificateChain(alias);
        
        //Impresion de datos del certificado del firmante
        for (int i = 0; i < chain.length; i++) 
        {
    	X509Certificate cert = (X509Certificate)chain[i];
    	System.out.println(String.format("[%s] %s", i, cert.getSubjectDN()));
    	System.out.println(CertificateUtil.getCRLURL(cert));
    	}
        
        //Se obtiene las CRL del certificado raiz e intermedio y se agrega para agregar al documento firmado
        List<CrlClient> crlList = new ArrayList<CrlClient>();
        crlList.add(new CrlClientOnline(chain));
        
        //Control verificacion de revocacion del ceritificado
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL)cf.generateCRL(new FileInputStream("C:/xampp/htdocs/sgfrs/public/crl/intermedia.crl"));
        if(crl.isRevoked(chain[0]) == true)
		{
        	System.out.println("\nCertificado revocado \n");
		}
		else
		{
			System.out.println("\nCertificado de usuario valido \n");
		}
      //Control verificacion de fecha de la CRL obtenida
        Date FechaActual = new Date();
        Date FechaActualizacionCRL = crl.getNextUpdate();    
        //System.out.println("CRL valid until: " + FechaActualizacionCRL);
        //System.out.println("Certificate revoked: " + crl.isRevoked(chain[0]));
        if(FechaActualizacionCRL.compareTo(FechaActual) < 0)//menor a 0 fecha actualizacion es anterior a fecha actual
        {
        	System.out.println("\nError: Fecha de actualización de CRL anterior a la fecha actual \n");
        }
		else
		{
			System.out.println("\nFecha de actualización de CRL correcta\n");
		}
        
		//Se indica el origen del pdf a firmar
        PdfReader reader = new PdfReader(origenrecibo);
        
        //Se indica el destino del pdf firmado
        PdfStamper stamper = PdfStamper.createSignature(reader, new FileOutputStream(destinorecibo), '\0', null, true);
        
        //Se indican alguno detalles de la forma en que se firmara
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        if (estado_recibo == 1)//Si estado recibo es igual a 1 el firmante es la empresa
        {
        	appearance.setReason("Firma de parte de la empresa por la aceptación del contenido del recibo de salario del empleado");
        	appearance.setVisibleSignature(new Rectangle(50,260,200,300),1,null);
        }else if (estado_recibo == 2)//Si estado recibo es igual a 2 el firmante es el empleado
        {
        	appearance.setReason("Firma de parte de la empleado por la aceptación del contenido de su recibo de salario entregado por la empresa");
        	appearance.setVisibleSignature(new Rectangle(600,260,750,300),1,null);
        }
        
        // Se entrega la llave privada del certificado, el algoritmo de firma y el provider usado (bouncycastle)
        ExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
        ExternalDigest digest = new BouncyCastleDigest();

        //Se genera la firma y se almacena el pdf como se indico en las lineas anteriores
        MakeSignature.signDetached(appearance, digest, es, chain,crlList, null, null,0, CryptoStandard.CMS);
        
        //Se cierran las instancias para liberar espacio
        stamper.close();
        reader.close();
        
        //creación objeto recibo, este objeto se utilizara para eliminar el recibo origen
        File recibo = new File(origenrecibo);
        recibo.delete();
    }
}
