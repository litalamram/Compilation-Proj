package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_STMT_IF extends AST_STMT
{
	public AST_EXP cond;
	public AST_STMT_LIST body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_IF(AST_EXP cond, AST_STMT_LIST body)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== stmt -> IF (exp){stmtList} \n");

		this.cond = cond;
		this.body = body;
	}

	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST EXP STMT IF */
		/*******************************/
		System.out.print("AST NODE STMT IF\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (cond != null) cond.PrintMe();
		if (body != null) body.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"IF (exp) {stmtList}\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (cond != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cond.SerialNumber);
		if (body != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
	}

	public TYPE SemantMe()
	{
		TYPE t,t1;
		TYPE_FUNCTION function;
		String condTypeName= null;
		SYMBOL_TABLE.getInstance().beginScope(SYMBOL_TABLE.IF_SCOPE);
		/**********************************************/
		/* [1] Check If Type exists and get exp type */
		/********************************************/
		t = cond.SemantMe();
		t = t.getTypeToBeCompared();
		if (t == null)
		{
			System.out.format("%s\n","Error from : AST_STMT_IF");
			System.out.format(">> ERROR [%d] illigal exp in STMT_IF\n", cond.line);
			throw new MyRunTimeException(cond.line);
		}
		/**************************************/
		/* [2] Check cond type */
		/**************************************/
		if(t.isFunction()){
			function = (TYPE_FUNCTION) t;
			condTypeName = function.returnType.name;
		}

//		else if(t.isClass()){
		//TODO!!!! -
//		}
		else{
			condTypeName = t.name;
		}
		if(!condTypeName.equals("int")){
			System.out.format("%s\n","Error from : AST_STMT_IF");
			System.out.format(">> ERROR [%d] cond type is not int %s\n",cond.line, t.name);
			throw new MyRunTimeException(cond.line);

		}
		body.offset = this.offset;
		body.funcParamNum = this.funcParamNum;
		body.functionName = this.functionName;
		t1 = body.SemantMe();
		this.offset = body.offset;

		if (t1 == null)
		{
			System.out.format("%s\n","Error from : AST_STMT_IF");
			System.out.format(">> ERROR [%d] illigal body in STMT_IF\n", body.line);
			throw new MyRunTimeException(body.line);
		}


		SYMBOL_TABLE.getInstance().endScope();

		/*********************************************************/
		/* [4] Return TYPE */
		/*********************************************************/

		return t;

	}

	public TEMP IRme()
	{
		TEMP tempCond = null;
		TEMP tempBody = null;

		if (cond != null){
			tempCond = cond.IRme();
		}
		IR.getInstance().Add_IRcommand(new IRcommand_If(tempCond));

		// if body is null -> parser error so this is never null if we got here
		if (body != null){
			body.useLabelEnd = true;
			tempBody = body.IRme();
		}
		return tempCond;
	}
}