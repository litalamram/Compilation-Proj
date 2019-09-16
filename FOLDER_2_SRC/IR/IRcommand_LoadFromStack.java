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

public class IRcommand_LoadFromStack extends IRcommand
{
	TEMP dst;
	int offset;
	
	public IRcommand_LoadFromStack(TEMP dst, int offset)
	{
		this.dst = dst;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().loadFromStack(dst,offset);
		
	}
}
