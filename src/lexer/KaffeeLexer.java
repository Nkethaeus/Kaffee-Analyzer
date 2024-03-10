package lexer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import lexer.LexicalAnalyzer;
import lexer.Token;
import lexer.TokenType;

public class KaffeeLexer {
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
				
				// I-s-start yung Lexer
				LexicalAnalyzer lexer = new LexicalAnalyzer(file);
				
				System.out.println("[Lexical Analysis] " + args[i]);
				Token token;

				do {
					token = lexer.getToken();
					
					// Output para sa mga Unrecognized Tokens [row, col]
					if(token.getType() == TokenType.UNRECOGNIZED_TOKEN){
						System.err.print(" [" + token.getLineNumber() + ", " + token.getColumnNumber() + "] ");
						System.err.print(token.getType());
						System.out.println();
						continue;
					}
					
					// Output para sa mga tokens [row, col]
					System.out.print(" [" + token.getLineNumber() + ", " + token.getColumnNumber() + "] ");
					System.out.print(token.getType());
					
					// Taga-output ng tokens na may values
					if (token.getType() == TokenType.IDENTIFIER)
						System.out.println(": " + token.getAttribute().getIdVal());
					else if (token.getType() == TokenType.VALUE_NUMBER)
						System.out.println(": " + token.getAttribute().getIntVal());
					else if (token.getType() == TokenType.VALUE_STRING)
						System.out.println(": " + token.getAttribute().getCharVal());
					else if (token.getType() == TokenType.VALUE_BOOLEAN)
						System.out.println(": " + token.getAttribute().getBooleanVal());
					else
						System.out.println();
					
				} while (token.getType() != TokenType.END_OF_FILE); // Loop hangga't 'di pa dulo ng file
			}
		}
	}
}