package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_STMT_VARDEC extends AST_STMT{
	
	public AST_VARDEC_EXP varDec;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_STMT_VARDEC(AST_VARDEC_EXP varDec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> varDec;\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.varDec = varDec;
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST EXP LPARENRPAREN */
		/*******************************/
		System.out.print("AST NODE STMT VARDEC\n");
		
		if (varDec != null) varDec.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"VARDEC\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,varDec.SerialNumber);
	}
	public TYPE SemantMe(){

		TYPE t = null;
		varDec.offset = this.offset;


		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = varDec.SemantMe();
		if (t == null)
		{
			System.out.format(">> ERROR [%d] illigal varDec\n", varDec.line);
			throw new MyRunTimeException(varDec.line);
		}
		return t;
	}

	public TEMP IRme()
	{
		return varDec.IRme();
	}
}
