package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import java.lang.*;

import TEMP.*;
import IR.*;

public class AST_EXP_BINOP extends AST_EXP
{
	int OP;
	public AST_EXP left;
	public AST_EXP right;
	public boolean isStringCompare;


	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_BINOP(AST_EXP left, AST_EXP right, int OP)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> exp BINOP exp\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		this.OP = OP;
		this.isStringCompare = false;

	}

	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		String stringOP="";

		/*********************************/
		/* CONVERT OP to a printable stringOP */
		/*********************************/
		if (OP == 0) {stringOP = "+";}
		if (OP == 1) {stringOP = "-";}
		if (OP == 2) {stringOP = "*";}
		if (OP == 3) {stringOP = "/";}
		if (OP == 4) {stringOP = "<";}
		if (OP == 5) {stringOP = ">";}
		if (OP == 6) {stringOP = "=";}
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE BINOP EXP\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.PrintMe();
		if (right != null) right.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("BINOP(%s)",stringOP));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, left.SerialNumber);
		if (right != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, right.SerialNumber);
	}

	public TYPE SemantMe()
	{
		TYPE type1 = null;
		TYPE type2 = null;
		TYPE_CLASS firstClass, secondClass;
		TYPE_ARRAY arrayArg = null;
		TYPE_ARRAY arrayArg2 = null;
		
		if (left  != null) type1 = left.SemantMe();
		if (right != null) type2 = right.SemantMe();

		type1 = type1.getTypeToBeCompared();
		type2 = type2.getTypeToBeCompared();
		//if both int
		if ((type1.isEquals(TYPE_INT.getInstance())) && (type2.isEquals(TYPE_INT.getInstance())))
		{
			return TYPE_INT.getInstance();
		}
		//if both string
		if ((type1.isEquals(TYPE_STRING.getInstance())) && (type2.isEquals(TYPE_STRING.getInstance()))){
			this.isStringCompare= true;
			if (this.OP == 0)
				return TYPE_STRING.getInstance();
			if (this.OP == 6)
				return TYPE_INT.getInstance();

			else{
				System.out.format("Error from: AST_EXP_BINOP\n");
				System.out.format(">> ERROR [%d] illigal operation on strings\n",this.line);
				throw new MyRunTimeException(this.line);
			}

		}
		// Equality Testing
		if (this.OP == 6){
			//Check special array inharitance case - has to be same type !!
			if(type1.isEquals(type2) && type1.name.equals("newID")){
				arrayArg = (TYPE_ARRAY) type1;
				arrayArg2 = (TYPE_ARRAY) type2;
				if(arrayArg.t.isEquals(arrayArg2.t)){
					return TYPE_INT.getInstance();
				}
			}

			// Equality is valid when the two types is equal
			else if (!type1.isEquals(type2)){
				// class and array can be compared to nil
				if (type2.isEquals(TYPE_NIL.getInstance()) && (type1.isClass() || type1.isArray())){
					return TYPE_INT.getInstance();
				}
				if (type1.isEquals(TYPE_NIL.getInstance()) && (type2.isClass() || type2.isArray())){
					return TYPE_INT.getInstance();
				}

				// Check for inheritance
				if (type1.isClass() && type2.isClass()){
					firstClass = (TYPE_CLASS) type1;
					secondClass = (TYPE_CLASS) type2;
					if(firstClass.gotFromAncestor(secondClass) || secondClass.gotFromAncestor(firstClass)){
						return TYPE_INT.getInstance();
					}
				}
				//Check for array compared to new array
				if (type1.isArray() && type2.isArray()){
					arrayArg = (TYPE_ARRAY) type1;
					arrayArg2 = (TYPE_ARRAY) type2;
					if((type2.name.equals("newID") || type1.name.equals("newID")) && arrayArg.t.isEquals(arrayArg2.t)){
						return TYPE_INT.getInstance();
					}

				}

			}
			else return TYPE_INT.getInstance();
		}
		// If we got here, the binary operation is illegal :\
		System.out.format("%s\n", "Error from: AST_EXP_BINOP");
		System.out.format(">> ERROR [%d] variables types doesn't match [%s,%s]\n", this.line, type1.name, type2.name);
		throw new MyRunTimeException(this.line);
	}

	public TEMP IRme()
	{
		TEMP t1 = null;
		TEMP t2 = null;
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();

		if (left  != null){
			if(right != null && right.isExpBrackes()){
				t2 = right.IRme();
				t1 = left.IRme();
			}
			else{
				t1 = left.IRme();
				if (right != null) t2 = right.IRme();
			}

		}
		else if (right != null) t2 = right.IRme();

		if (OP == 0)
		{
			if(this.isStringCompare) {
				IR.
						getInstance().
						Add_IRcommand(new IRcommand_Binop_Concat_Strings(dst,t1,t2)); //dst = t1t2
			}
			else {
				IR.
						getInstance().
						Add_IRcommand(new IRcommand_Binop_Add_Integers(dst,t1,t2));
			}
		}
		if (OP == 1)
		{
			IR.
					getInstance().
					Add_IRcommand(new IRcommand_Binop_Substruct_Integers(dst,t1,t2));
		}
		if (OP == 2)
		{
			IR.
					getInstance().
					Add_IRcommand(new IRcommand_Binop_Multiply_Integers(dst,t1,t2));
		}
		if (OP == 3)
		{
			IR.
					getInstance().
					Add_IRcommand(new IRcommand_Binop_Divide_Integers(dst,t1,t2));
		}
		if ((OP == 4) || (OP == 5))
		{
			IR.
					getInstance().
					Add_IRcommand(new IRcommand_Binop_LT_Integers(dst,t1,t2, (OP==4)));
		}
		//need to verify if this is a string or an int.
		if (OP == 6)
		{
			if(this.isStringCompare)
			{
				IR.
						getInstance().
						Add_IRcommand(new IRcommand_Binop_EQ_Strings(dst,t1,t2)); //dst = 0 if eqauls otherwise 1.
			}
			else {
				IR.
						getInstance().
						Add_IRcommand(new IRcommand_Binop_EQ_Integers(dst,t1,t2));
			}
		}
		return dst;
	}
}
