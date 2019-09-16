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

public class IRcommand_CallClassFunction extends IRcommand
{
	public TEMP object;
	public int offset;
	
	public IRcommand_CallClassFunction(TEMP object, int offset)
	{
		this.object = object;
		this.offset = offset;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP virtualTable = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP func = TEMP_FACTORY.getInstance().getFreshTEMP();
		//load virtualTable address
		sir_MIPS_a_lot.getInstance().load(virtualTable, object, 0);
		//load function address
		sir_MIPS_a_lot.getInstance().load(func, virtualTable, offset);
		//call function
		sir_MIPS_a_lot.getInstance().jalr(func);
	}
}
