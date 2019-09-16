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

public class IRcommand_StoreRegistersToFrame extends IRcommand
{

	
	public IRcommand_StoreRegistersToFrame()
	{

	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().storeMachineRegistersToFrame();
		sir_MIPS_a_lot.getInstance().storeRegisterToFrame("$ra",-32);
	}
}
