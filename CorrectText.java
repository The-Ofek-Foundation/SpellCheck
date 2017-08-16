import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class CorrectText {

	private SpellCheck sc;
	private File textFile;

	public CorrectText(String textLoc) throws IOException {
		sc = new SpellCheck("words.txt", false);
		textFile = new File(textLoc);
	}

	public static void main(String... pumpkins) throws IOException {
		CorrectText ct = new CorrectText("text.txt");
		ct.run();
	}

	public void run() throws IOException {
		String txt = "";
		BufferedReader reader = new BufferedReader(new FileReader(textFile));
		for (String line = reader.readLine(); line != null; line = reader.readLine())
			txt += parseLine(line) + "\n";
		reader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
		writer.write(txt);
		writer.close();
	}

	public String parseLine(String line) {
		String newLine = "";
		String word = "";

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (Character.isLetter(c))
				word += c;
			else if (word.length() == 0)
				newLine += c;
			else {
				if (word.length() > 1)
					word = sc.fixTypo(word);
				newLine += word + c;
				word = "";
			}
		}
		if (word.length() != 0) {
			if (word.length() > 1)
				word = sc.fixTypo(word);
			newLine += word;
		}
		return newLine;
	}
}