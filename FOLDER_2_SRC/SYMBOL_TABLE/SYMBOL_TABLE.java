/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;

/****************/
/* SYMBOL TABLE */
/****************/
public class SYMBOL_TABLE
{
	// scope names
	public static String SCOPE_BOUNDARY = "SCOPE-BOUNDARY";
	public static String CLASS_SCOPE = "SCOPE-BOUNDARY-CLASS";
	public static String FUNCTION_SCOPE = "SCOPE-BOUNDARY-FUNCTION";
	public static String METHOD_SCOPE = "SCOPE-BOUNDARY-METHOD"; // function inside a class
	public static String IF_SCOPE = "SCOPE-BOUNDARY-IF";
	public static String WHILE_SCOPE = "SCOPE-BOUNDARY-WHILE";
	public static String GLOBAL_SCOPE = "SCOPE-BOUNDARY-GLOBAL";
	private int hashArraySize = 13;


	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	private SYMBOL_TABLE_ENTRY[] table = new SYMBOL_TABLE_ENTRY[hashArraySize];
	private SYMBOL_TABLE_ENTRY top;
	private int top_index = 0;
	private int scopeCount = 0;
	private String scopeName = GLOBAL_SCOPE;
	private boolean insideFunctionDefinition = false;
	private String currClassName = null;

	/**************************************************************/
	/* A very primitive hash function for exposition purposes ... */
	/**************************************************************/
	private int hash(String s)
	{
		if (s.charAt(0) == 'l') {return 1;}
		if (s.charAt(0) == 'm') {return 1;}
		if (s.charAt(0) == 'r') {return 3;}
		if (s.charAt(0) == 'i') {return 6;}
		if (s.charAt(0) == 'd') {return 6;}
		if (s.charAt(0) == 'k') {return 6;}
		if (s.charAt(0) == 'f') {return 6;}
		if (s.charAt(0) == 'S') {return 6;}
		return 12;
	}

	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public void enter(String name,TYPE t)
	{
		/*************************************************/
		/* [1] Compute the hash value for this new entry */
		/*************************************************/
		int hashValue = hash(name);

		/******************************************************************************/
		/* [2] Extract what will eventually be the next entry in the hashed position  */
		/*     NOTE: this entry can very well be null, but the behaviour is identical */
		/******************************************************************************/
		SYMBOL_TABLE_ENTRY next = table[hashValue];

		/**************************************************************************/
		/* [3] Prepare a new symbol table entry with name, type, next and prevtop */
		/**************************************************************************/
		SYMBOL_TABLE_ENTRY e = new SYMBOL_TABLE_ENTRY(name,t,hashValue,next,top,top_index++,scopeCount,scopeName);

		/**********************************************/
		/* [4] Update the top of the symbol table ... */
		/**********************************************/
		top = e;

		/****************************************/
		/* [5] Enter the new entry to the table */
		/****************************************/
		table[hashValue] = e;

		/**************************/
		/* [6] Print Symbol Table */
		/**************************/
		PrintMe();
	}

	/***********************************************/
	/* Find the inner-most scope element with name */
	/***********************************************/
	public TYPE find(String name)
	{
		SYMBOL_TABLE_ENTRY e;

		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				return e.type;
			}
		}

		return null;
	}
	/**********************************************/
	/* Find the inner-most scope entry with name */
	/*********************************************/
	public SYMBOL_TABLE_ENTRY findEntry(String name)
	{
		SYMBOL_TABLE_ENTRY e;

		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				return e;
			}
		}

		return null;
	}

	/***************************************************************************/
	/* begine scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/***************************************************************************/
	public void beginScope(String newScopeName)
	{
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to be ablt to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		this.scopeCount++;
		this.scopeName = newScopeName;
		if(this.scopeName.equals(FUNCTION_SCOPE)){
			 // TODO: what about methods?
			this.insideFunctionDefinition = true;
		}
		enter(SCOPE_BOUNDARY,
				new TYPE_FOR_SCOPE_BOUNDARIES(newScopeName,this.scopeCount));

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		PrintMe();
	}

	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	public void endScope()
	{
		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */
		/**************************************************************************/
		while (top.name != SCOPE_BOUNDARY)
		{
			table[top.index] = top.next;
			top_index = top_index-1;
			top = top.prevtop;
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */
		/**************************************/
		table[top.index] = top.next;
		if(top.scopeName.equals(FUNCTION_SCOPE)){
			// we removed function definition scope
			this.insideFunctionDefinition = false;
		}
		top_index = top_index-1;
		top = top.prevtop;
		this.scopeCount--;
		this.scopeName = top.scopeName;
		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		PrintMe();
	}

	public static int n=0;

	public void PrintMe()
	{
		int i=0;
		int j=0;
		String dirname="./FOLDER_5_OUTPUT/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i=0;i<hashArraySize-1;i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n",hashArraySize-1,hashArraySize-1);

			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			for (i=0;i<hashArraySize;i++)
			{
				if (table[i] != null)
				{
					/*****************************************************/
					/* [4a] Print hash table array[i] -> entry(i,0) edge */
					/*****************************************************/
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				}
				j=0;
				for (SYMBOL_TABLE_ENTRY it=table[i];it!=null;it=it.next)
				{
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
							it.name,
							it.type.name,
							it.prevtop_index);

					if (it.next != null)
					{
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
								"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
								i,j,i,j+1);
						fileWriter.format(
								"node_%d_%d:f3 -> node_%d_%d:f0;\n",
								i,j,i,j+1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static SYMBOL_TABLE instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SYMBOL_TABLE() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static SYMBOL_TABLE getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SYMBOL_TABLE();

			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enter(TYPE_INT.getInstance().name,		TYPE_INT.getInstance());
			instance.enter(TYPE_STRING.getInstance().name,	TYPE_STRING.getInstance());


			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			instance.enter(
					"PrintInt",
					new TYPE_FUNCTION(
							TYPE_VOID.getInstance(),
							"PrintInt",
							new TYPE_LIST(
									TYPE_INT.getInstance(),
									null),false,1,0));
			instance.enter(
					"PrintString",
					new TYPE_FUNCTION(
							TYPE_VOID.getInstance(),
							"PrintString",
							new TYPE_LIST(
									TYPE_STRING.getInstance(),
									null),false,1,0));
			instance.enter(
					"PrintTrace",
					new TYPE_FUNCTION(
							TYPE_VOID.getInstance(),
							"PrintTrace",
							null,false,0,0));
		}
		return instance;
	}

	public int getScopeCount(){
		return this.scopeCount;
	}

	public String getScopeName(){
		return this.scopeName;
	}
	public boolean getInsideFunctionDef(){
		return this.insideFunctionDefinition;
	}
	public boolean isInClassScope(){
		TYPE t = this.getInstance().find(CLASS_SCOPE);
		if(t != null) return true;
		return false;
	}
	public boolean isInFunctionScope(){
		TYPE t = this.getInstance().find(FUNCTION_SCOPE);
		if(t != null) return true;
		return false;
	}
	public String getClassName(){
		TYPE t = this.getInstance().find(CLASS_SCOPE);
		if(t == null) return null;
		return t.name;
	}

	public TYPE_CLASS getTypeClass(){
		TYPE_CLASS t = (TYPE_CLASS) this.getInstance().find(CLASS_SCOPE);
		if(t == null) return null;
		return t;
	}

	public boolean isNameTakenByClassOrArray(String name){

		TYPE t = this.getInstance().find(name);
		if(t!=null && (t.isArray() || t.isClass())){
			if(t.name.equals(name)){
			 return true;
			 }
		}
		return false;
	}
}
