public class Word {

	public final String word;
	public final String S, V, VS, C, CS, VC, VCS;
	public double score;

	public Word(String word) {
		this.word = word.toLowerCase();
		S = WordHelper.simplifyWord(word);
		V = WordHelper.mergeV(word);
		VS = WordHelper.mergeV(S);
		C = WordHelper.mergeC(word);
		CS = WordHelper.mergeC(S);
		VC = WordHelper.mergeC(V);
		VCS = WordHelper.mergeC(VS);
		score = -1;
	}

	public String get(String selector) {
		switch (selector) {
			case "S": return S;
			case "V": return V;
			case "VS": return VS;
			case "C": return C;
			case "CS": return CS;
			case "VC": return VC;
			case "VCS": return VCS;
			default: return word;
		}
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof String)
			return word.equals(that);
		return word.equals(((Word)that).word);
	}

	@Override
	public String toString() { return word; }
}

class WordHelper {

	public static final String VOWELS = "aeiou";

	private WordHelper() {}

	public static String simplifyWord(String word) {

		// reduce characters (y -> i, z -> s)
		word = word.replace('y', 'i');
		word = word.replace('z', 's');
		word = word.replace("ght", "ghte");
		word = word.replace("kn", "n");
		word = word.replace("gn", "n");

		// remove consecutive characters
		char lastChar = word.charAt(0);
		String simple = lastChar + "";
		for (int i = 1; i < word.length(); i++) {
			char c = word.charAt(i);
			if (c == lastChar)
				continue;
			lastChar = c;
			simple += lastChar;
		}

		if (simple.charAt(simple.length() - 1) == 'e')
			simple = simple.substring(0, simple.length() - 1);
		return simple;
	}

	public static String mergeV(String word) { // merge vowels
		return mergeV(word, true);
	}

	public static String mergeC(String word) { // merge consonants
		return mergeV(word, false);
	}

	public static String mergeV(String word, boolean vowel) { // merge v or c
		String simple = "";
		char c = vowel ? '{':'}';
		boolean cons = false;
		for (int i = 0; i < word.length(); i++) {
			char curr = word.charAt(i);
			if ((VOWELS.indexOf(curr) != -1) == vowel)
				if (!cons) {
					simple += c;
					cons = true;
				} else;
			else {
				cons = false;
				simple += curr;
			}
		}
		return simple;
	}
}
