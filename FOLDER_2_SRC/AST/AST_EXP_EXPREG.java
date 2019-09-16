package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;



public class AST_EXP_EXPREG extends AST_EXP
{
	public AST_EXPREG expReg;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_EXPREG(AST_EXPREG expReg)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== exp -> expReg\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.expReg = expReg;
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE EXP EXPREG\n");
		if(expReg!=null) expReg.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"EXP\nEXPREG");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,expReg.SerialNumber);
	}

	public TYPE SemantMe()
	{
		TYPE type;

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		type = expReg.SemantMe();
		if (type == null)
		{
			System.out.format("%s\n","Error from: AST_EXP_EXPREG");
			System.out.format(">> ERROR [%d] non existing type %s\n",expReg.line);
			throw new MyRunTimeException(expReg.line);
		}

		return type;
	}

	public TEMP IRme()
	{
		TEMP t = null;

		if (expReg != null) t = expReg.IRme();

		return t;
	}
}