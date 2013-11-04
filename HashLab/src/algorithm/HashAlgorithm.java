package algorithm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.params.DESParameters;


/**
 * @author Stefan Eggenschwiler & Daniel Guerber
 * Implementation of the specified hash algorithm
 */
public class HashAlgorithm {
	/**
	 * Specified initial vector
	 */
	private final static byte[] IV = {0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55};
	
	/**
	 * Returns a new byte array with 0b1.... padding and 64 bit message length
	 * @param message message to hash
	 * @return padded message
	 */
	private static byte[] addPaddingAndLength(byte[] message) {
		int paddingLength = 8 - (message.length % 8);
		
		byte[] newMessage = new byte[message.length + paddingLength + 8];
		
		System.arraycopy(message, 0, newMessage, 0, message.length);
		
		if (message.length % 8 > 0) {
			newMessage[message.length] = -128;
		}
		
		System.arraycopy(ByteBuffer.allocate(8).putLong(message.length).array(), 0, newMessage, newMessage.length - 8, 8);
	
		return newMessage;
	}
	
	/**
	 * Calculates the hash with the specified algorithm
	 * @param message Message to hash
	 * @return hash
	 */
	public static byte[] calculateHash(byte[] message) {
		DESEngine engine = new DESEngine();
		
		byte[] newMessage = addPaddingAndLength(message);
		
		byte[] prevHash = IV;
		
		//calculate H
		for (int i = 0; i < newMessage.length; i+=8) {
			byte[] m = Arrays.copyOfRange(newMessage, i, i+8);
			
			engine.init(true, new DESParameters(prevHash));
			byte[] tmp = new byte[8];
			engine.processBlock(m,0,tmp,0);
			
			for (int j = 0; j < prevHash.length; j++) {
				prevHash[j] = (byte)(prevHash[j] ^ tmp[j]);
			}
		}
		
		ByteBuffer buffer = ByteBuffer.wrap(prevHash);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        long iPrevHash = buffer.getLong();
        
        int iResult = ((int)iPrevHash) ^ ((int)Long.reverse(iPrevHash));
		
		return ByteBuffer.allocate(4).putInt(iResult).array();
	}
}
