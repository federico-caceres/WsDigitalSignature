package com;

import java.io.File;

public class ControlError 
{
	public static void ControlKeyStore(int estado_recibo, String id_recibo)
	{
		File dir;
		if(estado_recibo == 1)
        {
        	int var = id_recibo.indexOf("-")+1;        	
        	dir = new File("C:/xampp/htdocs/sgfrs/public/recibos/pendientes/20"+id_recibo.substring(var+2,var+4)+"/"+id_recibo.substring(var,var+2)+"/");
        }else
        {
        	int var = id_recibo.indexOf("-")+1;        	
        	dir = new File("C:/xampp/htdocs/sgfrs/public/recibos/firmados_empresa/20"+id_recibo.substring(var+2,var+4)+"/"+id_recibo.substring(var,var+2)+"/");
            
        }
		//File dir = new File("C:/xampp/htdocs/sgfrs/public/recibos/pendientes/2019/01");
		String[] ficheros = dir.list();
		
		if (ficheros == null)
		{
			System.out.println("El directorio no existe");
		}else
		{
			for (int x=0; x<ficheros.length;x++)
			{
				System.out.println(ficheros[x]);
			}
		}
	}
}
