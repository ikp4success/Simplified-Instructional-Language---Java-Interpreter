/**
 * Name: Immanuel I George
 * Class: CIS335
 * Project: 7
 * Objective: Compiler to Generate SIC/XE Assembly
 * 
 * 
 * */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class csucc {
	static Hashtable<String, String> data = new Hashtable<String, String>();
	static PrintWriter output;
	static int customIDCount = 0;

	public static void main(String[] args) throws FileNotFoundException {
		
		output = new PrintWriter("sample.asm");
		//stmtList("sample.txt");
		stmtList(args[0]);// terminal arguments
		output.println("\n");
		printMap(data);
		output.close();
		

	}

	public static void stmtList(String filePath) {
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(filePath));

			while ((sCurrentLine = br.readLine()) != null) {
				stmt(sCurrentLine);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void stmt(String sCurrentLine) {
		
		String[] stmt = sCurrentLine.split("=");
		data.put(stmt[0], stmt[1]);

		if (stmt[1].contains("(")) {/* Check for parenthesis*/
			if (stmt[1].contains(")")) {
				factor(stmt[1], stmt[0], "");
			} else {
				output.println("SYNTAX ERROR -- Missing )");
			}
		} else {/* Check for goto expr*/

			String customID = CreateCustomTokenID(stmt[1], customIDCount);
			customIDCount = customIDCount + 1;
			

			String[] tokens = stmt[1].split(" ");

			if (tokens.length == 3) {

				for (String token : tokens) {
					token.replace(" ", "").replace(";", "");
					if (isInteger(token) == true && !token.contains(";")
							&& !token.contains(" ")) {
						output.println("LDA #" + token);

					} else if (isInteger(token) == false
							&& !token.contains(";") && !token.contains(" ")
							&& !token.contains("")) {

						output.println("LDA " + token);
					}
				}
				output.println("STA " + stmt[0]);
			} else if (tokens.length > 3) {
				for (String token : tokens) {
					expr(token, tokens, stmt[0]);
				}
				output.println("STA " + stmt[0]);
			}

		}

	}

	private static void expr(String token, String[] tokens, String key) {

		if (token.equals("+")) {
			String leftexpr = tokens[Arrays.asList(tokens).indexOf(token) - 1]/*grab left so 3*4 will be 3*/
					.trim();
			String rightexpr = tokens[Arrays.asList(tokens).indexOf(token) + 1]/*grab right*/
					.trim();

			if (isInteger(leftexpr) == true) {
				output.println("LDA #" + leftexpr);
				

			} else {

				output.println("LDA " + leftexpr);
			}
			if (isInteger(rightexpr)) {
				output.println("ADD #" + rightexpr);
			} else {
				output.println("ADD " + rightexpr);
			}
		}

		else if (token.equals("-")) {
			String leftexpr = tokens[Arrays.asList(tokens).indexOf(token) - 1]
					.trim();
			String rightexpr = tokens[Arrays.asList(tokens).indexOf(token) + 1]
					.trim();

			if (isInteger(leftexpr) == true) {
				output.println("LDA #" + leftexpr);
				

			}
			if (!leftexpr.contains("(") && !leftexpr.contains(")")) {

				output.println("LDA " + leftexpr);
			}
			if (isInteger(rightexpr)) {
				output.println("SUB #" + rightexpr);
			} else {
				output.println("SUB " + rightexpr);
			}
		}
		term(token, tokens, key);

	}

	private static void term(String token, String[] tokens, String key) {

		if (token.equals("*")) {
			String leftexpr = tokens[Arrays.asList(tokens).indexOf(token) - 1]/*grab left so 3*4 will be 3*/
					.trim();
			String rightexpr = tokens[Arrays.asList(tokens).indexOf(token) + 1]/*grab right*/
					.trim();

			if (isInteger(leftexpr) == true) {
				output.println("LDA #" + leftexpr);
				

			} else {

				output.println("LDA " + leftexpr);
			}
			if (isInteger(rightexpr)) {
				output.println("MUL #" + rightexpr);
			} else {
				output.println("MUL " + rightexpr);
			}
		}

		else if (token.equals("/")) {
			String leftexpr = tokens[Arrays.asList(tokens).indexOf(token) - 1]
					.trim();
			String rightexpr = tokens[Arrays.asList(tokens).indexOf(token) + 1]
					.trim();

			if (isInteger(leftexpr) == true) {
				output.println("LDA #" + leftexpr);
				

			} else {

				output.println("LDA " + leftexpr);
			}
			if (isInteger(rightexpr)) {
				output.println("DIV #" + rightexpr);
			} else {
				output.println("DIV " + rightexpr);
			}
		}

	}

	private static void factor(String stmt, String key, String csuId) {
		String[] tokens = stmt.split(" ");
		// csuId = "";
		if (stmt.contains("(")) {
			String token = "";
			for (String gtp : tokens) {
				if (gtp.contains("(")) {
					token = gtp;

				}

			}
			// String tokenS;
			StringBuilder sb = new StringBuilder(100);
			int i = Arrays.asList(tokens).indexOf(token) + 1;

			while (token.equals("(")) {/*grab statement between parenthesis*/

				sb.append(" " + tokens[i] + " ");

				expr(tokens[i].trim(), tokens, key);
				i += 1;
				if (tokens[i].equals(")"))

					break;

			}
			csuId = CreateCustomTokenID(sb.toString(), customIDCount);
			String customStatement = stmt.replaceFirst("\\((.*?)\\)", csuId);
			output.println("STA " + csuId);
			factor(customStatement, key, csuId);/*try again for multiple bracket*/

			// String Fstmt=regexOperation("J+H", "");

		} else {/*if no more bracket process custom statement*/

			
			String[] customStatementTokens = stmt.split(" ");
			for (String csToken : customStatementTokens) {
				expr(csToken.trim(), customStatementTokens, key);
			}

			output.println("STA " + csuId);
			return;
		}
		output.println("STA " + key);

	}

	private static String CreateCustomTokenID(String stmt, int customIDCount) {
		/* Check if expression/term/factor is in statement already then create a custom id for it*/
		String customId = "";
		String[] tokens = stmt.split("");

		for (String token : tokens) {

			String keyC = data.get(token + " ");
			
			if (data.containsKey(token + " ") && !data.containsValue(token)) {

				customId = "T" + (customIDCount) + " ";

				data.put(customId, token);

			} 
		}
		

		return customId.trim();
	}
	
	public static void printMap(Map<String,String> mp) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        output.println(pairs.getKey() + " RESW 1");
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}

	private static boolean isInteger(String token) {
		/*Method to check if its an Integer*/
		try {
			Integer.parseInt(token);

		} catch (NumberFormatException ex) {
			return false;
		}
		return true;

	}

}
