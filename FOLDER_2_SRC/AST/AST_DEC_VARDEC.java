package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;
public class AST_DEC_VARDEC extends AST_DEC{

	public AST_VARDEC_EXP varDec;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_VARDEC(AST_VARDEC_EXP varDec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== dec -> varDec\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.varDec = varDec;

	}

	/***********************************************/
	/* The default message for an exp var AST node */
	/***********************************************/
	public void PrintMe()
	{
		/************************************/
		/* AST NODE TYPE = EXP VAR AST NODE */
		/************************************/
		System.out.print("AST NODE DEC VARDEC\n");

		/*****************************/
		/* RECURSIVELY PRINT var ... */
		/*****************************/
		if (varDec != null) varDec.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"DEC\nVARDEC");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,varDec.SerialNumber);

	}
	public TYPE SemantMe()
	{
		TYPE t;

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = varDec.SemantMe();
		if (t == null)
		{
			System.out.format(">> ERROR [%d:%d] illigal varDec\n",varDec.line,varDec.col);
			throw new MyRunTimeException(varDec.line);
		}

		return t;
	}

	public TEMP IRme()
	{
		if (varDec != null){
			return varDec.IRme();
		}
		else return null;
	}

}
