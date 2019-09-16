/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;

/**********************/
/* SYMBOL TABLE ENTRY */
/**********************/
public class SYMBOL_TABLE_ENTRY
{
	/*********/
	/* index */
	/*********/
	int index;
	public int offsetEntry = 0;
	public boolean isEntryParam = false;
	//needed when insude function scope but we dont have its name so we cant find it
	public TYPE_FUNCTION func = null;

	/********/
	/* name */
	/********/
	public String name;

	/******************/
	/* TYPE value ... */
	/******************/
	public TYPE type;

	/*********************************************/
	/* prevtop and next symbol table entries ... */
	/*********************************************/
	public SYMBOL_TABLE_ENTRY prevtop;
	public SYMBOL_TABLE_ENTRY next;

	/****************************************************/
	/* The prevtop_index is just for debug purposes ... */
	/****************************************************/
	public int prevtop_index;

	/******************/
	/* Scope Num ... */
	/******************/
	public int scopeCount;

	/******************/
	/* Scope Name ... */
	/******************/
	public String scopeName;


	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public SYMBOL_TABLE_ENTRY(
			String name,
			TYPE type,
			int index,
			SYMBOL_TABLE_ENTRY next,
			SYMBOL_TABLE_ENTRY prevtop,
			int prevtop_index, int scopeCount, String scopeName)
	{
		this.index = index;
		this.name = name;
		this.type = type;
		this.next = next;
		this.prevtop = prevtop;
		this.prevtop_index = prevtop_index;
		this.scopeCount = scopeCount;
		this.scopeName = scopeName;
	}
}
