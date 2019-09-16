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

public class IRcommand_If extends IRcommand
{
	TEMP res;
	
	public IRcommand_If(TEMP res)
	{
		this.res = res;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{

		/****************************************************/
		/* [3] Allocate a fresh label for possible overflow */
		/****************************************************/
		String label = getFreshLabel("endif");
		/********************************************************/
		/* [5] if (32767 <  t1_plus_t2) goto label_overflow;    */
		/*     if (32767 >= t1_plus_t2) goto label_no_overflow; */
		/********************************************************/
		sir_MIPS_a_lot.getInstance().beq(res,null,label,true);
		sir_MIPS_a_lot.getInstance().addLabelToStack(label,true);
	
	}
}
