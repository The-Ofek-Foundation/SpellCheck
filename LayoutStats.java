import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.util.ArrayList;
import java.util.Collections;

public class LayoutStats {

	private SpellCheck sc;

	public LayoutStats() throws IOException {
		sc = new SpellCheck("words.txt");
	}

	public static void main(String... pumpkins) throws IOException {
		LayoutStats ls = new LayoutStats();
		ls.run();
	}

	public void run() throws IOException {
		run("VC");
	}

	public void run(String selector) throws IOException {
		ArrayList<SortHelper> items = new ArrayList<SortHelper>();
		Word[] words = sc.wordCopy();

		for (int i = 0; i < words.length; i++) {
			if (words[i] == null) continue;
			String tmp = words[i].get(selector);
			int popularity = 1;
			for (int a = i + 1; a < words.length; a++) {
				if (words[a] == null) continue;
				if (words[a].get(selector).equals(tmp)) {
					popularity++;
					words[a] = null;
				}
			}
			items.add(new SortHelper(tmp, popularity));
			if (i % 1000 == 0)
				System.out.println(i + " " + words.length);
		}
		Collections.sort(items);

		output(items, selector);
	}

	public void output(ArrayList<SortHelper> items, String selector) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(
			new File(selector + ".txt")));
		for (SortHelper item : items)
			writer.write(item.word + " " + item.popularity + "\n");
		writer.close();
	}
}

class SortHelper implements Comparable<SortHelper> {

	public String word;
	public int popularity;

	public SortHelper(String word, int popularity) {
		this.word = word;
		this.popularity = popularity;
	}

	@Override
	public int compareTo(SortHelper that){
		return that.popularity - this.popularity;
	}
}
