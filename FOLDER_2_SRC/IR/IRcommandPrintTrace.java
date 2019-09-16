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

public class IRcommandPrintTrace extends IRcommand
{
	TEMP t;
	
	public IRcommandPrintTrace(TEMP t)
	{
		this.t = t;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP curr_fp = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP curr_func_name = TEMP_FACTORY.getInstance().getFreshTEMP();

		sir_MIPS_a_lot.getInstance().loadFromFrame(curr_fp, 0);            //curr_fp = 0($fp)
		//while curr_fp != 0
		new IRcommand_AddLabel("While",false).MIPSme();
		new IRcommand_If(curr_fp).MIPSme();
		//while body
		sir_MIPS_a_lot.getInstance().load(curr_func_name, curr_fp, -4);    //curr_func_name = -4(curr_fp)
		sir_MIPS_a_lot.getInstance().print_string(curr_func_name);         //print func name
		sir_MIPS_a_lot.getInstance().load(curr_fp, curr_fp, 0);            //curr_fp = 0(curr_fp)
		new IRcommand_AddLabelFromStack(false).MIPSme();
		new IRcommand_AddLabelFromStack(true).MIPSme();
		//end while

		//print main
		sir_MIPS_a_lot.getInstance().printRuntimeErrorMsg("string_func_main");




	}
}
