package parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import parser.KaffeeParser;

public class KaffeeParser {
	public static void main(String[] args) throws IOException {
		if (args.length == 0)
			System.err.println("No file arguments given");
		else {
			// Babasahin na yung file na nasa argument
			for (int i = 0; i < args.length; i++) {
				FileReader file;
				// T-try niyang buksan yung file
				try {
					file = new FileReader("C:\\Users\\jerom\\Documents\\Programmierung Projekte\\Java Projekte\\PL-Projekt\\Kaffee-LexicalSyntaxSemanticAnalyzer\\Kaffee-LexicalSyntaxSemanticAnalyzer\\tests\\KaffeeTest.txt");
				} catch (FileNotFoundException e) {
					System.err.println(args[i] + " was not found!");
					continue; // Kung may extrang file sa argument, hahanapin rin niya
				}
				
				// Taga-create ng instance ng parser
				SyntaxAnalyzer parser = new SyntaxAnalyzer(file);
				System.out.println("[Syntax Analysis] " + args[i]);
				
				// I-s-start yung Parser
				parser.parseProgram();
				
				// Taga-output kung ilan yung errors
				System.out.println(parser.getErrors() + " syntax error(s)");
				System.out.println("---");
			}
		}
	}
}