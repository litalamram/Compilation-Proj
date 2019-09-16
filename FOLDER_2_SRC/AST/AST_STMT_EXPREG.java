package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;



public class AST_STMT_EXPREG extends AST_STMT {
	
	public AST_EXPREG expReg;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_STMT_EXPREG(AST_EXPREG expReg)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> expReg\n");

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
		/* AST NODE TYPE = AST EXP LPARENRPAREN */
		/*******************************/
		System.out.print("AST NODE STMT EXPREG\n");
		
		if (expReg != null) expReg.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"EXPREG;\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,expReg.SerialNumber);
	}

	/*********************************************************/
	/* The AST node semantic validation		 				 */
	/*********************************************************/
	public TYPE SemantMe()
	{
		TYPE t;
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = expReg.SemantMe();
		if (t == null)
		{
			System.out.format("%s\n", "Error from : AST_STMT_EXPREG");
			System.out.format(">> ERROR [%d] non existing type %s\n", expReg.line, expReg);
			throw new MyRunTimeException(expReg.line);
		}
		return t;
	}

	public TEMP IRme()
	{
		if (expReg != null) return expReg.IRme();

		return null;
	}
}
