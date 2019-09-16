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

public class IRcommand_LoadGlobalVar extends IRcommand
{
	int globalVarOffset;
	boolean isRight;
	TEMP dst;
	
	public IRcommand_LoadGlobalVar(TEMP dst,int globalVarOffset,boolean isRight)
	{
		this.globalVarOffset = globalVarOffset;
		this.isRight = isRight;
		this.dst = dst;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP adr = TEMP_FACTORY.getInstance().getFreshTEMP();
		//v := v - this is the right side of the assignment - we want to load the value of v from memory
		if (isRight){
				sir_MIPS_a_lot.getInstance().loadGlobal(dst,adr,this.globalVarOffset*4);
		}
		
		//left side - we want to get variable address into temp t 
		else{			
			sir_MIPS_a_lot.getInstance().loadGlobalAddress(dst, this.globalVarOffset*4);
		}
	}
}
