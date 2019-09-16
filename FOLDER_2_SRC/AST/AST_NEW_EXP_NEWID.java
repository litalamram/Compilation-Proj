package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_NEW_EXP_NEWID extends AST_NEW_EXP
{
	public String type;
	public String className;
	public int objSize = 0;
	public boolean isVtableEmpty = true;

	int OFFSET_TO_FRAME = 36 +4;
	int STACK_OFFSET = OFFSET_TO_FRAME + 2*4;

	public boolean isNewID(){
		return true;
	}

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_NEW_EXP_NEWID(String type)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== exp -> new ID ( %s)\n", type);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.type = type;
	}

	/************************************************/
	/* The printing message for an INT EXP AST node */
	/************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST INT EXP */
		/*******************************/
		System.out.format("AST NODE NEW EXP NEW ID( %s )\n", type);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("new String(%s)", type));
	}


	public TYPE SemantMe()
	{
		TYPE t;
		TYPE_CLASS classType;

		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = SYMBOL_TABLE.getInstance().find(type);
		if (t == null)
		{
			System.out.println("Error from : AST_NEW_EXP_NEWID");
			System.out.format(">> ERROR [%d] non existing type %s\n",this.line,type);
			throw new MyRunTimeException(this.line);
		}

		//check type is a class
		if(t.isClass()){
			classType = (TYPE_CLASS) t;
			this.objSize = classType.fieldNum + 1;
			if (classType.funcNum > 0) this.isVtableEmpty = false;
			this.className = classType.name;
		}

		//check type isn't string or int
		if((t.isEquals(TYPE_STRING.getInstance())) || (t.isEquals(TYPE_INT.getInstance()))){
			System.out.format("Error from : AST_NEW_EXP_NEWID\n");
			System.out.format(">> ERROR [%d] primitive types cannot be assigned with NEW %s\n",this.line,t.name);
			throw new MyRunTimeException(this.line);
		}

		//check type isn't array
		if(t.isArray()){
			System.out.format("Error from : AST_NEW_EXP_NEWID\n");
			System.out.format(">> ERROR [%d] array type can not be assigned without '[]' %s\n",this.line,t.name);
			throw new MyRunTimeException(this.line);
		}

		/*********************************************************/
		/* [2] Return type declared */
		/*********************************************************/
		return t;
	}
	public TEMP IRme()	{

		TEMP returnedValue;
		//allocate class object to contain vtable address and fields
		TEMP objectAddr = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRcommand_AllocateClassObject(objectAddr, objSize*4, this.className, isVtableEmpty));
		IR.getInstance().Add_IRcommand(new IRcommand_Prolog(objectAddr, OFFSET_TO_FRAME, STACK_OFFSET, false, true, null));
		IR.getInstance().Add_IRcommand(new IRcommand_CallFunction(this.className));
		returnedValue = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRcommand_Epilog(returnedValue, OFFSET_TO_FRAME, STACK_OFFSET));
		return objectAddr;
	}

}