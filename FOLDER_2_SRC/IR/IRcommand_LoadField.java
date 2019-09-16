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


//dont use it 

public class IRcommand_LoadField extends IRcommand
{
	int offset;
	boolean isRight;
	TEMP dst;
	TEMP object;
	
	public IRcommand_LoadField(TEMP dst,TEMP object, int offset,boolean isRight)
	{
		this.offset = offset;
		this.isRight = isRight;
		this.dst = dst;
		this.object = object;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		//contains object address
		if (isRight){
			sir_MIPS_a_lot.getInstance().load(dst,object, offset);
		}
		//left side - we want to get variable address into dst
		else{
			sir_MIPS_a_lot.getInstance().move(dst, object);
			sir_MIPS_a_lot.getInstance().addi(dst, offset);
		}
	}
}
