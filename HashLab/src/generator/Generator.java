package generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import algorithm.HashAlgorithm;

public final class Generator {
	
	private final int maxCollisions;
	private final String fileOriginal;
	private final String templateOriginal;
	private final String templateCopy;
	private final String[][] replacements;
	private final boolean changeOriginal;
	
	public Generator(int maxCollisions, boolean changeOriginal) throws IOException {
		this.maxCollisions = maxCollisions;
		this.changeOriginal = changeOriginal;
		
		this.fileOriginal = readFile("OriginalFile.txt");
		this.templateOriginal = readFile("PlaceholderOriginal.txt");
		this.templateCopy = readFile("PlaceholderCopy.txt");
		
		replacements = readReplacements();
		
		searchCollisions();
	}
	
	private void searchCollisions() throws FileNotFoundException {
		HashMap<Integer, String> originalHashes = new HashMap<Integer, String>();
		HashMap<Integer, String> copyHashes = new HashMap<Integer, String>();
		
		int foundCollisions = 0;
		HashSet<Integer> collidedHashes = new HashSet<Integer>();
		
		ByteBuffer buffer = ByteBuffer.wrap(HashAlgorithm.calculateHash(fileOriginal.getBytes()));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int origHash = buffer.getInt();
		
		originalHashes.put(origHash, fileOriginal);
		
		while (foundCollisions < maxCollisions) {
			for (int i = 0; i < 2048; i++) {
				String randomCopy = String.format(templateCopy, getRandomReplacement());
				buffer = ByteBuffer.wrap(HashAlgorithm.calculateHash(randomCopy.getBytes()));
		        buffer.order(ByteOrder.LITTLE_ENDIAN);
		        int copyHash = buffer.getInt(); 

		        copyHashes.put(copyHash, randomCopy);
		        
		        if (changeOriginal)
		        {
		        	String randomOriginal = String.format(templateOriginal, getRandomReplacement());
					buffer = ByteBuffer.wrap(HashAlgorithm.calculateHash(randomOriginal.getBytes()));
			        buffer.order(ByteOrder.LITTLE_ENDIAN);
			        origHash = buffer.getInt(); 

			        originalHashes.put(origHash, randomOriginal);
		        }
			}
			
			for (Entry<Integer,String> hashEntry : originalHashes.entrySet()) {
				if (!collidedHashes.contains(hashEntry.getKey()) && copyHashes.containsKey(hashEntry.getKey())) {
					foundCollisions++;
					collidedHashes.add(hashEntry.getKey());
					PrintWriter out = new PrintWriter("original" + foundCollisions + ".txt");
					out.print(hashEntry.getValue());
					out.close();
					
					out = new PrintWriter("copy" + foundCollisions + ".txt");
					out.print(copyHashes.get(hashEntry.getKey()));
					out.close();
				}
			}	
			
			System.out.println("Calculating...");
			System.out.println("Number of Collisions: "  + foundCollisions);
			System.out.println("Number of original Hashes: " + originalHashes.size());
			System.out.println("Number of copy Hashes: " + copyHashes.size());
		}
	}
	
	private Object[] getRandomReplacement() {
		Object[] random = new Object[replacements.length];
		for (int i = 0; i < replacements.length; i++) {
			random[i] = replacements[i][(int) (Math.random() * replacements[i].length)];
		}
		return random;
	}
	
	private static String readFile(String path) throws IOException {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int)file.length()];
		fis.read(data);
		fis.close();
		
		return new String(data, "UTF-8");
	}
	
	
	
	private static String[][] readReplacements() throws IOException {
		List<String[]> repList = new ArrayList<String[]>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("Replacements.txt"), "UTF8"));

		
		String line;
		while ((line = reader.readLine()) != null)
			repList.add(line.split("\\|"));
		
		reader.close();
		
		return repList.toArray(new String[repList.size()][]);
	}
	
	public static void main(String[] args) {
		try {
			new Generator(2,true);
		} catch (IOException e) {
			System.out.println("Error accessing Files: " + e.getMessage());
		}
	}
}
