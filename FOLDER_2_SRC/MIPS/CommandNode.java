/***********/
/* PACKAGE */
/***********/
package MIPS;


/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

import java.util.*;

public class CommandNode
{
	/*****************/
	/* Class Fields  */
	/*****************/
	

	int commandSerialNum;
	boolean isLabelCommand;
	String labelName;
	boolean isTerminatJumpCommand;
	boolean isJumpLabelCommand;
	String labelJumpName;
	HashSet<Integer> def;
	HashSet<Integer> use;
	HashSet<Integer> liveIn;
	HashSet<Integer> liveInTag;
	HashSet<Integer> liveOut;
	HashSet<Integer> liveOutTag;
	
	CommandNode[] children;
	
	public CommandNode(int commandSerialNum, boolean isLabelCommand, String labelName,
			boolean isTerminatJumpCommand, boolean isJumpLabelCommand, String labelJumpName,
			int firtsDef, int firstUse, int secondUse){
		// ----- command info -----
		this.commandSerialNum = commandSerialNum;
		this.isLabelCommand = isLabelCommand;
		this.labelName = labelName;
		this.isTerminatJumpCommand = isTerminatJumpCommand;
		this.isJumpLabelCommand = isJumpLabelCommand;
		this.labelJumpName = labelJumpName;
		// ----- command sets -----
		this.def = new HashSet<>();
		this.use = new HashSet<>();
		if(firtsDef != -1)
			this.def.add(firtsDef);
		if(firstUse != -1)
			this.use.add(firstUse);
		if(secondUse != -1)
			this.use.add(secondUse);
		
		// -- liveness sets
		this.liveIn = new HashSet<>();
		this.liveInTag = new HashSet<>();
		this.liveOut = new HashSet<>();
		this.liveOutTag = new HashSet<>();
		
		// ----- childern
		this.children = new CommandNode[2];	
		
	}
	
	public void printCommand(){
		System.out.format("******** command number:%d ********\n",this.commandSerialNum);
		System.out.format("******** command def:{ ");
		printHashSet(this.def);
		System.out.format(" } ********\n");
		System.out.format("******** command use:{ ");
		printHashSet(this.use);
		System.out.format(" } ********\n");
		System.out.format("******** command children :{ ");
		if(this.children[0] != null){
			System.out.format("%d,",this.children[0].commandSerialNum);
			if(this.children[1] != null){
				System.out.format("%d,",this.children[1].commandSerialNum);
			}
		}
		else if(this.children[1] != null)
			System.out.format("******** first child is null and second not !!!!!!  ");
		System.out.format(" } ********\n");
		
		if(isLabelCommand){
			System.out.format("Label command: %s", this.labelName);
		}
		
		if(isJumpLabelCommand){
			System.out.format("Label jump command: %s", this.labelJumpName);
		}
	}
	
	public void printHashSet(HashSet<Integer> set){
		for(Integer i:set){
			System.out.format("%d,",i);
		}			
	}
	
	public void printCommandLive(){
		if(this.commandSerialNum < 250 && this.commandSerialNum > 0){
			System.out.format("******** command live number:%d ********\n",this.commandSerialNum);
			if(!(this.liveIn.isEmpty())){
				System.out.format("******** command liveIn:{ ");
				printHashSet(this.liveIn);
				System.out.format(" } ********\n");
			}

			if(!(this.liveInTag.isEmpty())){
				System.out.format("******** command liveInTag:{ ");
				printHashSet(this.liveInTag);
				System.out.format(" } ********\n");
			}

			if(!(this.liveOut.isEmpty())){
				System.out.format("******** command liveOut:{ ");
				printHashSet(this.liveOut);
				System.out.format(" } ********\n");
			}

			if(!(this.liveOutTag.isEmpty())){
				System.out.format("******** command liveOutTag:{ ");
				printHashSet(this.liveOutTag);
				System.out.format(" } ********\n");
			}	
		}
	



	}
}
