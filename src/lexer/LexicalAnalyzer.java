package lexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LexicalAnalyzer {
	private BufferedReader stream; // Taga-basa ng file/input stream
	private Token nextToken;
	private int nextChar;
	private int lineNumber = 1; // Row Counter
	private int columnNumber = 1; // Column Counter

	private final static Map<String, TokenType> reservedWords; // Dictionary para sa mga reserved words
	private final static Map<Character, TokenType> punctuation; // Dictionary para sa mga punctuations
	private final static Map<String, TokenType> operators; // Dictionary para sa mga operators
	
	private int errors; // Error Counter

	static {
		reservedWords = new HashMap<String, TokenType>();
		reservedWords.put("init", TokenType.INITIALIZER);
		reservedWords.put("sysout", TokenType.KEY_OUTPUT);
		reservedWords.put("sysin", TokenType.KEY_INPUT);
		reservedWords.put("num", TokenType.TYPE_NUM);
		reservedWords.put("str", TokenType.TYPE_STR);
		reservedWords.put("bool", TokenType.TYPE_BOOL);
		reservedWords.put("if", TokenType.COND_IF);
		reservedWords.put("else", TokenType.COND_ELSE);
		reservedWords.put("elsif", TokenType.COND_ELSIF);
		reservedWords.put("while", TokenType.LOOP_WHILE);

		punctuation = new HashMap<Character, TokenType>();
		punctuation.put('(', TokenType.LPAREN);
		punctuation.put(')', TokenType.RPAREN);
		punctuation.put('{', TokenType.LBRACKET);
		punctuation.put('}', TokenType.RBRACKET);
		punctuation.put(':', TokenType.COLON);
		punctuation.put(';', TokenType.SEMICOLON);
		punctuation.put(',', TokenType.COMMA);
		punctuation.put('=', TokenType.ASSIGN);
		punctuation.put('!', TokenType.NOT);

		operators = new HashMap<String, TokenType>();
		operators.put("&&", TokenType.LOG_AND);
		operators.put("||", TokenType.LOG_OR);
		operators.put("==", TokenType.LOG_EQ);
		operators.put("!=", TokenType.LOG_NEQ);
		operators.put("<", TokenType.LOG_LESS_THAN);
		operators.put(">", TokenType.LOG_GREATER_THAN);
		operators.put("<=", TokenType.LOG_LESS_THAN_EQ);
		operators.put(">=", TokenType.LOG_GREATER_THAN_EQ);
		operators.put("+", TokenType.MATH_ADD);
		operators.put("-", TokenType.MATH_SUB);
		operators.put("*", TokenType.MATH_MUL);
		operators.put("/", TokenType.MATH_DIV);
	}

	public LexicalAnalyzer(FileReader file) throws FileNotFoundException {
		this.stream = new BufferedReader(file);
		nextChar = getChar();
	}
	
	public int getErrors() {
		return errors;
	}

	// Error catcher para sa pagbasa ng file
	private int getChar() {
		try {
			return stream.read();
		} catch (IOException e) {
			System.err.print(e.getMessage());
			System.err.println("IOException occured in Lexer::getChar()");
			return -1;
		}
	}

	// Taga-skip ng mga line breaks (e.g. '\n', '\t')
	private boolean skipNewline() {
		if (nextChar == '\n') {
			lineNumber++;
			columnNumber = 1;
			nextChar = getChar();
			return true;
		}
		if (nextChar == '\t') {
			lineNumber++;
			columnNumber = 1;
			nextChar = getChar();
			return true;
		}
		return false; // Walang mga line break sa code
	}

	// Taga-check ng katabi ng current token na tinitignan ng Lexer
	public Token peek() throws IOException {
		if (nextToken == null)
			nextToken = getToken();

		return nextToken;
	}

	// Taga-return ng token sa binabasa na file
	public Token getToken() throws IOException {
		// check if peek() was called
		if (nextToken != null) {
			Token token = nextToken;
			nextToken = null; // 'Pag walang nakitang token, tatawagin si peek() para i-check yung kasunod na token
			return token;
		}

		// Taga-skip ng whitespaces
		while (Character.isWhitespace(nextChar)) {
			// Taga-check kung /n ba yung whitespace
			if (!skipNewline()) {
				columnNumber++;
				nextChar = getChar();
			}

			// Taga-move ng cursor
			if (nextChar == '\t')
				columnNumber += 3;
		}

		// Para sa mga identifiers and reserved words
		if (Character.isLetter(nextChar)) {
			String current = Character.toString((char) nextChar);
			columnNumber++;
			nextChar = getChar();

			while (Character.isLetterOrDigit(nextChar)) {
				current += (char) nextChar;
				columnNumber++;
				nextChar = getChar();
			}

			// Taga-check kung yung identifier ba ay reserved word
			TokenType type = reservedWords.get(current);

			if (type != null)
				return new Token(type, new TokenAttribute(), lineNumber, columnNumber - current.length());

			if(current.equals("true")) 
				return new Token(TokenType.VALUE_BOOLEAN, new TokenAttribute(true), lineNumber, columnNumber - current.length());
			else if(current.equals("false"))
				return new Token(TokenType.VALUE_BOOLEAN, new TokenAttribute(false), lineNumber, columnNumber - current.length());

			// Kung identifier nga siya
			return new Token(TokenType.IDENTIFIER, new TokenAttribute(current), lineNumber, columnNumber - current.length());
		}

		// Para sa mga integers/numbers
		if (Character.isDigit(nextChar)) {

			// Taga-convert ng integer/number to string
			String numString = Character.toString((char) nextChar);
			columnNumber++;
			nextChar = getChar();

			// Taga-concatenate ng mga katabi niyang integers/numbers
			while (Character.isDigit(nextChar)) {
				numString += (char) nextChar;
				columnNumber++;
				nextChar = getChar();
			}
			
			if(nextChar == '.'){
				nextChar = getChar(); 
				columnNumber++;
				
				if(Character.isDigit(nextChar)){
					numString += '.';
					// Taga-concatenate ng mga katabi niyang integers/numbers
					while (Character.isDigit(nextChar)) {
						numString += (char) nextChar;
						columnNumber++;
						nextChar = getChar();
					}
					
					return new Token(TokenType.VALUE_NUMBER, new TokenAttribute(Float.parseFloat(numString)), lineNumber, columnNumber - numString.length());
				}
				while(!Character.isWhitespace(nextChar)){
					columnNumber++;
					numString += nextChar;
					nextChar = getChar();
				}
				
				return new Token(TokenType.UNRECOGNIZED_TOKEN, new TokenAttribute(), lineNumber, columnNumber - numString.length() + 1);
			}

			// Taga-return ng integer/number
			return new Token(TokenType.VALUE_NUMBER, new TokenAttribute(Integer.parseInt(numString)), lineNumber, columnNumber - numString.length());
		}

		// Taga-recognize ng string
		if(nextChar == '\"' || nextChar == '\''){ //Kapag nabasa niya ay ''' or '"'
			nextChar = getChar();
			columnNumber++;
			if(Character.isAlphabetic(nextChar)){ //Babasahin niya yung nasa loob
				char current = (char) nextChar;
				stream.mark(0);
				nextChar = getChar();
				columnNumber++;

				if(nextChar == '\"' || nextChar == '\''){ //Kapag nabasa niya ulit yung ''' or '"', i-c-count na niya as string
					nextChar = getChar();
					columnNumber++;
					return new Token(TokenType.VALUE_STRING, new TokenAttribute(current), lineNumber, columnNumber - 1);
				}
				stream.reset();
			}
			
			return new Token(TokenType.UNRECOGNIZED_TOKEN, new TokenAttribute(), lineNumber, columnNumber - 1);
		}

		// Kapag nasa dulo na ng file
		if (nextChar == -1)
			return new Token(TokenType.END_OF_FILE, new TokenAttribute(), lineNumber, columnNumber);

		// Taga-check ng mga logical operators
		switch (nextChar) {
		
		case '&': //Kapag '&' ang nabasa niya
			columnNumber++;
			nextChar = getChar();

			// Ch-check niya kung ang kasunod ba ng '&' ay isa pang '&' para maging '&&' (AND)
			if (nextChar == '&') {
				nextChar = getChar();
				return new Token(TokenType.LOG_AND, new TokenAttribute(), lineNumber, columnNumber - 2);
			} else //Kung hindi
				return new Token(TokenType.UNRECOGNIZED_TOKEN, new TokenAttribute(), lineNumber, columnNumber - 1);

		case '|': //Kapag '|' ang nabasa niya
			columnNumber++;
			nextChar = getChar();

			// Ch-check niya kung ang kasunod ba ng '|' ay isa pang '|' para maging '||' (OR)
			if (nextChar == '|') {
				nextChar = getChar();
				return new Token(TokenType.LOG_OR, new TokenAttribute(), lineNumber, columnNumber - 2);
			} else //Kung hindi
				return new Token(TokenType.UNRECOGNIZED_TOKEN, new TokenAttribute(), lineNumber, columnNumber - 1);

		case '=': //Kapag '=' ang nabasa niya
			columnNumber++;
			nextChar = getChar();

			// Ch-check niya kung ang kasunod ba ng '=' ay isa pang '=' para maging '==' (IS EQUAL)
			if (nextChar == '=') {
				nextChar = getChar();
				return new Token(TokenType.LOG_EQ, new TokenAttribute(), lineNumber, columnNumber - 2);
			} else //Kung hindi, edi '=' (ASSIGNMENT OPERATOR) lang siya
				return new Token(TokenType.ASSIGN, new TokenAttribute(), lineNumber, columnNumber - 1);

		case '!': //Kapag '!' ang nabasa niya
			columnNumber++;
			nextChar = getChar();

			// Ch-check niya kung ang kasunod ba ng '!' ay '=' para maging '!=' (NOT EQUAL)
			if (nextChar == '=') {
				nextChar = getChar();
				return new Token(TokenType.LOG_NEQ, new TokenAttribute(), lineNumber, columnNumber - 2);
			} else //Kung hindi, edi '!' (NOT) lang siya
				return new Token(TokenType.NOT, new TokenAttribute(), lineNumber, columnNumber - 1);

		case '<': // Kapag '<' ang nabasa niya
			columnNumber++;
			nextChar = getChar();

			// Ch-check niya kung ang kasunod ba ng '<' ay '=' para maging '<=' (LESS THAN OR EQUAL)
			if (nextChar == '=') {
				nextChar = getChar();
				return new Token(TokenType.LOG_LESS_THAN_EQ, new TokenAttribute(), lineNumber, columnNumber - 2);
			} else // Kung hindi, edi '<' (LESS THAN) lang siya
				return new Token(TokenType.LOG_LESS_THAN, new TokenAttribute(), lineNumber, columnNumber - 1);

		case '>': // Kapag '>' ang nabasa niya
			columnNumber++;
			nextChar = getChar();

			// Ch-check niya kung ang kasunod ba ng '>' ay '=' para maging '>=' (GREATER THAN OR EQUAL)
			if (nextChar == '=') {
				nextChar = getChar();
				return new Token(TokenType.LOG_GREATER_THAN_EQ, new TokenAttribute(), lineNumber, columnNumber - 2);
			} else // Kung hindi, edi '>' (GREATER THAN) lang siya
				return new Token(TokenType.LOG_GREATER_THAN, new TokenAttribute(), lineNumber, columnNumber - 1);

		case '+': // Kapag '+' ang nabasa niya
			columnNumber++;
			nextChar = getChar();
			// Edi ADDITION siya
			return new Token(TokenType.MATH_ADD, new TokenAttribute(), lineNumber, columnNumber - 1);

		case '-': // Kapag '-' ang nabasa niya
			columnNumber++;
			nextChar = getChar();
			// Edi SUBTRACTION siya
			return new Token(TokenType.MATH_SUB, new TokenAttribute(), lineNumber, columnNumber - 1);

		case '*': // Kapag '*' ang nabasa niya
			columnNumber++;
			nextChar = getChar();
			// Edi MULTIPLICATION siya
			return new Token(TokenType.MATH_MUL, new TokenAttribute(), lineNumber, columnNumber - 1);

		case '/': // Kapag '/' ang nabasa niya
			columnNumber++;
			nextChar = getChar();
			// Edi DIVISION siya
			return new Token(TokenType.MATH_DIV, new TokenAttribute(), lineNumber, columnNumber - 1);
		}

		// Taga-check kung may punctuations
		TokenType type = punctuation.get((char) nextChar);
		columnNumber++;
		nextChar = getChar();

		// Kapag may nakitang punctuation
		if (type != null)
			return new Token(type, new TokenAttribute(), lineNumber, columnNumber - 1);

		// Kapag sa lahat ng checks nag-"false" siya, UNRECOGNIZED siya
		return new Token(TokenType.UNRECOGNIZED_TOKEN, new TokenAttribute(), lineNumber, columnNumber - 1);
	}
}
