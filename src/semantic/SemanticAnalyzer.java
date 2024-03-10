package semantic;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ast.Assign;
import ast.BooleanArrayType;
import ast.BooleanLiteral;
import ast.BooleanType;
import ast.CharArrayType;
import ast.CharLiteral;
import ast.CharType;
import ast.Equal;
import ast.Exp;
import ast.FloatArrayType;
import ast.FloatLiteral;
import ast.FloatType;
import ast.Identifier;
import ast.IdentifierExp;
import ast.IntegerArrayType;
import ast.IntegerLiteral;
import ast.IntegerType;
import ast.LessThan;
import ast.LessThanEqual;
import ast.MoreThan;
import ast.MoreThanEqual;
import ast.NotEqual;
import ast.Type;
import ast.VarDecl;
import lexer.TokenType;
import parser.SyntaxAnalyzer;

public class SemanticAnalyzer {

	private SyntaxAnalyzer parser;
	private ArrayList<VarDecl> declerations;
	private ArrayList<Identifier> identifiers;
	private ArrayList<Assign> assigns;
	private ArrayList<Exp> conditions;

	private int errors;

	public SemanticAnalyzer(FileReader file) throws IOException{
		this.parser = new SyntaxAnalyzer(file);
	}

	// Taga-kuha at taga-bigay ng mga semantic errors
	public int getErrors() {
		return errors;
	}
	
	// Taga-start ng semantic analyzer
	public void analyzeProgram() throws IOException{
		this.parser.parseProgram(); // I-a-access yung parser para malaman niya kung anu-ano mga rules
		this.declerations = this.parser.getDecelarations();
		checkDeclerations();
		this.identifiers = this.parser.getIdentifiers();
		checkIdenifiers();
		this.assigns = this.parser.getAssigns();
		checkAssigns();
		this.conditions = this.parser.getConditions();
		checkConditions();
	}

	// Taga-check kung may multiple declarations ba sa code
	private void checkDeclerations(){
		for(int i = 0; i < declerations.size(); i++){ // Taga-check ng declarations list
			VarDecl varDecl = declerations.get(i);
			String idName = varDecl.getId().getName();

			for(int j = i + 1; j < declerations.size(); j ++){ // Taga-check ulit ng declaration list
				VarDecl _varDecl = declerations.get(j);
				String _idName = _varDecl.getId().getName();

				if(idName.equals(_idName)) // Kapag may nakita siyang magka-parehas sa declaration list, return error
					error(ErrorType.MULTIPLE_DECLARATION, _idName);
			}
		}
	}

	// Taga-check ng mga identifiers sa code
	private void checkIdenifiers(){
		for (Identifier identifier : identifiers) { // Taga-check ng identifiers list
			if(!isIdentifierExists(identifier.getName())) // Kapag may nakita siya sa list na hindi naka-declare, return error
				error(ErrorType.UNDECLARED_VARIABLE, identifier.getName());
		}
	}
	
	// Taga-check ng conditions sa code
	private void checkConditions(){
		for (Exp exp : conditions) { //Kapag isa siya sa dito, tama
			if((exp instanceof MoreThan || exp instanceof MoreThanEqual ||exp instanceof LessThan ||
					exp instanceof LessThanEqual || exp instanceof NotEqual || exp instanceof Equal)){}
				// Note: Nag-b-bug yung error message kaya tinanggal ko.
			    // Which means, wala tayong error para sa INVALID CONDITION. ~Jerome [Reference: bugs.txt Line 11]
		}
	}
	
	// Taga-check kung nag-e-exist na yung mga identifiers.
	private boolean isIdentifierExists(String name){
		for (VarDecl varDecl : declerations) {
			String idName = varDecl.getId().getName();

			if(idName.equals(name))
				return true;
		}
		return false;
	}

	// Taga-check kung tama ba yung mga datatypes ng variables sa assigned values nila
	private void checkAssigns(){
		for (Assign assign : assigns) {
			Exp type = assign.getValue();
			String idName = assign.getId().getName();
			Type idType = getIdentifierType(idName);

			// Taga-check ng mga variables na naka-assign sa NUM
			if(idType != null && (idType instanceof IntegerType || idType instanceof IntegerArrayType)){
				// BOOLEAN -> NUM
				if(type instanceof BooleanLiteral)
					error(ErrorType.BOOL_TO_NUM, idName);

				if(type instanceof IdentifierExp){
					String _idName = ((IdentifierExp) type).getName();
					Type _idType = getIdentifierType(_idName);

					if(_idType != null){
						// BOOL -> INT
						if( _idType instanceof BooleanType)
							error(ErrorType.BOOL_TO_NUM, idName);

						if(idType instanceof IntegerType)
							// Variables na naka-declare as ARRAY
							if (_idType instanceof FloatArrayType || _idType instanceof BooleanArrayType
									|| _idType instanceof IntegerArrayType || _idType instanceof CharArrayType)
								error(ErrorType.ARRAY_TO_SINGLE, idName);

						if(idType instanceof IntegerArrayType)
							// Variables na naka-declare as SINGLE
							if (_idType instanceof FloatType || _idType instanceof BooleanType
									|| _idType instanceof IntegerType || _idType instanceof CharType)
								error(ErrorType.SINGLE_TO_ARRAY, idName);
					}
				}
			}

			// Taga-check ng mga variables na naka-assign sa STR
			if(idType != null && (idType instanceof CharType || idType instanceof CharArrayType)){

				// NUM -> STR
				if(type instanceof IntegerLiteral)
					error(ErrorType.NUM_TO_STR, idName);

				// BOOL -> STR
				if(type instanceof BooleanLiteral)
					error(ErrorType.BOOL_TO_STR, idName);

				// Taga-check ng mga variables na naka-assign sa NUM
				if(type instanceof IdentifierExp){
					String _idName = ((IdentifierExp) type).getName();
					Type _idType = getIdentifierType(_idName);

					if(_idType != null){
						// NUM -> STR
						if (_idType instanceof IntegerType)
							error(ErrorType.NUM_TO_STR, idName);

						// BOOL -> STR
						else if( _idType instanceof BooleanType)
							error(ErrorType.BOOL_TO_STR, idName);

						if(idType instanceof CharType)
							// Variables na naka-declare as ARRAY
							if (_idType instanceof FloatArrayType || _idType instanceof BooleanArrayType
									|| _idType instanceof IntegerArrayType || _idType instanceof CharArrayType)
								error(ErrorType.ARRAY_TO_SINGLE, idName);

						if(idType instanceof CharArrayType)
							// Variables na naka-declare as SINGLE
							if (_idType instanceof FloatType || _idType instanceof BooleanType
									|| _idType instanceof IntegerType || _idType instanceof CharType)
								error(ErrorType.SINGLE_TO_ARRAY, idName);
					}
				}
			}

			// Taga-check ng mga variables na naka-assign sa BOOL
			if(idType != null && (idType instanceof BooleanType || idType instanceof BooleanArrayType)){
				// NUM -> BOOL
				if(type instanceof IntegerLiteral)
					error(ErrorType.NUM_TO_BOOL, idName);
				// STR -> BOOL
				if(type instanceof CharLiteral)
					error(ErrorType.STR_TO_BOOL, idName);

				// Taga-check ng mga variables na naka-assign sa NUM
				if(type instanceof IdentifierExp){
					String _idName = ((IdentifierExp) type).getName();
					Type _idType = getIdentifierType(_idName);

					if(_idType != null){
						// NUM -> STRING
						if (_idType instanceof IntegerType)
							error(ErrorType.NUM_TO_STR, idName);

						// STR -> BOOL
						else if( _idType instanceof CharType)
							error(ErrorType.STR_TO_BOOL, idName);

						if(idType instanceof BooleanType)
							// Variables na naka-declare as ARRAY
							if (_idType instanceof FloatArrayType || _idType instanceof BooleanArrayType
									|| _idType instanceof IntegerArrayType || _idType instanceof CharArrayType)
								error(ErrorType.ARRAY_TO_SINGLE, idName);

						if(_idType instanceof BooleanArrayType)
							// Variables na naka-declare as SINGLE
							if (_idType instanceof FloatType || _idType instanceof BooleanType
									|| _idType instanceof IntegerType || _idType instanceof CharType)
								error(ErrorType.SINGLE_TO_ARRAY, idName);
					}
				}
			}
		}
	}

	// Taga-kuha ng datatype ng variable
	private Type getIdentifierType(String name){
		for (VarDecl dec : declerations) {
			Identifier id = dec.getId();
			if(id.getName().equals(name))
				return dec.getType();
		}
		return null;
	}

	// Taga-output ng mga errors
	private void error(ErrorType errorType, Object parm){
		errors++;
		switch (errorType) {
		case MULTIPLE_DECLARATION:
			System.err.println("Semantic Error: Multiple Declarations, variable (" + (String) parm + ")");
			break;
		case UNDECLARED_VARIABLE:
			System.err.println("Semantic Error: Undeclared Variable, variable (" + (String) parm + ")");
			break;
		case BOOL_TO_NUM:
			System.err.println("Semantic Error: Type Mismatch [BOOL -> NUM], variable (" + parm + ")");
			break;
		case NUM_TO_BOOL:
			System.err.println("Semantic Error: Type Mismatch [NUM -> BOOL] variable (" + parm + ")");
			break;
		case STR_TO_BOOL:
			System.err.println("Semantic Error: Type Mismatch [STR -> BOOL], variable (" + parm + ")");
			break;
		case BOOL_TO_STR:
			System.err.println("Semantic Error: Type Mismatch [BOOL -> STR], variable (" + parm + ")");
			break;
		case NUM_TO_STR:
			System.err.println("Semantic Error: Type Mismatch [NUM -> STR], variable (" + parm + ")");
			break;
		case ARRAY_TO_SINGLE:
			System.err.println("Semantic Error: Invalid Assignment of array [Array -> Single], variable (" + parm + ")");
			break;
		case SINGLE_TO_ARRAY:
			System.err.println("Semantic Error: Invalid Assignment of array [Single -> Array], variable (" + parm + ")");
			break;
		case INVALID_CONDITION:
			System.err.println("Semantic Error: Invalid Condition");
			break;
		default:
			break;
		}
	}
}