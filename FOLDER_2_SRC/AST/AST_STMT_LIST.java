package AST;

import TYPES.*;
import TEMP.*;
import IR.*;

import java.lang.reflect.Type;

public class AST_STMT_LIST extends AST_STMT
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_STMT head;
	public AST_STMT_LIST tail;
	public int offset = 0; //increase the offset of local vars by 1 because of PrintTrace
	public int funcParamNum = 0;
	public String functionName = "";
	public boolean useLabelEnd = false;
	public boolean useGotoEnd = false;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_STMT_LIST(AST_STMT head,AST_STMT_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== stmts -> stmt stmts\n");
		if (tail == null) System.out.print("====================== stmts -> stmt      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.head = head;
		this.tail = tail;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST STATEMENT LIST */
		/**************************************/
		System.out.print("AST NODE STMT LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (head != null) head.PrintMe();
		if (tail != null) tail.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"STMT\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}

	public TYPE SemantMe()
	{

		TYPE t1=null,t2=null;
		/*************************************/
		/* RECURSIVELY SEMANT HEAD + TAIL ... */
		/*************************************/
		if (head != null) {
			if(head.getClass().getSimpleName().equals("AST_STMT_VARDEC")){
				this.offset++;
				//head.offset = this.offset;
			}
			head.offset = this.offset;
			head.funcParamNum = this.funcParamNum;
			head.functionName = this.functionName;
			t1 = head.SemantMe();
			this.offset = head.offset;

		}

		if (tail != null){
			tail.offset = this.offset;
			tail.funcParamNum = this.funcParamNum;
			tail.functionName = this.functionName;
			t2 = tail.SemantMe();
			this.offset = tail.offset;
		}

		return t1;
	}

	public TEMP IRme()
	{
		if (head != null){
			head.IRme();
		}
		if (tail != null) {
			tail.useLabelEnd = this.useLabelEnd;
			tail.useGotoEnd = this.useGotoEnd;
			tail.IRme();
		}
		else if(useGotoEnd || useLabelEnd){
			if(useGotoEnd)
				IR.getInstance().Add_IRcommand(new IRcommand_AddLabelFromStack(false));
			if(useLabelEnd)
				IR.getInstance().Add_IRcommand(new IRcommand_AddLabelFromStack(true));
		}
		return null;
	}
}
