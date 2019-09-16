package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_NEW_EXP extends AST_EXP
{
	public int moish;

	public void PrintMe()
	{
		/************************************/
		/* AST NODE TYPE = EXP VAR AST NODE */
		/************************************/
		System.out.print("AST NODE NEW EXP VAR\n");

		/*****************************/
		/* RECURSIVELY PRINT var ... */
		/*****************************/
		System.out.println(moish);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"NEW EXP\nVAR");
	}

}