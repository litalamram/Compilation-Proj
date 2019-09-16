package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_STMT_WHILE extends AST_STMT
{
	public AST_EXP cond;
	public AST_STMT_LIST body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_WHILE(AST_EXP cond,AST_STMT_LIST body)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== stmt -> WHILE (exp){stmtList} \n");

		
		this.cond = cond;
		this.body = body;
	}
	
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST EXP STMT WHILE */
		/*******************************/
		System.out.print("AST NODE STMT WHILE\n");
		
		if (cond != null) cond.PrintMe();
		if (body != null) body.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"WHILE (exp) {stmtList}\n");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cond.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
	}


	public TYPE SemantMe()
	{
		TYPE t,t1;
		String condTypeName= null;
		TYPE_FUNCTION function;

		// cond should not be null - grammer error
		if (cond == null){
			throw new MyRunTimeException(this.line);
		}
		SYMBOL_TABLE.getInstance().beginScope(SYMBOL_TABLE.WHILE_SCOPE);
		/**********************************************/
		/* [1] Check If Type exists and get exp type */
		/********************************************/
		t = cond.SemantMe();
		if (t == null)
		{
			System.out.format("%s\n","Error from : AST_STMT_WHILE");
			System.out.format(">> ERROR [%d] illigal exp in STMT_WHILE\n", cond.line);
			throw new MyRunTimeException(cond.line);
		}
		/**************************************/
		/* [2] Check cond type */
		/**************************************/
		if(t.isFunction()){
			function = (TYPE_FUNCTION) t;
			condTypeName = function.returnType.name;
		}
		else{
			condTypeName = t.getTypeToBeCompared().name;
		}
		if(!condTypeName.equals("int")){
			System.out.format("%s\n","Error from : AST_STMT_WHILE");
			System.out.format(">> ERROR [%d] cond type is not int %s\n",cond.line, t.name);
			throw new MyRunTimeException(cond.line);

		}

		body.offset = this.offset;
		body.funcParamNum = this.funcParamNum;
		body.functionName = this.functionName;
		t1 = body.SemantMe();

		if (t1 == null)
		{
			System.out.format("%s\n","Error from : AST_STMT_WHILE");
			System.out.format(">> ERROR [%d] illigal body in STMT_WHILE\n", body.line);
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
		// adding while label
		IR.getInstance().Add_IRcommand(new IRcommand_AddLabel("While",false));

		if (cond != null){
			tempCond = cond.IRme();
		}

		IR.getInstance().Add_IRcommand(new IRcommand_If(tempCond));
		// if body is null -> parser error so this is never null if we got here
		if (body != null){
			body.useLabelEnd = true;
			body.useGotoEnd = true;
			tempBody = body.IRme();
		}
		return tempCond;
	}
}