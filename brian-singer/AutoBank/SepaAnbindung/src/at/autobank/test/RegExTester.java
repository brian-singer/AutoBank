package at.autobank.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTester {
	public static void main(String[] args) {
		String txt = "199,97'SEQ++3";

		String re1 = "(\\d+)"; // Integer Number 1
		String re2 = "(,)"; // Any Single Character 1
		String re3 = "(\\d+)"; // Integer Number 2

		Pattern p = Pattern.compile(re1 + re2 + re3, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		Matcher m = p.matcher(txt);
		if (m.find()) {
			String int1 = m.group(1);
			String c1 = m.group(2);
			String int2 = m.group(3);
			System.out.print("(" + int1.toString() + ")" + "(" + c1.toString()
					+ ")" + "(" + int2.toString() + ")" + "\n");
			System.out.println("Or simply " + m.group());
		}
	}
}
