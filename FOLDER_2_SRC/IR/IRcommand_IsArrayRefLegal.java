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

public class IRcommand_IsArrayRefLegal extends IRcommand
{
	TEMP object;
	String label;
	boolean isRight;
	
	public IRcommand_IsArrayRefLegal(TEMP object, String label, boolean isRight)
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
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		if (!isRight){
			sir_MIPS_a_lot.getInstance().load(t,object, 0);
			sir_MIPS_a_lot.getInstance().beq(t,null,label,true);

		}
		else {
			sir_MIPS_a_lot.getInstance().beq(object, null, label, true);
		}
	}
}
