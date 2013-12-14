package ch.fhnw.guerbereggenschwiler.apsi.lab2.mailservice;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class MailService {
	
	private final static String USERNAME = "rattlebits2013";
	private final static String PASSWORD = "ApsiLab02";
	
	public final void SendMail(String to, String company, String username, String password) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
 
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(USERNAME,PASSWORD);
				}
			});
 
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("rattlebits2013@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject("Your temporary username and password");
			message.setText("Dear "+ company + ","
					+"\n\nThank you for your registration on RattleBits.\n\nUsername: "+username+"\nPassword: "+password
					+"\n\nPlease change your password after your first login.\n\nRattleBits AG");
 
			Transport.send(message); 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}