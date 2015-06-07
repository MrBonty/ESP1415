package it.unipd.dei.esp1415.falldetector.mail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.util.Base64;
import java.util.Calendar;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MailSender {

	private int port;
	private String server;
	
	private String user;
	private String password;
	
	private SSLSocket socket;
	private BufferedReader br;
	private DataOutputStream out;
	private InputStream in;
	private Thread reader;
	
	private String response;
	
	
	private static final long DELAY = 500;
	private static final long DELAY_LONG = 1000;
	
	public MailSender(int port, String server, String user, String password, boolean toBase64){
		this.port = port;
		this.server = server;
		if(toBase64){
			this.user = Base64.encodeToString(user.getBytes(), Base64.DEFAULT);
			this.password = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
		}else {
			this.user = user;
			this.password = password;
		}
	}

	
	public boolean connect(){
		try {
			
			socket = (SSLSocket)((SSLSocketFactory)SSLSocketFactory.getDefault()).createSocket(server, port);
			
			in = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			
			out = new DataOutputStream(socket.getOutputStream());
			
			reader = new Thread(new Runnable(){
				public void run(){
					try{
						String line;
						while((line = br.readLine()) != null)
							response += line+"\n";
					}
					catch (IOException e){
						e.printStackTrace();
					}
				}
	        });
			
			reader.start();
			
			try {
				
				Thread.sleep(DELAY);
				System.out.println(response);

				smtp("EHLO smtp.gmail.com\r\n");
				Thread.sleep(DELAY);
				//TODO verify response and smtp
				System.out.println(response);

				smtp("AUTH LOGIN\r\n");
				Thread.sleep(DELAY);
				//TODO verify response and smtp
				System.out.println(response);

				smtp(user+"\r\n");
				Thread.sleep(DELAY);
				//TODO verify response and smtp
				System.out.println(response);

				smtp(password+"\r\n");
				Thread.sleep(DELAY);
				//TODO verify response and smtp
				System.out.println(response);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		return false;
	}
	
	public void close(){
		try {		
			smtp("QUIT\r\n");
		    Thread.sleep(DELAY_LONG);
			//TODO verify response and smtp
			System.out.println(response);
			
			reader.interrupt();
			
			br.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public boolean send(Mail mail){	
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dt = new SimpleDateFormat("dd MMM yy HH:mm:ss");
		String date = dt.format(cal.getTime());

		
		try {
			smtp("MAIL FROM:<"+ mail.getSender() + ">\r\n");
			Thread.sleep(DELAY);
			//TODO verify response and smtp
			System.out.println(response);

			if(mail.hasMore()){
				ArrayList<String> receivers = mail.getReceivers();
				for (int i = 0; i<receivers.size(); i++){
					smtp("RCPT TO:<" + receivers.get(i) + ">\r\n");
					Thread.sleep(DELAY);
					//TODO verify response and smtp
					System.out.println(response);
				}
			}else{
				smtp("RCPT TO:<" + mail.getReceiver() + ">\r\n");
				Thread.sleep(DELAY);
				//TODO verify response and smtp
				System.out.println(response);
			}
			
			smtp("DATA" + "\r\n");
			Thread.sleep(DELAY);
			//TODO verify response and smtp
			System.out.println(response);
			
			smtp("Date: " + date + ">\r\n");
			//Thread.sleep(delay);
			//TODO verify response and smtp
			//System.out.println(response);
			
			smtp("From: <" + mail.getSender() + ">\r\n");
			//Thread.sleep(delay);
			//TODO verify response and smtp
			//System.out.println(response);
			if(mail.hasMore()){
				ArrayList<String> receivers = mail.getReceivers();
				for (int i = 0; i<receivers.size(); i++){
					smtp("To: <" + receivers.get(i) + ">\r\n");
					//Thread.sleep(delay);
					//TODO verify response and smtp
					//System.out.println(response);
				}
			}else{
				smtp("To: <" + mail.getReceiver() + ">\r\n");
				//Thread.sleep(delay);
				//TODO verify response and smtp
				//System.out.println(response);
			}
			smtp("Subject: " + mail.getSubject() + "\r\n");
			//Thread.sleep(delay);
			//TODO verify response and smtp
			//System.out.println(response);

			smtp(mail.getMessage() + "\r\n");
			//Thread.sleep(delay);
			//TODO verify response and smtp
			//System.out.println(response);

			smtp("\r\n.\r\n");
			Thread.sleep(DELAY);
			//TODO verify response and smtp
			System.out.println(response);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean smtp(String command) {
		try {
			out.writeBytes(command);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/*
	private void read(){
		response = "";
		int n;
		
		byte[] b = new byte[1024];
		
		try {
			while ((n = in.available())> 0){
				
				for(int i = 0; i<n; i++){
					byte c= (byte) br.read();
					b[i] = c;
				}
				response += new String(b, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
}
