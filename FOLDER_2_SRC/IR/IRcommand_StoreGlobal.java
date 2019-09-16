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

public class IRcommand_StoreGlobal extends IRcommand
{
	TEMP src;
	int offset;
	
	public IRcommand_StoreGlobal(TEMP src,int offset)
	{
		this.src = src;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP globalsAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		sir_MIPS_a_lot.getInstance().storeGlobal(globalsAddress, src, offset);
	}
}
