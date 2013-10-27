package algorithm;

import java.util.Arrays;

import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.params.DESParameters;



public class HashAlgorithm {
	public static byte[] calculateHash(byte[] message) {
		DESEngine engine = new DESEngine();
		byte[] g = {0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55};
		
		for (int i = 0; i < message.length; i+=8) {
			byte[] m;
			
			if (i+8 < message.length) {
				m = Arrays.copyOfRange(message, i, i+8);
			} else {
				m = new byte[64];
				System.arraycopy(message, i, m, 0, message.length - i);
				m[message.length - i] = 8;
			}
			engine.init(true, new DESParameters(g));
			byte[] tmp = new byte[8];
			engine.processBlock(m,0,tmp,0);
			for (int j = 0; j < g.length; j++) {
				g[j] = (byte)(g[j] ^ tmp[j]);
			}
		}
		
		byte[] result = new byte[4];
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte)(g[i] ^ g[7-i]);
		}
		return result;
	}
	
	public static void main(String[] args) {
		String test = "this is a hash test.";
		String test2 = "this is a hash test too.";
		
		byte[] hash1 = calculateHash(test.getBytes());
		byte[] hash2 = calculateHash(test2.getBytes());
		
		System.out.println(new String(hash1));
		System.out.println(new String(hash2));
	}
}
