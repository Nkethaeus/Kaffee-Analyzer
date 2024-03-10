package semantic;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import semantic.SemanticAnalyzer;

public class KaffeeSemantic {
	public static void main(String[] args) throws IOException {
		if (args.length == 0)
			System.err.println("No file arguments given");
		else {
			// Taga-kuha ng mga file na nasa arguments
			for (int i = 0; i < args.length; i++) {
				FileReader file;
				// Try na buksan yung file
				try {
					file = new FileReader("C:\\Users\\jerom\\Documents\\Programmierung Projekte\\Java Projekte\\PL-Projekt\\Kaffee-LexicalSyntaxSemanticAnalyzer\\Kaffee-LexicalSyntaxSemanticAnalyzer\\tests\\KaffeeTest.txt");
				} catch (FileNotFoundException e) {
					System.err.println(args[i] + " was not found!");
					continue; // Try yung sunod na file
				}
				
				// I-s-start yung Semantic analyzer
				SemanticAnalyzer semantic = new SemanticAnalyzer(file);
				System.out.println("[Semantic Analysis] " + args[i]);
				
				// Start ang pagch-check at clock time
				semantic.analyzeProgram();
				
				System.out.println(semantic.getErrors() + " semantic error(s)");
				System.out.println("---");
			}
		}
	}
}