package generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.Arrays;

import algorithm.HashAlgorithm;

public class Generator {
	public static String[][] readReplacements() throws IOException {
		List<String[]> repList = new ArrayList<String[]>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("Replacements.txt"), "UTF8"));

		
		String line;
		while ((line = reader.readLine()) != null)
			repList.add(line.split("\\|"));
		
		reader.close();
		
		return repList.toArray(new String[repList.size()][]);
	}
	
	public static void main(String[] args) throws IOException {
		String[][] replacements = readReplacements();
		
		File file = new File("OriginalFile.txt");
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int)file.length()];
		fis.read(data);
		fis.close();
		
		byte[] originalHash = HashAlgorithm.calculateHash(data);
		
		file = new File("PlaceHolderFile.txt");
		fis = new FileInputStream(file);
		byte[] dataPlaceHolder = new byte[(int)file.length()];
		fis.read(dataPlaceHolder);
		fis.close();
		
		String placeholder = new String(dataPlaceHolder, "UTF-8");
		System.out.println(placeholder);
		System.out.println(getCollisions(originalHash, placeholder, replacements, new String[replacements.length], 0, 0));
	}
	
	public static int getCollisions(byte[] originalHash, String placeholder, String[][] replacements, String[] usedReplacements, int position, int count) throws FileNotFoundException {
		if (position < replacements.length) {
			int newCount = 0;
			for (int i = 0; i < replacements[position].length; i++) {
				usedReplacements[position] = replacements[position][i];
				newCount += getCollisions(originalHash, placeholder, replacements, usedReplacements, position+1, count + newCount);
			}
			return newCount;
		} else {
			String newText = String.format(placeholder, (Object[])usedReplacements);
			byte[] newHash = HashAlgorithm.calculateHash(newText.getBytes());
			if (Arrays.areEqual(newHash, originalHash)) {
				PrintWriter out = new PrintWriter("copy" + count + ".txt");
				out.print(newText);
				out.close();
				return 1;
			} else {
				return 0;
			}
		}
	}
}
