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

public class IRcommand_LoadFieldAddress extends IRcommand
{
	int localVarOffset;
	boolean isRight;
	TEMP dst;
	TEMP object;
	
	public IRcommand_LoadFieldAddress(TEMP dst,TEMP object, int localVarOffset,boolean isRight)
	{
		this.localVarOffset = localVarOffset;
		this.isRight = isRight;
		this.object = object;
		this.dst = dst;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().loadFromStack(object,4);
		
		if (isRight){
				sir_MIPS_a_lot.getInstance().load(dst,object, this.localVarOffset);
			}
		//left side - we want to get variable address into temp t 
		else{
			sir_MIPS_a_lot.getInstance().addi(object, this.localVarOffset);
			sir_MIPS_a_lot.getInstance().move(dst,object);
			
		}
	}
}
