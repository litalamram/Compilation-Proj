package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;
import SYMBOL_TABLE.*;

public class AST_CFIELD_VARDEC extends AST_CFIELD{

	public AST_VARDEC_EXP varDec;
	public int offset;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_VARDEC(AST_VARDEC_EXP varDec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== cField -> varDec\n");

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
		System.out.print("AST NODE CFIELD VARDEC\n");

		/*****************************/
		/* RECURSIVELY PRINT var ... */
		/*****************************/
		if (varDec != null) varDec.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"CFIELD\nVARDEC");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,varDec.SerialNumber);

	}

	public TYPE SemantMe()
	{
		TYPE type;
		varDec.fatherClass = this.fatherClass;
		type = varDec.SemantMe();

		return type;

	}
	public TEMP IRme(){

        if (varDec != null){
            varDec.offset = this.offset;
            return varDec.IRme();
        }

        else return null;
    }
}
