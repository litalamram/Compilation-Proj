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

public class IRcommand_StoreOldFpToStack extends IRcommand
{
	String reg;
	int offset;
	
	public IRcommand_StoreOldFpToStack(String reg, int offset)
	{
		this.reg = reg;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().storeOldFpToStack(reg,offset);
	}
}
