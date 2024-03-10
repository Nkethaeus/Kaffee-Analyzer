package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import ast.Program;
import lexer.LexicalAnalyzer;
import lexer.Token;
import lexer.TokenType;
import parser.SyntaxAnalyzer;
import semantic.SemanticAnalyzer;
public class MainKaffee {

    public static void main(String[] args) throws IOException {
         Scanner kaffee = new Scanner(System.in);
         char userChoice2;
         
         System.err.println("=======================================");
         System.err.println("  There are currently no files in use  ");
         System.err.println("=======================================");
         
         System.out.println("Specify the path of text file to use: ");
         String KaffeeFile = kaffee.nextLine();
        	 
         File f = new File(KaffeeFile);
         if(f.exists()) {
        	 System.out.println("\n" + f.getName() + " exists");
         }
         else {
        	 System.err.println("\nFile doesn't exist");
        	 System.exit(0);
         }
         
         do { // Loop para kapag gusto pang umulit ng user
	         System.out.println("=======================================");
	         System.out.println("||\t-->KAFFEE MAIN MENU<--       ||");
	         System.out.println("=======================================");
	         System.out.println("||\t[1] Upload Text File         ||");
	         System.out.println("||\t[2] View Lexemes/Tokens      ||");
	         System.out.println("||\t[3] View Lexical Error(s)    ||");
	         System.out.println("||\t[4] View Syntax Error(s)     ||");
	         System.out.println("||\t[5] View Semantic Error(s)   ||");
	         System.out.println("||\t[6] Exit program             ||");
	         System.out.println("---------------------------------------");
	         System.out.print("\nWhat would you like to do: ");
	         
	         int userChoice1 = kaffee.nextInt();
	         
	         if (userChoice1 == 1) {
	        	 System.out.println("\n[File Upload]");
	        	 System.out.println("File already uploaded successfully.");
	         }
	         
	         else if (userChoice1 == 2) {
	        	 System.out.println("\n[Lexemes & Tokens]");
	        	 System.out.println("Reserved Words");
	        	 System.out.println("init()\t:\tKeyword (Initializer)");
	        	 System.out.println("sysin \t:\tKeyword (Input)");
	        	 System.out.println("sysout\t:\tKeyword (Output)");
	        	 System.out.println("pub\t:\tKeyword (Public)");
	        	 System.out.println("priv\t:\tKeyword (Private)\n");
					   
	        	 System.out.println("Datatypes");
	        	 System.out.println("num\t:\tDatatype (Numerical)");
	        	 System.out.println("str\t:\tDatatype (String)");
	        	 System.out.println("bool\t:\tDatatype (Boolean)");
	        	 System.out.println("flat\t:\tDatatype (Constant)\n");
					   
	        	 System.out.println("Conditions");
	        	 System.out.println("if\t:\tConditional (If)");
	        	 System.out.println("elsif\t:\tConditional (Else If)");
	        	 System.out.println("else\t:\tConditional (Else)\n");
					   
	        	 System.out.println("Loops");
	        	 System.out.println("while\t:\tLoop (While)");
	        	 System.out.println("for\t:\tLoop (For)\n");
					   
	        	 System.out.println("Logical Operators");
	        	 System.out.println("&&\t:\tLogical (And)");
	        	 System.out.println("||\t:\tLogical (Or)");
	        	 System.out.println("==\t:\tLogical (Equal)");
	        	 System.out.println("!=\t:\tLogical (Not Equal)");
	        	 System.out.println("<\t:\tLogical (Less Than)");
	        	 System.out.println(">\t:\tLogical (Greater Than)");
	        	 System.out.println("<=\t:\tLogical (Less Than or Equal)");
	        	 System.out.println(">=\t:\tLogical (Greater Than or Equal)\n");
					   
	        	 System.out.println("Arithmetic Operators");
	        	 System.out.println("+\t:\tArithmetic (Addition)");
	        	 System.out.println("-\t:\tArithmetic (Subtract)");
	        	 System.out.println("*\t:\tArithmetic (Multiplication)");
	        	 System.out.println("/\t:\tArithmetic (Division)");
	        	 System.out.println("^\t:\tArithmetic (Exponent)\n");
					   
	        	 System.out.println("Punctations");
	        	 System.out.println("(\t:\tPunctuation (Left Parenthesis)");
	        	 System.out.println(")\t:\tPunctuation (Right Parenthesis)");
	        	 System.out.println("{\t:\tPunctuation (Left Bracket)");
	        	 System.out.println("}\t:\tPunctuation (Right Bracket)");
	        	 System.out.println(":\t:\tPunctuation (Colon)");
	        	 System.out.println(";\t:\tPunctuation (Semicolon)");
	        	 System.out.println(",\t:\tPunctuation (Comma)\n");
	         		
	        	 System.out.println("Others");
	        	 System.out.println("=\t:\tAssignment");
	         }
	         
	         else if (userChoice1 == 3) {
	        	 if (args.length == 0)
	        			System.err.println("No file arguments given");
	        	 else {
	        		 // Babasahin na yung file na nasa argument
	        		 for (int i = 0; i < args.length; i++) {
	        			 FileReader file;
	        			 // T-try niyang buksan yung file
	        			 try {
	        				 file = new FileReader(KaffeeFile);
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
	        					 System.err.print(" [" + token.getLineNumber() + ", " + token.getColumnNumber() + "] \t");
	        					 System.err.print(token.getType());
	        					 System.out.println();
	        					 continue;
	        				 }
	        					
	        				 // Output para sa mga tokens [row, col]
	        				 System.out.print(" [" + token.getLineNumber() + ", " + token.getColumnNumber() + "] ");
	        				 System.out.print(token.getType());
	        					
	        				 // Taga-output ng tokens na may values
	        				 if (token.getType() == TokenType.IDENTIFIER)
	        					 System.out.println(":\t " + token.getAttribute().getIdVal());
	        				 else if (token.getType() == TokenType.VALUE_NUMBER)
	        					 System.out.println(":\t " + token.getAttribute().getIntVal());
	        				 else if (token.getType() == TokenType.VALUE_STRING)
	        					 System.out.println(":\t " + token.getAttribute().getCharVal());
	        				 else if (token.getType() == TokenType.VALUE_BOOLEAN)
	        					 System.out.println(":\t " + token.getAttribute().getBooleanVal());
	        				 else
	        					 System.out.println();    					
	        			 } while (token.getType() != TokenType.END_OF_FILE); // Loop hangga't 'di pa dulo ng file
	        		 }
	        	 }
	         }
	         
	         else if (userChoice1 == 4) {
	        	 if (args.length == 0)
	        		 System.err.println("No file arguments given");
	        	 else {
	        		 // Babasahin na yung file na nasa argument
	        		 for (int i = 0; i < args.length; i++) {
	        			 FileReader file;
	        			 // T-try niyang buksan yung file
	        			 try {
	        				 file = new FileReader(KaffeeFile);
	        			 } catch (FileNotFoundException e) {
	        				 System.err.println(args[i] + " was not found!");
	        				 continue; // Kung may extrang file sa argument, hahanapin rin niya
	        			 }
	        				
	        			 // Taga-create ng instance ng parser
	        			 SyntaxAnalyzer parser = new SyntaxAnalyzer(file);
	        			 System.out.println("[Syntax Analysis] " + args[i]);
	        				
	        			 // Taga-start ng syntax analysis
	        			 parser.parseProgram();
	        				
	        			 // Taga-output kung ilan yung errors
	        			 System.out.println(parser.getErrors() + " syntax error(s)");
	        			 System.out.println("---");
	        		 }
	        	 }
	         }
	         
	         else if (userChoice1 == 5) {
	        	 if (args.length == 0)
	        		 System.err.println("No file arguments given");
	        	 else {
	        		 // Babasahin na yung file na nasa argument
	        		 for (int i = 0; i < args.length; i++) {
	        			 FileReader file;	
	        			 // T-try niyang buksan yung file
	        			 try {
	        				 file = new FileReader(KaffeeFile);
	        			 } catch (FileNotFoundException e) {
	        				 System.err.println(args[i] + " was not found!");
	        				 continue; // Kung may extrang file sa argument, hahanapin rin niya
	        			 }
	        				
	        			 // Taga-create ng instance ng semantic analyzer
	        			 SemanticAnalyzer semantic = new SemanticAnalyzer(file);
	        			 System.out.println("[Semantic Analysis] " + args[i]);
	        				
	        			 // Taga-start ng semantic analysis
	        			 semantic.analyzeProgram();
	        				
	        			 // Taga-output kung ilan yung errors
	        			 System.out.println(semantic.getErrors() + " semantic error(s)");
	        			 System.out.println("---");
	        		 }
	        	 }
	         }
	         
	         else if (userChoice1 == 6) {
	        	 System.exit(0); //Taga-terminate ng program
	         }
	         
	         else {
	        	 System.err.println("Invalid selection!");
	         }
	            
	         System.out.println("\nDo you want to go back to the main menu? [Y/N]: ");
	         userChoice2 = kaffee.next().charAt(0);
				
	         if(userChoice2 =='Y' || userChoice2 == 'y') {
	        	 continue;
	         }
         }while(userChoice2 == 'Y' || userChoice2 == 'y');
    }
}