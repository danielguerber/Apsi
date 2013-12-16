package ch.fhnw.guerbereggenschwiler.apsi.lab2.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import ch.fhnw.guerbereggenschwiler.apsi.lab2.mailservice.MailService;

import com.sun.istack.internal.NotNull;

/**
 * @author Daniel Guerber & Stefan Eggenschwiler
 * This class stores company information and provides functionality for companies.
 */
public class Company {
	@CheckForNull
	private final String username;
	@CheckForNull
	private final String name;
	@CheckForNull
	private final String address;
	private final int zip;
	@CheckForNull
	private final String town;
	@CheckForNull
	private final String mail;
	
	
	/**
	 * Creates an instance of the Company class.
	 * @param username Username of the company
	 * @param name Name of the company
	 * @param address Address of the company
	 * @param zip Zip code of the company
	 * @param town Town where the company is located
	 * @param mail Email address of the company
	 */
	public Company(@CheckForNull String username, 
			@CheckForNull String name, @CheckForNull String address, 
			int zip, @CheckForNull String town, @CheckForNull String mail) {
		super();
		this.username = username;
		this.name = name;
		this.address = address;
		this.zip = zip;
		this.town = town;
		this.mail = mail;
	}

	/**
	 * Gets the username of the company.
	 * @return username
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getUsername() {
		return username;
	}
	
	/**
	 * Gets the name of the company.
	 * @return name
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getName() {
		return name;
	}

	/**
	 * Gets the address of the company.
	 * @return address
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getAddress() {
		return address;
	}

	/**
	 * Gets the zip code of the company.
	 * @return zip code
	 */
	@CheckForNull 
	@CheckReturnValue
	public final int getZip() {
		return zip;
	}

	/**
	 * Gets the town of the company.
	 * @return town
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getTown() {
		return town;
	}

	/**
	 * Gets the email address of the company.
	 * @return email address
	 */
	@CheckForNull 
	@CheckReturnValue
	public final String getMail() {
		return mail;
	}

	/**
	 * Checks if login data is correct and loads the user data.
	 * @param user Username for the company to log in
	 * @param password Password for the company to log in
	 * @return Data of the company
	 * @throws SQLException thrown if problems with database occur
	 */
	public static Company checkLogin(@CheckForNull String user,
			@CheckForNull String password) throws SQLException {
		if (user == null || password == null) return null;
		try (Connection con = ConnectionHandler.getConnection()) {
		
			try (PreparedStatement stm = con.prepareStatement("SELECT  `username`, `name`, `address`, `zip`, `town`, `mail` FROM company WHERE username = ? AND password = ? ")) {
				stm.setString(1, user);
				stm.setString(2, hash(password));
				try (ResultSet rs = stm.executeQuery()) {
					if (rs.next()) {
						return new Company(
								rs.getString(1), 
								rs.getString(2),
								rs.getString(3),
								rs.getInt(4),
								rs.getString(5),
								rs.getString(6));
					}
					else return null;
				}
			}
		}
	}

	/**
	 * Validates the fields of the company.
	 * @return Error message of the validation
	 */
	@NotNull
	@CheckReturnValue
	public List<String> validate() {
		List<String> errors = new ArrayList<>();
				
	    if (name != null) {
	    	if (name.trim().isEmpty()) {
	    		errors.add("Firmenname eingeben.");
	    	} else if (name.trim().length() > 20) {
	    		errors.add("Firmenname zu lang (max. 20 Zeichen).");
	    	} else if (!name.matches("([èéÈÉäöüÄÖÜßa-zA-Z\\s]+)")) {
	    		errors.add("Ung&uuml;ltige Zeichen im Firmennamen");
	    	}
	    }
	    if (address != null) {
	    	if (address.trim().isEmpty()) {
	    		errors.add("Keine Adresse.");
	    	} else if (!address.matches("[èéÈÉäöüÄÖÜß\\w\\s\\.\\-]+")) {
	    		errors.add("Ung&uuml;ltige Adresse.");
	    	}
	    }
	    if (zip >= 1000 && zip <= 9999) {
	    	if(!validatePlz(zip))
	    		errors.add("Ung&uuml;ltige Postleitzahl.");
	    } else {
	    	errors.add("Ung&uuml;ltige Postleitzahl.");
        }
	    if (town != null) { 
	    	if (town.trim().isEmpty()) {
	    		errors.add("Keine Stadt.");
	    	} else if (!town.matches("[èéÈÉäöüÄÖÜßa-zA-Z\\-\\.\\s]+")) {
	    		errors.add("Ung&uuml;ltige Stadt.");
	    	}
	    }
	    // RFC-822 validation
	    if (mail != null && !mail.trim().isEmpty()) {
	        if (!mail.matches("(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)")) {
	            errors.add("Ung&uuml;ltige Email-Adresse.");
	        } else if(!mxLookup(mail)) {
	        	errors.add("Ung&uuml;ltige Email-Adresse.");
	        }
	    } else {
            errors.add("Keine Email-Adresse");
	    }
		return errors;
	}
	
	/**
	 * Saves the company to the database.
	 * @return reference to the company
	 * @throws SQLException thrown on database errors
	 */
	@NotNull
	public final Company save() throws SQLException {
		
		
		String username = createUsername();
		String password = createPassword();
		String[] data = new String[] {username, password, name, address, Integer.toString(zip), town, mail};

		try (Connection con = ConnectionHandler.getConnection()) {
			try (PreparedStatement stm = con.prepareStatement("INSERT INTO `company`(`username`, `password`, `name`, `address`, `zip`, `town`, `mail`) VALUES (?,?,?,?,?,?,?)")) {
				stm.setString(1, username);
				stm.setString(2, hash(password));
				stm.setString(3, name);
				stm.setString(4, address);
				stm.setInt(5, zip);
				stm.setString(6, town);
				stm.setString(7, mail);
				stm.execute();
				MailService.sendRegistrationMail(data);
				return this;
			}
		}
	}
	
	/**
	 * Changes the password for the company 
	 * if the credentials are correct.
	 * @param username Username of the company
	 * @param oldPassword old password of the company
	 * @param newPassword new password of the company
	 * @return true if password was changed
	 * @throws SQLException thrown on database errors
	 */
	public static final boolean changePassword(@CheckForNull String username,
			@CheckForNull String oldPassword, @CheckForNull String newPassword) throws SQLException {
		if (username == null || oldPassword == null|| newPassword == null) return false;
		
		try (Connection con = ConnectionHandler.getConnection()) {
			try (PreparedStatement stm = con.prepareStatement("UPDATE `company` SET `password`= ? WHERE `username`= ? AND `password` = ?")) {
			
				stm.setString(1, hash(newPassword));
				stm.setString(2, username);
				stm.setString(3, hash(oldPassword));
				
				stm.execute();
				return stm.getUpdateCount() > 0;
			}
		} 
	}
	
	/**
	 * Gets the SHA-256 hash of a String.
	 * @param s String to hash
	 * @return hash of the String
	 */
	@NotNull
	@CheckReturnValue
	private static String hash(@NotNull String s) {
		byte[] data = null;
		try {
			try {
				data = MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8"));
			} catch (NoSuchAlgorithmException e) {
				throw new AssertionError("SHA-256 not installed");
			}
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//JVM Violates standard
			throw new AssertionError("UTF-8 is not supported on this JVM");
		}
	}
	
	/**
	 * Checks if a Mailserver is registered for the specified
	 *  email adress.
	 * @param mail email adress to check
	 * @return true if mailserver is registered
	 */
	@CheckReturnValue
	private static final boolean mxLookup(@NotNull String mail) {
		String[] temp = mail.split("@");
		String hostname = temp[1];
		Hashtable<String, String> env = new Hashtable<String, String>();
		
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		try {
			DirContext ictx = new InitialDirContext(env);
			Attributes attrs = ictx.getAttributes(hostname, new String[] {"MX"});
			Attribute attr = attrs.get("MX");
			if (attr == null) {
				return false;
			} else {
				return true;
			}
		} catch (NamingException e) {
			return false;
		}
	}
	
	/**
	 * Validates the zip code with the post.ch service.
	 * @param zip zip to validate
	 * @return true if correct code
	 */
	@CheckReturnValue
	private static final boolean validatePlz(int zip) {
		URL url;
		HttpURLConnection conn;
		
		String line;
		try {
			url = new URL("http://www.post.ch/db/owa/pv_plz_pack/pr_check_data?p_language=de&p_nap="+zip+"&p_localita=&p_cantone=&p_tipo=luogo");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			String encoding = conn.getContentEncoding();
			InputStreamReader reader = new InputStreamReader(conn.getInputStream(), encoding == null ? "UTF-8" : encoding); 
			BufferedReader rd = new BufferedReader(reader);
			try {
				while ((line = rd.readLine()) != null) {
					if(line.contains("Keine PLZ gefunden"))
						return false;
				}
			} finally {
				rd.close();
			}
				
			
            return true;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	/**
	 * Validates the given password.
	 * @param pw password
	 * @return true if password is valid
	 */
	@CheckReturnValue
	public static final String validatePassword(@CheckForNull String pw) {
		String error = null;
		if (pw != null) {
			if (pw.trim().length() < 8) {
				error = "Passwort zu kurz (min. 8 Zeichen).";
			} else if (pw.trim().length() > 64) {
				error ="Passwort zu lang (max. 64 Zeichen).";
			} else if (!pw.matches("[èéÈÉäöüÄÖÜß\\-\\_\\.\\w]+")) {
	    		error = "Ungültige Zeichen im Passwort.";
	    	}
		} else {
			error = "Passwort zu kurz (min. 8 Zeichen).";
		}
		return error;
	}
	
	/**
	 * Generates a password with securerandom;
	 * @return new password
	 */
	@CheckReturnValue
	private static final String createPassword() {
		SecureRandom random = new SecureRandom();
	    return new BigInteger(130, random).toString(32);
	}
	
	/**
	 * Creates a new Username with the name of the company as a base.
	 * @return new username
	 * @throws SQLException thrown on database error
	 */
	@CheckReturnValue
	private final String createUsername() throws SQLException {
		String usernameBase = name != null ? name.replace(" ", "") : "";
		int tries = 0;
		
		String newUsername;
		boolean collision;
		try (Connection con = ConnectionHandler.getConnection()) {
			do {
				newUsername = usernameBase + (tries > 0 ? tries : "");
				tries++;
			
				try (PreparedStatement stm = con.prepareStatement("SELECT `username` FROM `company` WHERE `username` = ? ")) {
					stm.setString(1, newUsername);
					collision = stm.executeQuery().next();
				}
			} while (collision);
			
			return newUsername;
		}
	}
	
	/**
	 * Gets a FOrtuneCookie quote from the fullerdata.com webservice.
	 * @return new Quote
	 */
	@CheckReturnValue
	@NotNull
	public static final String getFortuneQuote() {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd = null;
		String line = null;
		StringBuilder quote = new StringBuilder("");
		try {
			url = new URL("http://www.fullerdata.com/FortuneCookie/FortuneCookie.asmx/GetFortuneCookie");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			String encoding = conn.getContentEncoding();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding == null ? "utf-8" : encoding));
			while ((line = rd.readLine()) != null) {
				if(!line.contains("<?xml") && !line.contains("<string") && !line.contains("string>")) {
					quote.append(line+"\n");
				}
			}
			rd.close();
		} catch (IOException e) {
			try {
				if (rd != null)
					rd.close();
			} catch (IOException e2) {
				System.err.println(e2.getMessage());
			}
			return "The quote is a lie! - Benjamin Franklin 1945";
		}
		return quote.toString();
	}
}