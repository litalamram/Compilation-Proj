package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;
public class AST_VAR_SUBSCRIPT extends AST_VAR
{
	public AST_VAR var;
	public AST_EXP subscript;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SUBSCRIPT(AST_VAR var, AST_EXP subscript)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> var [ exp ]\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.subscript = subscript;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSRIPT ... */
		/****************************************/
		if (var != null) var.PrintMe();
		if (subscript != null) subscript.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"SUBSCRIPT\nVAR\n...[...]");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var       != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		if (subscript != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,subscript.SerialNumber);
	}
	public TYPE SemantMe()
	{
		var.isRight = this.isRight;
		TYPE t, expType;
		TYPE_ARRAY arrayType;

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		if(var == null){
			System.out.format("%s\n","Error from: AST_VAR_SUBSCRIPT");
			System.out.format("var is null\n");
			throw new MyRunTimeException(this.line);
		}

		t = var.SemantMe();
		if (t == null){
			System.out.format("%s\n","Error from: AST_VAR_SUBSCRIPT");
			System.out.format("t is null\n");
			throw new MyRunTimeException(this.line);
		}
		this.isFunctionLocalVar = var.isFunctionLocalVar;
		this.IsInFunctionScope = var.IsInFunctionScope;
		this.isFunctionParam = var.isFunctionParam;
		this.isArrayVar = isArrayVar;



		t = t.getTypeToBeCompared();
		if(!t.isArray()){
			System.out.format(">> ERROR [%d] var %s is not a array\n",var.line, t.name);
			throw new MyRunTimeException(var.line);
		}

		expType = subscript.SemantMe();
		expType = expType.getTypeToBeCompared();
		if (!expType.isEquals(TYPE_INT.getInstance()))
		{
			System.out.format(">> ERROR [%d] NOT EQUALS TYPES %s,%s\n",subscript.line, expType.name, TYPE_INT.getInstance().name);
			throw new MyRunTimeException(subscript.line);
		}

		arrayType = (TYPE_ARRAY) t;
		return arrayType.t;

	}

	public TEMP IRme()
	{
		TEMP temp = TEMP_FACTORY.getInstance().getFreshTEMP();
		var.isRight = this.isRight;
		TEMP varTemp = var.IRme();  // contain frame address that contains base address
		TEMP subscriptTemp = subscript.IRme(); // contain int

		//if var is null
		String label_illegal_pointer_ref = IRcommand.getFreshLabel("illegal_pointer_ref");
		IR.getInstance().Add_IRcommand(new IRcommand_IsArrayRefLegal(varTemp, label_illegal_pointer_ref, this.isRight));

		if(var.isStringVar) {
			IR.getInstance().Add_IRcommand(new IRcommand_LoadLocalArrayVar(
					temp, varTemp, subscriptTemp, this.offset, this.isRight, true));
		}
		else {
			IR.getInstance().Add_IRcommand(new IRcommand_LoadLocalArrayVar(
					temp, varTemp, subscriptTemp, this.offset, this.isRight, false));
		}

		IR.getInstance().Add_IRcommand(new IRcommand_IllegalPointerRef(label_illegal_pointer_ref));


		return temp;
	}
}
