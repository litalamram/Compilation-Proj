package AST;

import TYPES.*;

public abstract class AST_STMT extends AST_Node
{
	public int offset = 0;
	public int funcParamNum = 0;
	public String functionName = "";
	/*********************************************************/
	/* The default message for an unknown AST statement node */
	/*********************************************************/
	public void PrintMe()
	{
		System.out.print("UNKNOWN AST STATEMENT NODE");
	}
	public TYPE SemantMe()
	{
		return null;
	}
}
