package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_DEC_CLASSDEC extends AST_DEC{

	String name;
	AST_CFIELD_LIST cFieldList;
	public String extendsClass;
	public int nameLine;
	public int extendsClassLine;
	TYPE_CLASS thisClass;


	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_CLASSDEC(String name, String extendsClass, AST_CFIELD_LIST cfieldList, int nameLine, int extendsClassLine)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (extendsClass == null)
			System.out.format("====================== classDec -> class ID(%s) {cField List} \n", name);
		else
			System.out.format("====================== classDec -> class ID(%s) extends ID(%s) {cField List} \n", name, extendsClass);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
		this.extendsClass = extendsClass;
		this.cFieldList = cfieldList;
		this.nameLine = nameLine;
		this.extendsClassLine = extendsClassLine;

	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE CLASSSDEC CLASS\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSRIPT ... */
		/****************************************/
		System.out.println(name);
		if(extendsClass != null) System.out.println(extendsClass);
		if(this.cFieldList != null) cFieldList.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (extendsClass != null)
			AST_GRAPHVIZ.getInstance().logNode(
					SerialNumber,
					String.format("CLASSDEC\nclass %s extends %s {cFieldList}\n", name, extendsClass));
		else
			AST_GRAPHVIZ.getInstance().logNode(
					SerialNumber,
					String.format("CLASSDEC\nclass %s {cFieldList}\n", name));
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (this.cFieldList != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cFieldList.SerialNumber);

	}

	public TYPE SemantMe()
	{
		TYPE cFiledType, extendsClassType = null;
		TYPE_LIST dataMembers, methodsList, dataMem = null, funcMember = null;
		TYPE_CLASS newClass, extendsClassNew = null;
		AST_CFIELD first;
		String className;
		TYPE_FUNCTION functType;
		TYPE_VAR_DEC dataType;

		boolean isErr = false;
		int errLine = -1;
		/**************************************************/
		/* [1] Class can be defined only in global scope */
		/************************************************/
		int scopeCount = SYMBOL_TABLE.getInstance().getScopeCount();
		// classes are defined only in the upper most scope
		if(scopeCount != 0){
			System.out.format("%s","Error from : AST_DEC_CLASSDEC");
			System.out.format(">> ERROR [%d] cannot define class %s in non global scope\n",this.line, name);
			throw new MyRunTimeException(this.line);
		}
		/*************************/
		/* [4] Begin Class Scope */
		/*************************/
		SYMBOL_TABLE.getInstance().beginScope(SYMBOL_TABLE.CLASS_SCOPE);
		/**************************************/
		/* [2] Check That Name is not reserved name */
		/**************************************/
		if(AST_Node.isReservedWord(name)){
			System.out.format("%s\n","Error from : AST_DEC_CLASSDEC");
			System.out.format(">> ERROR [%d] cannot declar class name of reserved name %s\n",this.nameLine, name);
			throw new MyRunTimeException(this.nameLine);
		}

		/**************************************/
		/* [2] Check That Name does NOT exist */
		/**************************************/
		if (SYMBOL_TABLE.getInstance().find(name) != null)
		{
			System.out.format("%s","Error from : AST_DEC_CLASSDEC");
			System.out.format(">> ERROR [%d] class %s already exists\n",this.nameLine, name);
			throw new MyRunTimeException(this.nameLine);
		}

		/*************************************************/
		/* [3] Check If extendsClass was previously defined */
		/***********************************************/
		if ( this.extendsClass != null){
			extendsClassType = SYMBOL_TABLE.getInstance().find(this.extendsClass);
			if (extendsClassType == null)
			{
				System.out.format("%s","Error from : AST_DEC_CLASSDEC");
				System.out.format(">> ERROR [%d] cannot extend non existing class %s\n",this.extendsClassLine, extendsClass);
				throw new MyRunTimeException(this.extendsClassLine);
			}
			if (!extendsClassType.isClass()){
				System.out.format("%s","Error from : AST_DEC_CLASSDEC");
				System.out.format(">> ERROR [%d] cannot extend non class variable %s\n",this.extendsClassLine, extendsClass);
				throw new MyRunTimeException(this.extendsClassLine);
			}
		}
		extendsClassNew = (TYPE_CLASS) extendsClassType;
		newClass = new TYPE_CLASS(extendsClassNew, name ,null ,null);
		SYMBOL_TABLE.getInstance().enter(SYMBOL_TABLE.CLASS_SCOPE, newClass);
		SYMBOL_TABLE.getInstance().enter(name, newClass);

		if (extendsClassNew != null){
			newClass.fieldNum = extendsClassNew.fieldNum;
			newClass.funcNum = extendsClassNew.funcNum;
		}

		// Get data members and methods

		// Semant dataMembers
		for (AST_CFIELD_LIST it = this.cFieldList; it != null; it = it.tail)
		{
			first = (AST_CFIELD)it.head;
			first.fatherClass = extendsClassNew;
			className = first.getClass().getSimpleName();
			if (first instanceof AST_CFIELD_VARDEC){//(className.equals("AST_CFIELD_VARDEC")){

				try {
					//set field offset
					((AST_CFIELD_VARDEC)first).offset = newClass.fieldNum;
					//semant
					dataType = (TYPE_VAR_DEC) first.SemantMe();
					if(dataType.t.name.equals(name)){
						// if the var is a class type variable
						dataType = new TYPE_VAR_DEC(dataType.name,newClass);
					}
					SYMBOL_TABLE.getInstance().enter(dataType.name,dataType);
					//update field offset
					dataType.offset = newClass.fieldNum;
					dataType.dataMem = it.head;
					// updating dataMem list
					if(dataMem == null){
						dataMem = new TYPE_LIST(dataType,dataMem);
						newClass.data_members = dataMem;
					}
					else{
						dataMem.addElement(dataType);
					}
					newClass.fieldNum++;

				} catch(MyRunTimeException e){
					if (!isErr || e.row < errLine)
						errLine = e.row;
					isErr = true;

				}
			}
		}

		// Methods
		for (AST_CFIELD_LIST it=this.cFieldList ;it != null; it=it.tail)
		{
			first = (AST_CFIELD)it.head;
			first.fatherClass = extendsClassNew;
			first.className = name;
			className= first.getClass().getSimpleName();
			if (first instanceof  AST_CFIELD_FUNCDEC)//(className.equals("AST_CFIELD_FUNCDEC"))
			{
				try{
					functType = (TYPE_FUNCTION)((AST_CFIELD_FUNCDEC)first).SemantSignature();
					if(funcMember == null){
						funcMember =  new TYPE_LIST(functType, funcMember);
						newClass.class_methods = funcMember;
					}
					else{
						funcMember.addElement(functType);
					}
					//if the function isn't inheritted
					if (!functType.isOverride){
						functType.offset = newClass.funcNum;
						newClass.funcNum++;
					}
				}catch(MyRunTimeException e){
					if (!isErr || e.row < errLine)
						errLine = e.row;
					isErr = true;
					if (SYMBOL_TABLE.getInstance().isInFunctionScope()) {
						//end func scope
						SYMBOL_TABLE.getInstance().endScope();
					}
					((AST_CFIELD_FUNCDEC)first).errSemantSignature = true;
				}
			}
		}

		for (AST_CFIELD_LIST it=this.cFieldList ;it != null; it=it.tail)
		{
			first = (AST_CFIELD)it.head;
			first.fatherClass = extendsClassNew;
			className = first.getClass().getSimpleName();
			if (first instanceof  AST_CFIELD_FUNCDEC)//(className.equals("AST_CFIELD_FUNCDEC"))
			{
				if (!((AST_CFIELD_FUNCDEC)first).errSemantSignature) {
					try {
						((AST_CFIELD_FUNCDEC) first).SemantBody();
					} catch (MyRunTimeException e) {
						if (!isErr || e.row < errLine) {
							errLine = e.row;
						}
						System.out.format(">> ERROR [%d] from AST_DEC_CLASSDEC", errLine);
						throw new MyRunTimeException(errLine);
					}
				}
			}
		}

		if (isErr){
			System.out.format(">> ERROR [%d] from AST_DEC_CLASSDEC", errLine);
			throw new MyRunTimeException(errLine);
		}

		//construct vtable
		if(newClass.funcNum != 0){
			newClass.methodsLabels = new String[newClass.funcNum];
			newClass.createMethodsLabels();
			sir_MIPS_a_lot.vtables += (name + "_vtable: .word " + newClass.methodsLabelsToString());
		}
		this.thisClass = newClass;


		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();


		/************************************************/
		/* [4] Enter the Class Type to the Symbol Table */
		/************************************************/
		SYMBOL_TABLE.getInstance().enter(name, newClass);

		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		if(newClass.father != null)
			System.out.format(" ****** name: %s name extendsClass: %s \n", newClass.name, newClass.father.name);
		return newClass;

	}

	public TEMP IRme()
	{
		AST_CFIELD first;
		String className;
		IR.getInstance().Add_IRcommand(new IRcommand_Jump(name+"_end", false));
		IR.getInstance().Add_IRcommand(new IRcommand_AddFixedLabel(name));
		TYPE_VAR_DEC firstArg;
		if (this.cFieldList != null){
			for (TYPE_LIST it=this.thisClass.data_members;it != null;it=it.tail)
			{
				firstArg = (TYPE_VAR_DEC)it.head;
				firstArg.dataMem.IRme();
			}
			TYPE_CLASS fatherClass = thisClass.father;
			//construct inherited fields
			while (fatherClass != null){
				if (this.cFieldList != null){
					for (TYPE_LIST it=fatherClass.data_members;it != null;it=it.tail)
					{
						firstArg = (TYPE_VAR_DEC)it.head;
						firstArg.dataMem.IRme();
					}
				}
				fatherClass = fatherClass.father;
			}

			IR.getInstance().Add_IRcommand(new IRcommand_Jump("$ra", true));
			IR.getInstance().Add_IRcommand(new IRcommand_AddFixedLabel(name+"_end"));

			//IR Methods
			for (AST_CFIELD_LIST it=this.cFieldList;it != null;it=it.tail)
			{
				first = (AST_CFIELD)it.head;
				className = first.getClass().getSimpleName();
				if (className.equals("AST_CFIELD_FUNCDEC"))
					first.IRme();
			}
		}
		return null;
	}
}
