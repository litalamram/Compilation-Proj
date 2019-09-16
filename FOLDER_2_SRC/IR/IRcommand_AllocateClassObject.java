/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;

public class IRcommand_AllocateClassObject extends IRcommand
{
	int size;
	String className;
	TEMP objectAddress;
	boolean virtualTableEmpty;
	
	public IRcommand_AllocateClassObject(TEMP objectAddress,int size, String name, boolean virtualTableEmpty)
	{
		this.size = size;
		this.objectAddress = objectAddress;
		this.className = name;
		this.virtualTableEmpty = virtualTableEmpty;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP virtualTable = TEMP_FACTORY.getInstance().getFreshTEMP();
		//allocate object size in heap
		sir_MIPS_a_lot.getInstance().allocateStr(objectAddress, size);
		
		//save virtualTable address in first cell if existed
		if(!virtualTableEmpty){
			sir_MIPS_a_lot.getInstance().la(virtualTable, className+"_vtable");
			sir_MIPS_a_lot.getInstance().store(objectAddress, virtualTable,0);
		}
	}
}
