import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;

import java.util.Scanner;
import java.util.ArrayList;

public class SpellCheck {

	public Word[] words;
	public double startTime;
	private boolean output;


	public SpellCheck(String dictLoc, boolean output) throws IOException {
		this.output = output;
		dos("Counting words");
		File dict = new File(dictLoc);
		LineNumberReader lnr = new LineNumberReader(new FileReader(dict));
		lnr.skip(Long.MAX_VALUE);
		words = new Word[lnr.getLineNumber()];
		lnr.close();
		done("counted");

		dos("Processing words");
		BufferedReader reader = new BufferedReader(new FileReader(dict));
		int i = 0;
		for (String word = reader.readLine(); word != null; word = reader.readLine(), i++)
			words[i] = new Word(word);
		reader.close();
		done("processed");
	}

	public SpellCheck(String dictLoc) throws IOException {
		this(dictLoc, true);
	}

	public static void main(String... pumpkins) throws IOException {
		System.out.println();
		SpellCheck sc = new SpellCheck("words.txt");
		sc.run();
		System.out.println();
	}

	public void run() {
		Word typo;
		do {
			System.out.print("\nPlease enter a typo: ");
			typo = new Word(new Scanner(System.in).nextLine().toLowerCase());
			System.out.println();

			if (wordExists(typo)) {
				System.out.printf("\n\t\'%s\' is a real word!\n", typo);
				continue;
			}

			ArrayList<Word> matches = new ArrayList<Word>();

			findMatches(matches, typo);
			if (matches.size() == 1)
				continue;

			filterMatches(matches, typo);
			if (matches.size() == 1)
				continue;

			System.out.printf("\nFiltered down to %d options: ", matches.size());
			for (int i = 0; i < matches.size() - 1 && i < 5; i++)
				System.out.print(matches.get(i) + ", ");
			if (matches.size() > 5)
				System.out.println("etc...");
			else System.out.println(matches.get(matches.size() - 1));

			System.out.printf("\n\tBy popularity, you probably mean: \'%s\'\n\n",
				matches.get(0));

		} while (!typo.equals("quit"));
	}

	public String fixTypo(String str) {
		Word typo = new Word(str);
		if (wordExists(typo))
			return str;

		ArrayList<Word> matches = new ArrayList<Word>();

		findMatches(matches, typo);
		if (matches.size() == 1)
			return matches.get(0).word;

		filterMatches(matches, typo);
		if (matches.size() == 1)
			return matches.get(0).word;

		return matches.get(0).word;
	}

	public boolean filterMatches(ArrayList<Word> matches, Word typo) {
		filterByScore(matches, typo);
		if (analyzeMatches(matches))
			return true;
		return false;
	}

	public void filterByScore(ArrayList<Word> matches, Word typo) {
		dos("Scoring matches");
		int initSize = matches.size();
		double maxScore = 0;
		for (Word w : matches) {
			w.score = scoreR(w, typo, false);
			if (w.score > maxScore)
				maxScore = w.score;
		}

		for (int i = matches.size() - 1; i >= 0; i--)
			if (matches.get(i).score < maxScore)
				matches.remove(i);
		done(String.format("filtered out %,d matches", initSize - matches.size()));
	}

	public double scoreR(Word w1, Word w2, boolean ideal) {
		String[] ws = new String[] {w1.word, w2.word};

		while (score(ws));

		String w1f = ws[0].replace("[", "");

		double score = w1.word.length() - w1f.length();

		if (ideal)
			return score;

		if (w1.word.length() == w2.word.length())
			score += 0.5;

		return score;
	}

	public boolean score(String[] ws) {
		int max = 0, im = -1, am = -1, l1 = ws[0].length(), l2 = ws[1].length();
		for (int i = 0; i < l1; i++)
			for (int a = 0; a < l2; a++) {
				int b;
				for (b = 0; i + b < l1 && a + b < l2
					&& ws[0].charAt(i + b) == ws[1].charAt(a + b)
					&& ws[0].charAt(i + b) != '['; b++);
				if (b > max) {
					max = b;
					im = i;
					am = a;
				}
			}
		if (max < 2)
			return false;
		ws[0] = ws[0].substring(0, im) + "[" + ws[0].substring(im + max);
		ws[1] = ws[1].substring(0, am) + "]" + ws[1].substring(am + max);
		return true;
	}

	public int score(Word w, Word typo) {
		int max = 0, l1 = w.word.length(), l2 = typo.word.length();
		for (int i = 0; i < l1; i++)
			for (int a = 0; a < l2; a++) {
				int b;
				for (b = 0; i + b < l1 && a + b < l2
					&& w.word.charAt(i + b) == typo.word.charAt(a + b); b++);
				if (b > max)
					max = b;
			}

		return max;
	}

	public boolean findMatches(ArrayList<Word> matches, Word typo) {
		addIdealMatches(matches, typo);
		if (analyzeMatches(matches))
			return true;

		addSwapMatches(matches, typo);
		if (analyzeMatches(matches))
			return true;

		addMatches(matches, typo, "S");
		if (analyzeMatches(matches))
			return true;

		addMatches(matches, typo, "V");
		if (analyzeMatches(matches))
			return true;

		addMatches(matches, typo, "VS");
		if (analyzeMatches(matches))
			return true;

		addMatches(matches, typo, "C");
		if (analyzeMatches(matches))
			return true;

		addMatches(matches, typo, "CS");
		if (analyzeMatches(matches))
			return true;

		addMatches(matches, typo, "VC");
		if (analyzeMatches(matches))
			return true;

		addMatches(matches, typo, "VCS");
		if (analyzeMatches(matches))
			return true;

		return false;
	}

	public boolean analyzeMatches(ArrayList<Word> matches) {
		if (matches.size() == 0)
			return false;
		if (matches.size() == 1 && output)
			System.out.printf("\n\tYou probably mean: \'%s\'\n\n", matches.get(0));
		return true;
	}

	public void addMatches(ArrayList<Word> matches, Word word, String selector) {
		dos(String.format("Finding possible matches (%s)", selector));
		for (int i = 0; i < words.length; i++)
			if (words[i].get(selector).equals(word.get(selector)))
				matches.add(words[i]);
		done(String.format("found %,d matches", matches.size()));
	}

	public void addIdealMatches(ArrayList<Word> matches, Word word) {
		dos("Finding ideal matches");
		int len = word.word.length();
		for (Word w : words) {
			if (w.word.length() > len + 1)
				continue;
			w.score = scoreR(w, word, true);
			if (w.score == len)
				matches.add(w);
		}
		done(String.format("found %,d matches", matches.size()));
	}

	public void addSwapMatches(ArrayList<Word> matches, Word word) {
		dos("Finding swap matches");
		String tmp = word.word;
		int len = tmp.length();
		for (Word w : words) {
			if (w.word.length() != len)
				continue;
			for (int i = 0; i < len - 1; i++)
				if (w.word.equals(swap(tmp, i))) {
					matches.add(w);
					break;
				}
		}
		done(String.format("found %,d matches", matches.size()));
	}

	private String swap(String str, int i) {
		return str.substring(0, i) + str.charAt(i + 1) + str.charAt(i)
			+ str.substring(i + 2);
	}

	public boolean wordExists(Word word) {
		dos("Searching for word");
		for (Word w : words)
			if (w.equals(word)) {
				done("found");
				return true;
			}
		done("searched");
		return false;
	}

	public void dos(String something) {
		if (!output) return;
		System.out.printf("%-35s", something + "...");
		startTime = System.nanoTime();
	}

	public void done() { done("done"); }

	public void done(String message) {
		if (!output) return;
		double elapsedTime = (System.nanoTime() - startTime) / 1E9;
		System.out.printf("%s in %.3f seconds\n", message, elapsedTime);
	}

}
