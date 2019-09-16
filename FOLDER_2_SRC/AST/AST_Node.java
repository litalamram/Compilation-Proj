package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;

public abstract class AST_Node
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int SerialNumber;
	public int hight;
	public int line;
	public int col;

	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void PrintMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}

	public TYPE SemantMe(){
		System.out.print("NO AST NODE SEMANTEME FUNCTION\n");
		return null;
	}

	public static boolean isReservedWord(String word){
		boolean isReserved = false;
		switch(word){
			case "int":
				isReserved = true;
				break;
			case "string":
				isReserved = true;
				break;
			case "nil":
				isReserved = true;
				break;
			case "void":
				isReserved = true;
				break;
			case "array":
				isReserved = true;
				break;
			case "class":
				isReserved = true;
				break;
			case "if":
				isReserved = true;
				break;
			case "while":
				isReserved = true;
				break;
			case "extends":
				isReserved = true;
			/*case "PrintInt":
				isReserved = true;
			case "PrintString":
				isReserved = true;
			case "PrintTrace":
				isReserved = true;*/
			default:
				break;
		}
		return isReserved;
	}

	public void setLineNumber(int line){
		this.line = line;
	}

	/*****************************************/
	/* The default IR action for an AST node */
	/*****************************************/
	public TEMP IRme()
	{
		return null;
	}
}
