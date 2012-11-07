package app;

import gnu.io.NRSerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread;

public class Datos {

	NRSerialPort serialPort;
	private Boolean ejecutando;
	private InputStream is;
	private OutputStream os;
	private Boolean[] notas = {false,false,false,false,false,false,false,false};

	public void setEjecutando(Boolean ejecutando) {
		this.ejecutando = ejecutando;
	}
	
	static String reverseMe(String s) {
		   StringBuilder sb = new StringBuilder();
		   for(int i = s.length() - 1; i >= 0; --i)
		     sb.append(s.charAt(i));
		   return sb.toString();
		 }

	public void setNotas(String binaryString) {
		StringBuilder sb = new StringBuilder();
		String cadena = "";
		if (binaryString.length() < 8) {
			for (int i = 0; i < 8; i++) {
				if (i < 8 - binaryString.length())
					sb.append('0');
				else
					sb.append(binaryString.charAt(i - 8 + binaryString.length()));
			}
			cadena = reverseMe(sb.toString());
		}else{
			cadena = reverseMe(binaryString);
		}
		for (int i = 0; i < cadena.length(); i++) {
			if (cadena.charAt(i) == '0')
				notas[i] = false;
			else
				notas[i] = true;
		}
		System.out.println(cadena);
	}

	public Boolean[] getNotas() {
		return notas;
	}

	public Datos() {
		ejecutando = true;
	}

	public boolean abrirPuertoSerial(String puerto) {
		serialPort = new NRSerialPort(puerto, 9600);
		try {
			serialPort.connect();
			EsperarDatos ed = new EsperarDatos();
			is = serialPort.getInputStream();
			os = serialPort.getOutputStream();
			Thread edt = new Thread(ed,
					"Thread para leer los datos asíncronamente.");
			edt.start();
		} catch (Exception spe) {
			System.out.println(spe.getMessage());
			return false;
		}
		return true;
	}

	class EsperarDatos implements Runnable {

		@Override
		public void run() {

			while (ejecutando) {
				try {
					Thread.sleep(10);
					if (is.available() > 0) {
						String binaryS = Integer.toBinaryString(is.read());
						setNotas(binaryS);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ejecutando = false;
				}
			}
		}

	}

	public Boolean cerrarPuerto(){
		try{
			serialPort.disconnect();
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			return false;
		}
		return true;
	}

	public void escribirDatos(byte[] datos) {
		try {
			os.write(datos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
