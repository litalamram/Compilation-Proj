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

public class IRcommand_IsClassRefLegal extends IRcommand
{
	TEMP object;
	String label;
	boolean isRight;
	
	public IRcommand_IsClassRefLegal(TEMP object, String label, boolean isRight)
	{
		this.object = object;
		this.label = label;
		this.isRight = isRight;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{	
		if (!isRight){
			sir_MIPS_a_lot.getInstance().load(object,object, 0);
		}
		//check if object is uninitialized (null)
		sir_MIPS_a_lot.getInstance().beq(object,null,label,true);
	}
}
