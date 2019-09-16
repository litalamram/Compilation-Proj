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

public class IRcommand_LoadFromFrame extends IRcommand
{
	TEMP dst;
	int offset;
	
	public IRcommand_LoadFromFrame(TEMP dst, int offset)
	{
		this.dst = dst;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().loadFromFrame(dst,offset);
		
	}
}
