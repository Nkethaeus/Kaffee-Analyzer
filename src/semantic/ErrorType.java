package semantic;

public enum ErrorType {
	//Undeclared variable error
	UNDECLARED_VARIABLE,
	
	//Multiple declarations ng variable error
	MULTIPLE_DECLARATION,
	
	//Type mismatch errors
	BOOL_TO_NUM, // BOOL -> NUM error
	BOOL_TO_STR, // BOOL -> STR error
	NUM_TO_BOOL, // NUM -> BOOL error
	NUM_TO_STR, // NUM -> STR error
	STR_TO_BOOL, // STR -> BOOL error
	
	SINGLE_TO_ARRAY, // SINGLE -> ARRAY error
	ARRAY_TO_SINGLE, // ARRAY -> SINGLE
	INVALID_CONDITION // Currently unused, kasi sira code para sa INVALID CONDITION [Reference: bugs.txt Line 11]
}
