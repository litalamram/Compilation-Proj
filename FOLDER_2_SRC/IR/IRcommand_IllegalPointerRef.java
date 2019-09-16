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

public class IRcommand_IllegalPointerRef extends IRcommand
{
	String label;
	
	public IRcommand_IllegalPointerRef(String label)
	{
		this.label = label;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{	
		
		String label_illegal_pointer_ref_end = getFreshLabel("legal_pointer_ref_end");
		
		sir_MIPS_a_lot.getInstance().jump(label_illegal_pointer_ref_end);

		//if illegal
		sir_MIPS_a_lot.getInstance().label(label);
		sir_MIPS_a_lot.getInstance().printRuntimeErrorMsg("string_invalid_ptr_dref");
		sir_MIPS_a_lot.getInstance().exitProgram();
		sir_MIPS_a_lot.getInstance().label(label_illegal_pointer_ref_end);
	}
}
