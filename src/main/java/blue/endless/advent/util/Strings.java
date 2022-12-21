package blue.endless.advent.util;

public class Strings {
	public static String removePrefix(String s, String toRemove) {
		if (!s.startsWith(toRemove)) throw new IllegalArgumentException("Expected: \""+toRemove+"\", Found: \""+s+"\"");
		return s.substring(toRemove.length());
	}
}
