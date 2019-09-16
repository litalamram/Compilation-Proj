package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;


public class AST_DEC_FUNCDEC extends AST_DEC {

	String returnTypeName;
	String name;
	AST_TYPE_NAME_LIST params;
	AST_STMT_LIST body;
	TYPE_CLASS fatherClass;
	int returnTypeNameLine, nameLine;
	String className;
	public int paramsNum = 0;
	public int localVarNum = 0;
	public String funcLabelName = "";
	public boolean isOverride = false;
	public boolean isMethod = false;

	/******************/
	/* CONSTRUCTOR(S) */

	/******************/
	public AST_DEC_FUNCDEC(String returnTypeName, String name, AST_TYPE_NAME_LIST params, AST_STMT_LIST body, int returnTypeNameLine, int nameLine) {
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== funcDec -> ID(%s) ID(%s) (params) {sl} \n", returnTypeName, name);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.returnTypeName = returnTypeName;
		this.name = name;
		this.params = params;
		this.body = body;
		this.returnTypeNameLine = returnTypeNameLine;
		this.nameLine = nameLine;
		this.className = "";
		this.fatherClass = null;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */

	/*****************************************************/
	public void PrintMe() {
		/*************************************************/
		/* AST NODE TYPE = AST NODE FUNCTION DECLARATION */
		/*************************************************/
		System.out.format("FUNC(%s):%s\n", name, returnTypeName);

		/***************************************/
		/* RECURSIVELY PRINT params + body ... */
		/***************************************/
		if (params != null) params.PrintMe();
		if (body != null) body.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("FUNC(%s)\n:%s\n", name, returnTypeName));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (params != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, params.SerialNumber);
		if (body != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, body.SerialNumber);
	}

	public TYPE SemantSignature(){
		TYPE returnType = null;
		TYPE_FUNCTION func, fatherFunc = null;
		TYPE_LIST funcParams = null;
		SYMBOL_TABLE_ENTRY nameFuncType = null;
		TYPE_VAR_DEC idVar;
		boolean checkForOverloading = false;
		isMethod = SYMBOL_TABLE.getInstance().isInClassScope();

		// functions can be defined only inside class or in global scope
		if (SYMBOL_TABLE.getInstance().getScopeCount() != 0 && !(SYMBOL_TABLE.getInstance().isInClassScope())) {
			System.out.format("%s\n", "Error from : AST_DEC_FUNCDEC");
			System.out.format(">> ERROR [%d] cannot declare function not in global scope and not inside a class %s\n", this.line, name);
			throw new MyRunTimeException(this.line);
		}

		/*******************/
		/* [1] return type */
		/*******************/

		// checking the returned type
		if (!returnTypeName.equals("void")) {
			returnType = SYMBOL_TABLE.getInstance().find(returnTypeName);
			if (returnType == null) {
				System.out.format("Error from : AST_DEC_FUNCDEC");
				System.out.format(">> ERROR [%d] non existing return type\n", this.returnTypeNameLine);
				throw new MyRunTimeException(this.returnTypeNameLine);
			}
			//check the type is one of: class,array,int,string
			if(!returnType.isClass() &&
					!returnType.isArray() &&
					!returnType.isEquals(TYPE_INT.getInstance()) &&
					!returnType.isEquals(TYPE_STRING.getInstance())
			) {
				System.out.format("%s","ERROR FROM AST_DEC_FUNCDEC");
				System.out.format(">> ERROR [%d]  %s is not a type\n", this.returnTypeNameLine, returnTypeName);
				throw new MyRunTimeException(this.returnTypeNameLine);
			}
		} else {
			returnType = TYPE_VOID.getInstance();
		}

		// checking reserved words
		if (AST_Node.isReservedWord(name)) {
			System.out.format("%s\n", "Error from : AST_DEC_FUNCDEC");
			System.out.format(">> ERROR [%d] cannot declare function with reserved name %s\n", this.nameLine, name);
			throw new MyRunTimeException(this.nameLine);
		}
		// checking name
		System.out.format("function name is %s\n", name);
		nameFuncType = SYMBOL_TABLE.getInstance().findEntry(name);
		if (nameFuncType != null) {
			System.out.format("name found is %s\n", nameFuncType);
			System.out.format("scope num of func is %d, scope num of func found is %d\n", SYMBOL_TABLE.getInstance().getScopeCount(), nameFuncType.scopeCount);
			// scope check - same name same scope - forbidden
			if (nameFuncType.scopeCount == SYMBOL_TABLE.getInstance().getScopeCount()) {
				System.out.format("Error from : AST_DEC_FUNCDEC");
				System.out.format(">> ERROR [%d] cannot declare same function name %s since already taken\n", this.nameLine, name);
				throw new MyRunTimeException(this.nameLine);
			}

		}


		/****************************/
		/* [3] Begin Function Scope */
		/****************************/
		SYMBOL_TABLE.getInstance().beginScope(SYMBOL_TABLE.FUNCTION_SCOPE);
		SYMBOL_TABLE.getInstance().enter(SYMBOL_TABLE.getInstance().FUNCTION_SCOPE, returnType);

		// checking for overriding and overloading
		if (SYMBOL_TABLE.getInstance().isInClassScope()) {
			// Need to check for overriding
			if (this.fatherClass != null) {
				// get method by name from father
				fatherFunc = fatherClass.doesAncestorsHaveMethod(name);
				if (fatherFunc != null) {
					// method exist in father's class
					// checking returned type match
					if (!fatherFunc.returnType.name.equals(this.returnTypeName)) {
						System.out.format("%s\n", "Error from : AST_DEC_FUNCDEC");
						System.out.format(">> ERROR [%d] cannot overload function %s with different type %s instead of %s\n", this.nameLine, this.returnTypeName, this.name, fatherFunc.returnType.name);
						throw new MyRunTimeException(this.nameLine);
					}
					// returned type and name matches. need to check for arguments later on
					checkForOverloading = true;
				}
				//check if there is a field in father class with same name
				if (fatherClass.getInheritedFieldType(this.name) != null) {
					System.out.format("%s\n", "Error from : AST_DEC_FUNCDEC");
					System.out.format(">> ERROR [%d] variable %s already exists in father class\n", this.nameLine, this.name);
					throw new MyRunTimeException(this.nameLine);
				}
			}
		}

		/***************************/
		/* [2] Semant Input Params */
		/***************************/
		if (this.params != null) {
			//funcParams = params.SemantMe();
			for (AST_TYPE_NAME_LIST it = this.params; it  != null; it = it.tail) {
				it.head.offset = paramsNum;
				idVar = (TYPE_VAR_DEC)it.head.SemantMe();
				paramsNum++;
				if (funcParams == null){
					funcParams = new TYPE_LIST(idVar, null);
				}
				else {
					funcParams.addElement(idVar);
				}
			}

			if (checkForOverloading) {
				TYPE_LIST fatherParams = fatherFunc.params;
				if (fatherParams == null) {
					System.out.format("Error from : AST_DEC_FUNCDEC");
					System.out.format(">> ERROR [%d] cannot override function %s with no params with function contain params\n", params.line, name);
					throw new MyRunTimeException(params.line);
				}
				else {
					AST_TYPE_NAME_LIST it = params;
					TYPE_LIST it1 = funcParams, it2 = fatherParams;
					while (it!=null && it1!=null && it2!=null){
						TYPE t1 = it1.head.getTypeToBeCompared();
						TYPE t2 = it2.head.getTypeToBeCompared();
						if(!t1.isEquals(t2)) {
							System.out.format("Error from : AST_DEC_FUNCDEC");
							System.out.format(">> ERROR [%d] cannot override function %s : mismatch arguments [%s,%s]\n", it.line, name, t1.name, t2.name);
							throw new MyRunTimeException(it.line);
						}
						it = it.tail; it1 = it1.tail; it2 = it2.tail;

					}
					if (it1 != null && it2 == null){
						System.out.format("Error from : AST_DEC_FUNCDEC");
						System.out.format(">> ERROR [%d] cannot override function %s : too much arguments supplied\n", it.line, it.line, name);
						throw new MyRunTimeException(it.line);
					}
					if (it1 == null && it2 != null){
						System.out.format("Error from : AST_DEC_FUNCDEC");
						System.out.format(">> ERROR [%d] cannot override function %s : too few arguments supplied\n", it.line, it.line, name);
						throw new MyRunTimeException(params.line);
					}
				}

			}
		}
		else{
			//need to check that in father also params is empty
			if(checkForOverloading && fatherFunc.params!=null){
				System.out.format("Error from : AST_DEC_FUNCDEC");
				System.out.format(">> ERROR [%d] cannot overload function %s with different params\n", this.line, name);
				throw new MyRunTimeException(this.line);
			}
		}



		/*****************/
		/* [4] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();

		/***************************************************/
		/* [5] Enter the Function Type to the Symbol Table */
		/***************************************************/
		this.funcLabelName = IRcommand.getFreshLabel(name+"_"+this.className);
		func = new TYPE_FUNCTION(returnType,name,funcParams,isMethod,paramsNum,localVarNum);
		func.labelName = this.funcLabelName;
		if (checkForOverloading){
			func.isOverride = true;
			this.isOverride = true;
			func.offset = fatherFunc.offset;
		}
		SYMBOL_TABLE.getInstance().enter(name, func);


		/***********************************/
		/* [6] Return value is irrelevant */
		/**********************************/
		return func;

	}

	public TYPE SemantMe() {
		TYPE_FUNCTION func;
		TYPE t, returnType;
		boolean isMethod = SYMBOL_TABLE.getInstance().isInClassScope();
		SYMBOL_TABLE_ENTRY e = null;

		//semant signature
		if (!isMethod){
			this.SemantSignature();
		}

		func = (TYPE_FUNCTION)SYMBOL_TABLE.getInstance().find(name);

		/****************************/
		/* [1] Begin Function Scope */
		/****************************/
		if (!returnTypeName.equals("void")) {
			returnType = SYMBOL_TABLE.getInstance().find(returnTypeName);
		} else {
			returnType = TYPE_VOID.getInstance();
		}
		SYMBOL_TABLE.getInstance().beginScope(SYMBOL_TABLE.FUNCTION_SCOPE);
		SYMBOL_TABLE.getInstance().enter(SYMBOL_TABLE.getInstance().FUNCTION_SCOPE, returnType);
		SYMBOL_TABLE.getInstance().enter(name, func);


		/************************************/
		/* [2] Insert Params to symbol table*/
		/***********************************/
		if (this.params != null) {
			//funcParams = params.SemantMe();
			for (AST_TYPE_NAME_LIST it = this.params; it != null; it = it.tail) {
				it.head.SemantMe();
			}
		}
		

		/*******************/
		/* [3] Semant Body */
		/*******************/
		body.funcParamNum = this.paramsNum;
		body.functionName = this.name;
		body.SemantMe();
		this.localVarNum = (body.offset+1);
		func.localVarNum = this.localVarNum;
		e = SYMBOL_TABLE.getInstance().findEntry(SYMBOL_TABLE.getInstance().FUNCTION_SCOPE);
		e.func = func;


		/*****************/
		/* [4] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();

		/*********************************************************/
		/* [5] Return value is irrelevant for class declarations */
		/*********************************************************/
		return func;
	}

	public TEMP IRme()
	{
		int stackPointerOffset;
		//add function name
		if (!sir_MIPS_a_lot.funcNames.contains("string_func_" + name + ": .asciiz \"" + name + "\"\n")) {
			sir_MIPS_a_lot.funcNames += ("string_func_" + name + ": .asciiz \"" + name + "\"\n");
		}
		// adding function label
		if(!name.equals("main") || isMethod){
			IR.getInstance().Add_IRcommand(new IRcommand_Jump(this.funcLabelName+"_end", false));
			IR.getInstance().Add_IRcommand(new IRcommand_AddFixedLabel(this.funcLabelName));
		}
		else{
			IR.getInstance().Add_IRcommand(new IRcommand_Jump("Label_0_main_end", false));
			IR.getInstance().Add_IRcommand(new IRcommand_AddFixedLabel("Label_0_main"));

			// move stack pointer according to num of local vars
			stackPointerOffset = (this.localVarNum+1+1)*4;
			IR.getInstance().Add_IRcommand(new IRcommand_Increase_StackPointer(-stackPointerOffset));
		}
		if (body != null){
			body.IRme();
		}

		if(!name.equals("main") || isMethod){
			IR.getInstance().Add_IRcommand(new IRcommand_Jump("$ra",true));
			IR.getInstance().Add_IRcommand(new IRcommand_AddFixedLabel(this.funcLabelName+"_end"));
		}
		else{
			// exiting after main
			IR.getInstance().Add_IRcommand(new IRcommand_ExitProgram());
			IR.getInstance().Add_IRcommand(new IRcommand_AddFixedLabel("Label_0_main_end"));
		}
		return null;
	}
}
