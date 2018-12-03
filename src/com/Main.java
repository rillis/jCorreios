package com;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Main {
	public static void main(String args[]) {
		
		Object[] passos = getEntrega("OG004330991BR");
		
		for (int i = 0; i < passos.length; i++) {
			System.out.println(passos[i]);
		}
		
	}
	
	public static Object[] getEntrega(String cod) {
		try {
			
			String url = "https://www2.correios.com.br/sistemas/rastreamento/resultado_semcontent.cfm?";
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "Objetos="+cod;
			
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String a = response.toString();
			
			if(a.contains("Não é possível exibir")) {
				return new String[]{"Código não encontrado!"};
				
			}else {
				String b = a.split("<table class=\"listEvent sro\">")[1];
				String c = b.split("</table>")[0].replaceAll("\\s+", " ");
				
				String infos[] = c.split("<tr> ");
				int n=infos.length-1;
				String[] infosb=new String[n];
				System.arraycopy(infos,1,infosb,0,n);
				ArrayList<String> informacoes = new ArrayList<String>();
				for (int i = 0; i < infosb.length; i++) {
					String temp = "Data:";
					
					
					infosb[i].replace(" </tr>", "");
					
					String data = infosb[i].replaceAll("<td class=\"sroDtEvent\" valign=\"top\"> ", "").split(" <br />")[0];
					
					temp += data + "|Hora:";
					
					String hora = infosb[i].split(" <br /> ")[1].split(" <br /> ")[0];
					
					temp += hora + "|Local:";
					
					String r = infosb[i].split(" <br /> ")[2];
					
					if(r.contains("<label style=\"text-transform:")) {
						String local = r.replaceAll("<label style=\"text-transform:capitalize;\">", "").split("</label>")[0].replace("&nbsp;/&nbsp;", " / ");
						temp += local + "|Mensagem:";
						
					}else {
						String local = r.split("<br /> ")[0];
						temp += local + "|Mensagem:";
					}
					
					if(r.contains("strong")) {
						String msg = r.split("<strong>")[1].split("</strong>")[0];
						temp += msg;
					}else {
						String msg = r.split("<td class=\"sroLbEvent\"> ")[1].replaceAll("<br /> ", ". ");
						temp += msg;
					}
					
					informacoes.add(temp);
					
				}
				return informacoes.toArray();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
