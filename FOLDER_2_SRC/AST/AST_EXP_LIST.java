package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_EXP_LIST extends AST_EXP
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_EXP head;
	public AST_EXP_LIST tail;
	public boolean isComparedTypes;
	public TYPE_LIST typeListToCompare;
	public int offsetInFrame = 4;
	public boolean isSysCall = false;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_LIST(AST_EXP head,AST_EXP_LIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== listExp -> exp expList\n");
		if (tail == null) System.out.print("====================== listExp -> exp      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.head = head;
		this.tail = tail;
		this.isComparedTypes = false;
		this.typeListToCompare = null;
	}


	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST STATEMENT LIST */
		/**************************************/
		System.out.print("AST NODE EXP LIST\n");

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
			"EXP\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (head != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,head.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}

	public TYPE SemantMe()
	{
		TYPE t = null, t1 = null, t2 = null, compared = null;
		AST_EXP first;
		TYPE_LIST listExp = null, listToCompare = null;
		TYPE_CLASS classArg, classArg2;
		TYPE_ARRAY arrayArg, arrayArg2;

		if(this.isComparedTypes && this.typeListToCompare == null){
			System.out.format("%s\n","Error from: AST_EXP_LIST");
			System.out.format(">> ERROR [%d] need to compare list types and compareTypes is empty\n",this.line);
			throw new MyRunTimeException(this.line);

		}
		listToCompare = this.typeListToCompare;

		/*************************************/
		/* RECURSIVELY SEMANT HEAD + TAIL ... */
		/*************************************/
		for (AST_EXP_LIST it = this; it != null; it = it.tail){
			if(this.isComparedTypes && listToCompare == null){
				System.out.format("%s\n","Error from: AST_EXP_LIST");
				System.out.format(">> ERROR [%d] need to compare list types and compareTypes is not equal length\n",this.line);
				throw new MyRunTimeException(this.line);

			}

			first = (AST_EXP) it.head;
			t = first.SemantMe();

			if(isComparedTypes){
				compared = (TYPE) listToCompare.head;
				if (compared == null){
					System.out.format("%s\n","Error from: AST_EXP_LIST");
					System.out.format("compared is null\n");
					throw new MyRunTimeException(this.line);
				}
				compared = compared.getTypeToBeCompared();
				t = t.getTypeToBeCompared();
				if(!compared.isEquals(t)){
					// types mismatch - try to check for nil
					if((compared.isClass() || compared.isArray()) &&  t.isEquals(TYPE_NIL.getInstance())){}
					// inheritance is allowed
					else if(compared.isClass() && t.isClass()){
						classArg = (TYPE_CLASS)compared;
						classArg2 = (TYPE_CLASS)t;
						if(!classArg.gotFromAncestor(classArg2)){
							System.out.format("%s\n", "Error from : AST_EXP_VAR_DOT_ID_EXP_LIST classArg.gotFromAncestor(classArg2)");
							System.out.format(">> ERROR [%d] function parameters mismatch [%s,%s]", first.line, t.name, compared.name);
							throw new MyRunTimeException(first.line);
						}
					}
					else if(compared.isArray() && t.isArray()){
						arrayArg = (TYPE_ARRAY)compared;
						arrayArg2 = (TYPE_ARRAY)t;
						if(!arrayArg2.name.equals("newID") || !arrayArg2.t.isEquals(arrayArg.t)){
							System.out.format("%s\n", "Error from : AST_EXP_VAR_DOT_ID_EXP_LIST else in for loop");
							System.out.format(">> ERROR [%d] function parameters mismatch [%s,%s]",first.line, t.name,compared.name);
							throw new MyRunTimeException(first.line);
						}
					}
					else{
						System.out.format("%s\n","Error from: AST_EXP_LIST");
						System.out.format(">> ERROR [%d] need to compare list types: mis-match [compared:%s,needToBeEqual:%s]\n", first.line ,compared.name, t.name);
						throw new MyRunTimeException(first.line);
					}
				}
				listToCompare = listToCompare.tail;
			}
			if(listExp == null){
				listExp =  new TYPE_LIST(t,listExp);
			}
			else{
				listExp.addElement(t);
			}
		}
		// check list sizes match
		if(listToCompare != null){
			System.out.format("%s\n","Error from: AST_EXP_LIST");
			System.out.format(">> ERROR [%d] too much arguments to %s in compared to:%s\n",this.line,t.name,compared.name);
			throw new MyRunTimeException(this.line);
		}
		this.typeListToCompare = null;
		this.isComparedTypes = false;
		return listExp;
	}
	public TEMP IRme()
	{
		TEMP headValue = head.IRme();
		if(!isSysCall){
			// save  head to frame
			IR.getInstance().Add_IRcommand(new IRcommand_StoreToStack(headValue, offsetInFrame + 4));

			if(tail != null){
				tail.offsetInFrame = this.offsetInFrame + 4;
				tail.IRme();
			}
		}


		return headValue;
	}
}