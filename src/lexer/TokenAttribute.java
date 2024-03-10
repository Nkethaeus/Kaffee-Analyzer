package lexer;

public class TokenAttribute {
	private int intVal; // int value ng token
	private float floatVal; // float value ng token
	private char charVal; // char value ng token
	private boolean booleanVal; // boolean value ng token
	private String idVal; // id ng token

	public TokenAttribute() {}

	// gagawa ng TokenAttribute na may integer value
	public TokenAttribute(int intVal){
		this.intVal = intVal;
	}

	// gagawa ng TokenAttribute na may float value
	public TokenAttribute(float floatVal){
		this.floatVal = floatVal;
	}

	// gagawa ng TokenAttribute na may char value
	public TokenAttribute(char charVal){
		this.charVal = charVal;
	}

	// gagawa ng TokenAttribute na may boolean value
	public TokenAttribute(boolean booleanVal){
		this.booleanVal = booleanVal;
	}

	// gagawa ng TokenAttribute na may ID
	public TokenAttribute(String idVal){
		this.idVal = idVal;
	}

	public int getIntVal() {
		return intVal;
	}

	public void setIntVal(int intVal) {
		this.intVal = intVal;
	}

	public float getFloatVal() {
		return floatVal;
	}

	public void setFloatVal(float floatVal) {
		this.floatVal = floatVal;
	}

	public char getCharVal() {
		return charVal;
	}

	public void setCharVal(char charVal) {
		this.charVal = charVal;
	}

	public boolean getBooleanVal() {
		return booleanVal;
	}
	
	public void setBooleanVal(boolean booleanVal) {
		this.booleanVal = booleanVal;
	}

	public String getIdVal() {
		return idVal;
	}
	
	public void setIdVal(String idVal) {
		this.idVal = idVal;
	}
}
