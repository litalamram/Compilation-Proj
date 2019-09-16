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

public class IRcommand_Binop_EQ_Integers extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;

	public IRcommand_Binop_EQ_Integers(TEMP dst,TEMP t1,TEMP t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String label_end        = getFreshLabel("end");
		String label_set_to_one  = getFreshLabel("set_to_one");
		String label_set_to_zero = getFreshLabel("set_to_zero");
		
		/******************************************/
		/* [2] if (t1 == t2) goto label_set_to_one;  */
		/*     else goto label_set_to_zero; */
		/******************************************/
		sir_MIPS_a_lot.getInstance().beq(t1,t2,label_set_to_one,false);
		sir_MIPS_a_lot.getInstance().bne(t1,t2,label_set_to_zero);

		/************************/
		/* [3] label_set_to_one: */
		/*                      */
		/*         t3 := 1      */
		/*         goto end;    */
		/*                      */
		/************************/
		sir_MIPS_a_lot.getInstance().label(label_set_to_one);
		sir_MIPS_a_lot.getInstance().li(dst,1);
		sir_MIPS_a_lot.getInstance().jump(label_end);

		/*************************/
		/* [4] label_set_to_zero: */
		/*                       */
		/*         t3 := 1       */
		/*         goto end;     */
		/*                       */
		/*************************/
		sir_MIPS_a_lot.getInstance().label(label_set_to_zero);
		sir_MIPS_a_lot.getInstance().li(dst,0);
		sir_MIPS_a_lot.getInstance().jump(label_end);

		/******************/
		/* [5] label_end: */
		/******************/
		sir_MIPS_a_lot.getInstance().label(label_end);
	}
}
