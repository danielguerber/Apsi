package ch.fhnw.guerbereggenschwiler.apsi.lab2.model;

import javax.annotation.Nonnull;

/**
 * @author Daniel Guerber & Stefan Eggenschwiler
 * This class provides useful methods for the application.
 */
public final class Utils {
	/**
	 * No instances should be crated.
	 */
	private Utils(){}
	
	/**
	 * Encodes possible dangerous characters in the String 
	 * for usage in a HTML page.
	 * @param s String to encode
	 * @return encoded string
	 */
	@Nonnull
	public static String encodeHTML(@Nonnull String s)
	{
	    StringBuffer out = new StringBuffer();
	    for(int i=0; i<s.length(); i++)
	    {
	        char c = s.charAt(i);
	        if(c > 127 || c=='"' || c=='<' || c=='>')
	        {
	           out.append("&#"+(int)c+";");
	        }
	        else
	        {
	            out.append(c);
	        }
	    }
	    return out.toString();
	}
}
