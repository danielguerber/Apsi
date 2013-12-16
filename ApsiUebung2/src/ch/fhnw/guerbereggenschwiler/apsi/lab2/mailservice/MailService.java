	package ch.fhnw.guerbereggenschwiler.apsi.lab2.mailservice;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.istack.internal.NotNull;
 
/**
 * @author Daniel Guerber & Stefan Eggenschwiler
 * This class provides methods to send emails.
 */
public final class MailService {
	/**
	 * No instance of this class should be created.
	 */
	private MailService() {}
	
	//Credentials for the mail account
	private final static String USERNAME = "rattlebits2013";
	private final static String PASSWORD = "ApsiLab02";
	
	/**
	 * Sends the mail after registration.
	 * @param data {Username, Password, Name, Address, ZipCode, City, E-Mail}
	 */
	public static void sendRegistrationMail(@NotNull String[] data) {
		if(data.length < 7)
			throw new InternalError("Data field to small!");
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
					InternetAddress.parse(data[6]));
			message.setSubject("Your temporary username and password");
			message.setText("Dear "+ data[2] + ","
					+"\n\nThank you for your registration on RattleBits. Your registration data are:\n"
					+"\nCompany: " + data[2]
					+"\nAddress: " + data[3]
					+"\nZipCode: " + data[4]
					+"\nCity: " + data[5]
					+"\n\nYou can login with this data:"
					+ "\nUsername: " + data[0] + "\nPassword: " + data[1]
					+"\n\nPlease change your password after your first login.\n\nRattleBits AG");
 
			Transport.send(message); 
		} catch (MessagingException e) {
			System.err.println(e.getMessage());
		}
	}
}