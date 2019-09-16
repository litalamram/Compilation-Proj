package AST;
import TYPES.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_EXP_NEGATIVE_NUM extends AST_EXP
{
	public int value;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_NEGATIVE_NUM(int value)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== exp -> - INT( %d )\n", value);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.value = -value;
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE NEGATIVE NUM( %d )\n",value);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("- INT(%d)",value));
	}

	public TYPE SemantMe()
	{
		return TYPE_INT.getInstance();
	}

	public TEMP IRme()
	{
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRcommandConstInt(t,value));
		return t;
	}
}
