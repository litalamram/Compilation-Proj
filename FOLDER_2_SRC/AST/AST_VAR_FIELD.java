package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_VAR_FIELD extends AST_VAR
{
	public AST_VAR var;
	public String fieldName;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_FIELD(AST_VAR var,String fieldName)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.fieldName = fieldName;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void PrintMe()
	{
		/*********************************/
		/* AST NODE TYPE = AST FIELD VAR */
		/*********************************/
		System.out.print("AST NODE FIELD VAR\n");

		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.PrintMe();
		System.out.format("FIELD NAME( %s )\n",fieldName);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("FIELD\nVAR\n...->%s",fieldName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
	}
	public TYPE SemantMe()
	{
		TYPE t = null, dataMemberType;
		TYPE_CLASS classType = null;

		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var == null){
			throw new MyRunTimeException(this.line);
		}
		var.isRight = this.isRight;
		t = var.SemantMe();

		/*********************************/
		/* [2] Make sure type is a class */
		/*********************************/
		t = t.getTypeToBeCompared();
		if (!t.isClass())
		{
			System.out.format("%s\n","Error from : AST_VAR_FIELD");
			System.out.format(">> ERROR [%d] access %s field of a non-class variable\n",var.line, fieldName);
			throw new MyRunTimeException(var.line);
		}
		// t must be class

		classType = (TYPE_CLASS) t;

		/************************************/
		/* [3] Look for fiedlName inside tc */
		/************************************/
		dataMemberType = classType.getInheritedFieldType(fieldName);
		/*********************************************/
		/* [4] fieldName does not exist in class var */
		/*********************************************/

		if (dataMemberType == null){
			System.out.format("%s\n","Error from : AST_VAR_FIELD");
			System.out.format(">> ERROR [%d] field %s does not exist in class\n",var.line, fieldName);
			throw new MyRunTimeException(var.line);
		}

		this.offset = dataMemberType.offset;
		return dataMemberType;
	}
	public TEMP IRme()
	{
		var.isRight = this.isRight;
		TEMP varTemp = var.IRme();  // contain frame address that contains base address
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();

		String label_illegal_pointer_ref = IRcommand.getFreshLabel("illegal_pointer_ref");
		IR.getInstance().Add_IRcommand(new IRcommand_IsClassRefLegal(varTemp, label_illegal_pointer_ref, this.isRight));
		IR.getInstance().Add_IRcommand(new IRcommand_LoadField(
				t,varTemp,(this.offset+1)*4, this.isRight));
		IR.getInstance().Add_IRcommand(new IRcommand_IllegalPointerRef(label_illegal_pointer_ref));

		return t;
	}
}

