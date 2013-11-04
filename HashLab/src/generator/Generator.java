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

/**
 * @author Stefan Eggenschwiler & Daniel Guerber
 * Generates new files from the templates & replacement files
 * and calculates hashes looking for collisions.
 */
public final class Generator {
	
	private final int minCollisions;
	private final String fileOriginal;
	private final String templateOriginal;
	private final String templateCopy;
	private final String[][] replacements;
	private final boolean changeOriginal;
	
	/**
	 * Reads in the files and starts the calculation
	 * @param minCollisions Number of minimal collisions to find
	 * @param changeOriginal Specifies if original file should be generated
	 * @throws IOException Is thrown in case of problems with file handling
	 */
	public Generator(int minCollisions, boolean changeOriginal) throws IOException {
		this.minCollisions = minCollisions;
		this.changeOriginal = changeOriginal;
		
		this.fileOriginal = readFile("OriginalFile.txt");
		this.templateOriginal = readFile("PlaceholderOriginal.txt");
		this.templateCopy = readFile("PlaceholderCopy.txt");
		
		replacements = readReplacements();
		
		searchCollisions();
	}
	
	/**
	 * Searches for collisions by randomly generating new texts and calculating the hashes.
	 * @throws FileNotFoundException Is thrown in case of problems with file handling
	 */
	private void searchCollisions() throws FileNotFoundException {
		HashMap<Integer, String> originalHashes = new HashMap<Integer, String>();
		HashMap<Integer, String> copyHashes = new HashMap<Integer, String>();
		
		int foundCollisions = 0;
		HashSet<Integer> collidedHashes = new HashSet<Integer>();
		
		//Calculate hash for original file
		ByteBuffer buffer = ByteBuffer.wrap(HashAlgorithm.calculateHash(fileOriginal.getBytes()));
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int origHash = buffer.getInt();
		
		originalHashes.put(origHash, fileOriginal);
		
		//Search collisions
		while (foundCollisions < minCollisions) {
			//generate multiple copies to speed up
			for (int i = 0; i < 2048; i++) {
				//Generate random copy and calculate hash
				String randomCopy = String.format(templateCopy, getRandomReplacement());
				buffer = ByteBuffer.wrap(HashAlgorithm.calculateHash(randomCopy.getBytes()));
		        buffer.order(ByteOrder.LITTLE_ENDIAN);
		        int copyHash = buffer.getInt(); 

		        copyHashes.put(copyHash, randomCopy);
		        
		        //if enabled generate random original
		        if (changeOriginal)
		        {
		        	String randomOriginal = String.format(templateOriginal, getRandomReplacement());
					buffer = ByteBuffer.wrap(HashAlgorithm.calculateHash(randomOriginal.getBytes()));
			        buffer.order(ByteOrder.LITTLE_ENDIAN);
			        origHash = buffer.getInt(); 

			        originalHashes.put(origHash, randomOriginal);
		        }
			}
			
			//search for collisions and write them in a file
			for (Entry<Integer,String> hashEntry : originalHashes.entrySet()) {
				if (!collidedHashes.contains(hashEntry.getKey()) && copyHashes.containsKey(hashEntry.getKey())) {
					foundCollisions++;
					//make shure not to count collision twice
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
	
	/**
	 * Gets an Object array of strings to replace placeholder.
	 * @return Object array containing the replacement
	 */
	private Object[] getRandomReplacement() {
		Object[] random = new Object[replacements.length];
		for (int i = 0; i < replacements.length; i++) {
			random[i] = replacements[i][(int) (Math.random() * replacements[i].length)];
		}
		return random;
	}
	
	/**
	 * Reads a file into a string
	 * @param path Path of the file
	 * @return Full text of the file
	 * @throws IOException 
	 */
	private static String readFile(String path) throws IOException {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int)file.length()];
		fis.read(data);
		fis.close();
		
		return new String(data, "UTF-8");
	}
	
	
	/**
	 * reads the replacement file into an two dimensional string array
	 * @return array of replacements
	 * @throws IOException
	 */
	private static String[][] readReplacements() throws IOException {
		List<String[]> repList = new ArrayList<String[]>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("Replacements.txt"), "UTF8"));

		
		String line;
		while ((line = reader.readLine()) != null)
			repList.add(line.split("\\|"));
		
		reader.close();
		
		return repList.toArray(new String[repList.size()][]);
	}
	
	/**
	 * Executes the generator
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Generator(1,true);
		} catch (IOException e) {
			System.out.println("Error accessing Files: " + e.getMessage());
		}
	}
}
