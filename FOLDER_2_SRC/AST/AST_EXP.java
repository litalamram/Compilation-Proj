package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public abstract class AST_EXP extends AST_Node
{
	public int moish;

	public void PrintMe()
	{
		/************************************/
		/* AST NODE TYPE = EXP VAR AST NODE */
		/************************************/
		System.out.print("AST NODE EXP VAR\n");

		/*****************************/
		/* RECURSIVELY PRINT var ... */
		/*****************************/
		System.out.println(moish);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"EXP\nVAR");
	}
	public boolean isExpBrackes(){return false;}


}