package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import IR.*;
import MIPS.*;
import TEMP.*;

public class AST_DEC_ARRAYDEC extends AST_DEC{

	String type;
	String arrayName;
	int typeLine, arrayNameLine;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_ARRAYDEC(String arrayName, String type, int arrayNameLine, int typeLine)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== arrayDec -> array ID(%s) = ID(%s) [] \n", arrayName, type);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.type = type;
		this.arrayName = arrayName;
		this.arrayNameLine = arrayNameLine;
		this.typeLine = typeLine;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE ARRAYDEC\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSRIPT ... */
		/****************************************/
		System.out.println(type);
		System.out.println(arrayName);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("ARRAYDEC\narray %s = %s[]\n", arrayName, type));

	}
	public TYPE SemantMe()
	{
		TYPE typeExists,typeAlreadyDefined;

		if(SYMBOL_TABLE.getInstance().getScopeCount() != 0){
			System.out.format("%s","ERROR FROM AST_DEC_ARRAYDEC");
			System.out.format(">> ERROR [%d] declare array %s not in global scope\n", this.line, arrayName);
			throw new MyRunTimeException(this.line);
		}

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		typeExists = SYMBOL_TABLE.getInstance().find(type);
		if(typeExists == null) {
			System.out.format("%s","ERROR FROM AST_DEC_ARRAYDEC");
			System.out.format(">> ERROR [%d] type %s is not defined\n", this.typeLine, type);
			throw new MyRunTimeException(this.typeLine);
		}
		if(!typeExists.isClass() &&
				!typeExists.isArray() &&
				!typeExists.isEquals(TYPE_INT.getInstance()) &&
				!typeExists.isEquals(TYPE_STRING.getInstance())
		) {
			System.out.format("%s","ERROR FROM AST_DEC_ARRAYDEC");
			System.out.format(">> ERROR [%d]  %s is not a type\n", this.typeLine, type);
			throw new MyRunTimeException(this.typeLine);
		}

		typeAlreadyDefined = SYMBOL_TABLE.getInstance().find(arrayName);
		if(typeAlreadyDefined !=null){
			System.out.format("%s","ERROR FROM AST_DEC_ARRAYDEC");
			System.out.format(">> ERROR [%d] variable %s is already defined\n", this.arrayNameLine, arrayName);
			throw new MyRunTimeException(this.arrayNameLine);
		}

		if(AST_Node.isReservedWord(arrayName)){
			System.out.format("%s\n","Error from : AST_DEC_ARRAYDEC");
			System.out.format(">> ERROR [%d] cannot declar array of reserved name %s\n", this.arrayNameLine, arrayName);
			throw new MyRunTimeException(this.arrayNameLine);
		}

		SYMBOL_TABLE.getInstance().enter(arrayName,new TYPE_ARRAY(arrayName, typeExists, true));
		return typeExists;
	}

	public TEMP IRme(){
		return null;
	}
}
