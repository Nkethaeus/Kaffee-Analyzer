package parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ast.*;
import lexer.LexicalAnalyzer;
import lexer.Token;
import lexer.TokenType;

public class SyntaxAnalyzer {
	private LexicalAnalyzer lexer;
	private Token token;
	private Token errorToken;

	// HashMap para sa order ng mga operators
	private final static Map<TokenType, Integer> binopLevels;
	
	private ArrayList <VarDecl> decelarations; // Array para sa mga declarations
	private ArrayList <Identifier> identifiers; // Array para sa mga identifiers
	private ArrayList <Assign> assigns; // Array para sa mga assignments
	private ArrayList <Exp> conditions; // Array para sa mga conditions
	
	private int errors;

	static {
		binopLevels = new HashMap<TokenType, Integer>();
		binopLevels.put(TokenType.LOG_AND, 10);
		binopLevels.put(TokenType.LOG_OR, 10);
		binopLevels.put(TokenType.LOG_LESS_THAN, 20);
		binopLevels.put(TokenType.LOG_GREATER_THAN, 20);
		binopLevels.put(TokenType.LOG_LESS_THAN_EQ, 20);
		binopLevels.put(TokenType.LOG_GREATER_THAN_EQ, 20);
		binopLevels.put(TokenType.LOG_EQ, 20);
		binopLevels.put(TokenType.LOG_NEQ, 20);
		binopLevels.put(TokenType.MATH_ADD, 30);
		binopLevels.put(TokenType.MATH_SUB, 30);
		binopLevels.put(TokenType.MATH_MUL, 40);
		binopLevels.put(TokenType.MATH_DIV, 40);
		binopLevels.put(TokenType.LBRACKET, 50);
	}

	// Taga-gawa ng instance ng Lexer para makapag-check ng tokens
	public SyntaxAnalyzer(FileReader file) throws IOException {
		this.lexer = new LexicalAnalyzer(file);
		this.token = lexer.getToken();
		this.decelarations = new ArrayList<VarDecl>();
		this.identifiers = new ArrayList<Identifier>();
		this.assigns = new ArrayList<Assign>();
		this.conditions = new ArrayList<Exp>();
	}

	// Taga-check ng token saka taga-basa ng kasunod, or taga-report ng error
	private boolean eat(TokenType type) throws IOException {
		if (token.getType() == type) {
			token = lexer.getToken();
			return true;
		} else {
			error(type);
			return false;
		}
	}

	// Taga-output ng error
	private void error(TokenType type) {
		// Kapag na-report na yung error, 'di na niya i-re-report ulit
		if (token == errorToken)
			return;

		System.err.print("Syntax Error: " + token.getType());
		System.err.print(" at line " + token.getLineNumber() + ", column " + token.getColumnNumber());
		System.err.println("; Expected " + type);
				
		errorToken = token;
		errors++; // Taga-dagdag sa error counter
	}

	// 'Di papansinin yung mga tokens hangga't 'di nakikita yung kaparehas
	private void skipTo(TokenType... follow) throws IOException {
		while (token.getType() != TokenType.END_OF_FILE) {
			for (TokenType skip : follow) {
				if (token.getType() == skip)
					return;
			}
			token = lexer.getToken();
		}
	}

	// Taga-kuha at taga-bigay ng mga syntax errors
	public int getErrors() {
		return errors;
	}

	public ArrayList <VarDecl> getDecelarations() {
		return decelarations;
	}

	public ArrayList <Identifier> getIdentifiers() {
		return identifiers;
	}

	public ArrayList <Assign> getAssigns() {
		return assigns;
	}

	public ArrayList <Exp> getConditions() {
		return conditions;
	}

	//Para sa buong program
	/* Syntax ng buong program: init(){
									<program code>
								}*/
	public Program parseProgram() throws IOException {		
		eat(TokenType.INITIALIZER);
		eat(TokenType.LPAREN);
		eat(TokenType.RPAREN);
		eat(TokenType.LBRACKET);

		Declarations declarations = parseDeclarations();
		StatementList statementList = parseStatementList();

		eat(TokenType.RBRACKET);
		eat(TokenType.END_OF_FILE);
		return new Program(statementList, declarations);
	}

	// Taga-parse ng mga declarations
	// Taga-check kung isa ba sa tatlong datatypes yung binabasa niya ngayon
	private Declarations parseDeclarations() throws IOException{
		Declarations declarations = new Declarations();

		while(token.getType() == TokenType.INITIALIZER || token.getType() == TokenType.TYPE_NUM
				|| token.getType() == TokenType.TYPE_BOOL || token.getType() == TokenType.TYPE_STR)
			declarations.addElement(parseVarDecList());

		return declarations;
	}

	// Syntax ng declaration: DATATYPE IDENTIFIER;
	private VarDeclList parseVarDecList() throws IOException{
		VarDeclList varDeclList = new VarDeclList();
		VarDecl varDecl = parseVarDecl();
		varDeclList.addElement(varDecl);
		getDecelarations().add(varDecl);

		// Taga-check kung may isa pang naka-declare, e.g. DATATYPE IDENTIFIER, IDENTIFIER, IDENTIFIER;
		while (token.getType() == TokenType.COMMA) {
			eat(TokenType.COMMA);
			VarDecl newVarDecl = new VarDecl(varDecl.getType(), parseIdentifier());
			varDeclList.addElement(newVarDecl);
			getDecelarations().add(newVarDecl);
		}
		eat(TokenType.SEMICOLON);

		return varDeclList;
	}

	// Taga-save ng declaration
	private VarDecl parseVarDecl() throws IOException {
		Type type = parseType();
		Identifier id = parseIdentifier();
		return new VarDecl(type, id);
	}

	// Taga-check kung tama ba declaration ng array, e.g. DATATYPE [SIZE];
	private Type parseType() throws IOException {
		switch (token.getType()) {

		case INITIALIZER:
			eat(TokenType.INITIALIZER);

			// Taga-check ng type ng array
			if (token.getType() == TokenType.LBRACKET) {
				eat(TokenType.LBRACKET);

				// Taga-check ng size ng array
				if(eat(TokenType.VALUE_NUMBER)){
					if (token.getType() == TokenType.RBRACKET) {
						eat(TokenType.RBRACKET);
						return new IntegerArrayType();
					}
				}

				// Maling declaration ng array
				eat(TokenType.TYPE);
				return null;
			}
			return new IntegerType();

		case TYPE_NUM:
			eat(TokenType.TYPE_NUM);

			// Taga-check kung NUM ba ang datatype ng array
			if (token.getType() == TokenType.LBRACKET) {
				eat(TokenType.LBRACKET);

				// Taga-check ng size ng array
				if(eat(TokenType.VALUE_NUMBER)){
					if (token.getType() == TokenType.RBRACKET) {
						eat(TokenType.RBRACKET);
						return new FloatArrayType();
					}
				}

				// Maling declaration ng array
				eat(TokenType.TYPE);
				return null;
			}
			return new FloatType();

		case TYPE_BOOL:
			eat(TokenType.TYPE_BOOL);

			// Taga-check kung BOOL ba ang datatype ng array
			if (token.getType() == TokenType.LBRACKET) {
				eat(TokenType.LBRACKET);

				// Taga-check ng size ng array
				if(eat(TokenType.VALUE_NUMBER)){
					if (token.getType() == TokenType.RBRACKET) {
						eat(TokenType.RBRACKET);
						return new BooleanArrayType();
					}
				}

				// Maling declaration ng array
				eat(TokenType.TYPE);
				return null;
			}
			return new BooleanType();

		case TYPE_STR:
			eat(TokenType.TYPE_STR);

			// Taga-check kung STR ba ang datatype ng array
			if (token.getType() == TokenType.LBRACKET) {
				eat(TokenType.LBRACKET);

				// Taga-check ng size ng array
				if(eat(TokenType.VALUE_NUMBER)){
					if (token.getType() == TokenType.RBRACKET) {
						eat(TokenType.RBRACKET);
						return new CharArrayType();
					}
				}

				// Maling declaration ng array
				eat(TokenType.TYPE);
				return null;
			}
			return new CharType();

		default:
			// Kapag wala sa tatlong datatypes yung array, unknown
			eat(TokenType.TYPE);
			return null;

		}
	}

	// Taga-parse ng mga variables
	private Identifier parseIdentifier() throws IOException {
		Identifier identifier = null;

		// Taga-kuha ng value ng variable
		if (token.getType() == TokenType.IDENTIFIER)
			identifier = new Identifier(token.getAttribute().getIdVal());
		
		eat(TokenType.IDENTIFIER);

		return identifier;
	}

	// Taga-save ng statements, e.g. { STATEMENT }
	private StatementList parseStatementList() throws IOException{
		StatementList statementList = new StatementList();
		while (isStatement())
			statementList.addElement(parseStatement());
		return statementList;
	}

	// Taga-check ng start ng statement
	private boolean isStatement() {
		switch(token.getType()){
		case SEMICOLON :
		case COND_IF : // Kapag sa IF nagsisimula...
		case LOOP_WHILE : // Kapag sa WHILE nagsisimula...
		case LPAREN : // Kapag sa '(' nagsisimula...
		case LBRACKET: // Kapag sa '{' nagsisimula...
		case IDENTIFIER : // Kapag sa IDENTIFIER nagsisimula...
			return true; // ...edi statement siya
		default: // Kapag wala siya sa mga yan...
			return false; // ...edi hindi siya statement
		}
	}

	// Taga-parse ng statements
	private Statement parseStatement() throws IOException {

		// Syntax ng IF: if '(' expression ')'
		if (token.getType() == TokenType.COND_IF) {
			eat(TokenType.COND_IF);

			// Taga-parse ng expression
			if (!eat(TokenType.LPAREN))
				skipTo(TokenType.RPAREN, TokenType.LBRACKET, TokenType.RBRACKET);

			Exp condExp = parseExp();
			conditions.add(condExp);

			if (!eat(TokenType.RPAREN))
				skipTo(TokenType.LBRACKET, TokenType.SEMICOLON, TokenType.RBRACKET);

			// Taga-parse ng boolean statements
			Statement trueStm;

			// Taga-basa ng statement after ng '{'
			if (token.getType() == TokenType.LBRACKET)
				trueStm = parseBlock();

			else
				// I-p-parse yung true
				trueStm = parseStatement();

			if (token.getType() == TokenType.COND_ELSE){
				if (!eat(TokenType.COND_ELSE))
					skipTo(TokenType.LBRACKET, TokenType.SEMICOLON, TokenType.RBRACKET);

				Statement falseStm;
				
				// Taga-basa ng statement after ng '{'
				if (token.getType() == TokenType.LBRACKET)
					falseStm = parseBlock();

				else
					// I-p-parse yung false
					falseStm = parseStatement();

				return new If(condExp, trueStm, falseStm);
			}
			return new If(condExp, trueStm, null);
		}

		// Syntax ng WHILE: while '(' expression ')' '{' statement '}'
		if (token.getType() == TokenType.LOOP_WHILE) {
			eat(TokenType.LOOP_WHILE);

			// Taga-parse ng loop
			if (!eat(TokenType.LPAREN))
				skipTo(TokenType.RPAREN, TokenType.LBRACKET, TokenType.RBRACKET);

			Exp condExp = parseExp();
			conditions.add(condExp);

			if (!eat(TokenType.RPAREN))
				skipTo(TokenType.LBRACKET, TokenType.SEMICOLON, TokenType.RBRACKET);

			Statement loopStm;

			// Taga-basa ng statement after ng '{'
			if (token.getType() == TokenType.LBRACKET)
				loopStm = parseBlock();

			else
				// I-p-parse yung loop
				loopStm = parseStatement();

			return new While(condExp, loopStm);
		}

		// Para sa identifiers
		if (token.getType() == TokenType.IDENTIFIER) {

			Identifier id = new Identifier(token.getAttribute().getIdVal());
			identifiers.add(id);
			eat(TokenType.IDENTIFIER);

			// Syntax ng assignment: IDENTIFIER = VALUE;
			if (token.getType() == TokenType.ASSIGN) {
				eat(TokenType.ASSIGN);
				Exp value = parseExp();
				
				eat(TokenType.SEMICOLON); // Taga-expect ng semicolon

				Assign assign = new Assign(id, value);
				assigns.add(assign);
				return assign;
			}

			// Syntax ng ARRAY: IDENTIFIER '[' SIZE ']' = VALUE;
			if (token.getType() == TokenType.LBRACKET) {
				eat(TokenType.LBRACKET);
				Exp index = parseExp();
				
				if(!(index instanceof IntegerLiteral)){
					// Kapag unknown yung statement
					eat(TokenType.TYPE);
					token = lexer.getToken();
					return null;
				}

				if (!eat(TokenType.RBRACKET))
					skipTo(TokenType.ASSIGN, TokenType.SEMICOLON);

				if (!eat(TokenType.ASSIGN))
					skipTo(TokenType.SEMICOLON);

				Exp value = parseExp();
				
				eat(TokenType.SEMICOLON); //Taga-expect ng semicolon
				
				Assign assign = new Assign(id, value);
				assigns.add(assign);
				return new ArrayAssign(id, index, value);
			}
		}
		// Kapag unknown yung statement
		eat(TokenType.STATEMENT);
		token = lexer.getToken();
		return null;
	}

	// Taga-parse ng block, syntax ng block: '{' STATEMENT '}'
	private Block parseBlock() throws IOException{
		eat(TokenType.LBRACKET);

		// Tatawagin nang tatawagin yung parseStatement() na function hanggang makita niya yung end '}'
		StatementList stms = new StatementList();
		while (token.getType() != TokenType.RBRACKET && token.getType() != TokenType.END_OF_FILE)
			stms.addElement(parseStatement());

		if (!eat(TokenType.RBRACKET)) 
			skipTo(TokenType.RBRACKET, TokenType.SEMICOLON);

		return new Block(stms);
	}

	// Taga-parse ng expressions
	private Exp parseExp() throws IOException {
		Exp lhs = parsePrimaryExp();
		return parseBinopRHS(0, lhs); // Taga-check ng mga binary operators
	}

	// Taga-parse ng expression bago i-parse yung mga binary operators, e.g. ((expression) && (expression))
	private Exp parsePrimaryExp() throws IOException {
		switch (token.getType()) {

		case VALUE_NUMBER:
			int intValue = token.getAttribute().getIntVal();
			eat(TokenType.VALUE_NUMBER);
			return new IntegerLiteral(intValue);

		case VALUE_BOOLEAN:
			boolean booleanVal = token.getAttribute().getBooleanVal();
			eat(TokenType.VALUE_BOOLEAN);
			return new BooleanLiteral(booleanVal);

		case VALUE_STRING:
			char charVal = token.getAttribute().getCharVal();
			eat(TokenType.VALUE_STRING);
			return new CharLiteral(charVal);

		case IDENTIFIER:
			Identifier id = parseIdentifier();
			identifiers.add(id);
			return new IdentifierExp(id.getName());

		case NOT:
			eat(TokenType.NOT);
			return new Not(parseExp());

		case LPAREN:
			eat(TokenType.LPAREN);
			Exp exp = parseExp();
			eat(TokenType.RPAREN);
			return exp;

		default:
			// Kapag mali yung expression
			eat(TokenType.EXPRESSION);
			token = lexer.getToken();
			return null;
		}
	}

	// Taga-expressions based sa precedence ng operators
	private Exp parseBinopRHS(int level, Exp lhs) throws IOException {
		// Taga-parse lang nang taga-parse hanggang makakita ng lower precedence na operator
		while (true) {
			// Taga-kuha ng precedence ng operator
			// -1 kung 'di siya operator
			Integer val = binopLevels.get(token.getType());
			int tokenLevel = (val != null) ? val.intValue() : -1;

			// Kapag mas mababa precedence ng operator kaysa sa naunang operator sa kanya,
			// or hindi operator yung token na nabasa
			if (tokenLevel < level)
				return lhs;

			// Taga-save ng binary operator bago i-parse yung right side ng expression
			TokenType binop = token.getType();
			eat(binop);

			Exp rhs = parsePrimaryExp(); // Taga-parse ng right side ng expression

			// Taga-kuha ng precedence ng operator
			// -1 kung 'di siya operator
			val = binopLevels.get(token.getType());
			int nextLevel = (val != null) ? val.intValue() : -1;
 
			// Kapag mas mataas yung precedence ng kasunod na operator kaysa sa nauna sa kanya,
			// tawagin ulit yung function
			if (tokenLevel < nextLevel)
				rhs = parseBinopRHS(tokenLevel + 1, rhs);

			// Taga-build ng AST para sa mga binary operators
			switch (binop) {
			case LOG_AND:
				lhs = new And(lhs, rhs);
				break;
			case LOG_OR:
				lhs = new Or(lhs, rhs);
				break;
			case LOG_EQ:
				lhs = new Equal(lhs, rhs);
				break;
			case LOG_NEQ:
				lhs = new NotEqual(lhs, rhs);
				break;
			case LOG_LESS_THAN:
				lhs = new LessThan(lhs, rhs);
				break;
			case LOG_GREATER_THAN:
				lhs = new MoreThan(lhs, rhs);
				break;
			case LOG_LESS_THAN_EQ:
				lhs = new LessThanEqual(lhs, rhs);
				break;
			case LOG_GREATER_THAN_EQ:
				lhs = new MoreThanEqual(lhs, rhs);
				break;
			case MATH_ADD:
				lhs = new Plus(lhs, rhs);
				break;
			case MATH_SUB:
				lhs = new Minus(lhs, rhs);
				break;
			case MATH_MUL:
				lhs = new Times(lhs, rhs);
				break;
			case MATH_DIV:
				lhs = new Divide(lhs, rhs);
				break;
			case LBRACKET:
				lhs = new ArrayLookup(lhs, rhs);
				eat(TokenType.RBRACKET);
				break;
			default:
				eat(TokenType.OPERATOR);
				break;
			}
		}
	}
}