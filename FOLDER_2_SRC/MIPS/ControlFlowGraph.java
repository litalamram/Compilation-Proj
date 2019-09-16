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
import java.lang.*;

public class ControlFlowGraph
{
	/*****************/
	/* Class Fields  */
	/*****************/
	
	ArrayList<CommandNode> commandsFromFile;
	ArrayList<ArrayList<CommandNode>> controlGraph;
	CommandNode lastCommandNode;
	CommandNode firstCommandNode;
	public static int serialNumber = 0;
	
	public ControlFlowGraph(){
		this.commandsFromFile = new ArrayList<>();
		this.controlGraph = new ArrayList<ArrayList<CommandNode>>();
		this.firstCommandNode = null;
		this.lastCommandNode = null;
	}
	
	
	public void addCommand(CommandNode command){
		if(this.commandsFromFile.isEmpty()){
			// the list is empty
			commandsFromFile.add(command);
			this.firstCommandNode = command;
			this.lastCommandNode = command;
			return;
		}
		commandsFromFile.add(command);
		// connecting last command to current command
		this.lastCommandNode.children[0] = command; 
		// updating lastCommand
		this.lastCommandNode = command;
		return;	
	}
	
	public void createControlGraph(){
		// add last connectivitey graph to the graph
		if(!(this.commandsFromFile.isEmpty())){
			// creates a new connectivity graph
			controlGraph.add(this.commandsFromFile);
			this.commandsFromFile = null;
			this.firstCommandNode = null;
			this.lastCommandNode = null;
		}
		// need to add edges for labels Jumps
		for(ArrayList<CommandNode> graph:this.controlGraph){
			for(CommandNode command:graph){
				if(command.isJumpLabelCommand){
					command.children[1] = getCommandLabelNode(command.labelJumpName,graph);
				}
				else if(command.isLabelCommand){
					if(command.labelName.contains("illegal_array_index") || command.labelName.contains("division_by_zero")
							|| command.labelName.contains("illegal_pointer_ref"))
					
					
					command.children[0] = null;
					command.children[1] = null;
				}
				else if(command.isTerminatJumpCommand){
					command.children[0] = null;
					command.children[1] = null;
				}
				else command.children[1] = null;
			}
		}		
	}
	
	public CommandNode getCommandLabelNode(String labelName,ArrayList<CommandNode> graph){
		for(CommandNode command:graph){
			if(command.isLabelCommand && command.labelName.equals(labelName)){
				return command;
			}
		}
		return null; // should never get here
	}
	
	public void calcLiveness(){
		for(ArrayList<CommandNode> graph:this.controlGraph)
			liveness(graph);
	}
	
	public void liveness(ArrayList<CommandNode> controlGraph){
		int counter = 0;
		int did_not_reach_fix_point;
		do
		{
			counter++;
			did_not_reach_fix_point = 0;
			/*******************/
			/* [1] Make a step */
			/*******************/
			for(CommandNode command:controlGraph)
			{
				
				//command.printCommandLive();
//				if(command.def.contains(159) || command.commandSerialNum == 219){
//					command.printCommand();
//					command.printCommandLive();
//				}

				
				command.liveInTag.addAll(command.liveOut);
				command.liveInTag.removeAll(command.def);
				command.liveInTag.addAll(command.use);
				//command.printCommandLive();
				//live__in_tag[command] := use[command] U (live_out[command] - def[command]);
				
			}
			


			/***************************************************************/
			/* [2] Propogate the effect of [1] to related nodes in the CFG */
			/***************************************************************/
			for (CommandNode command:controlGraph)
			{
				if(command.children[0] != null){
					// update only if the variables lives after this command
					command.liveOutTag.addAll(command.children[0].liveInTag);
					if(command.children[1] != null){
						command.liveOutTag.addAll(command.children[1].liveInTag);
//						if(command.isJumpLabelCommand){
//							if(command.labelJumpName.contains("While")){
//								command.liveOutTag.addAll(command.children[1].liveInTag);
//							}
//						}
						
					}
						
						//live_out_tag[command] := U_{s in successor[n]} live__in_tag[s]
				}			

			}

			/************************************/
			/* [3] check if fix point was found */
			/************************************/
			for (CommandNode command:controlGraph)
			{
				//command.printCommandLive();
				if(notEqualsLists(command.liveIn, command.liveInTag))
					did_not_reach_fix_point = 1;
				else if(notEqualsLists(command.liveOut, command.liveOutTag))
					did_not_reach_fix_point = 1;
				
				//if (live__in[command] != live__in_tag[command]) did_not_reach_fix_point = 1;
				//if (live_out[command] != live_out_tag[command]) did_not_reach_fix_point = 1;
			}
			
			/***********************/
			/* [4] sigma := sigma' */
			/***********************/
			for (CommandNode command:controlGraph)
			{
				command.liveIn = (HashSet)command.liveInTag.clone();
				command.liveOut = (HashSet)command.liveOutTag.clone();
			}
		}
		while (did_not_reach_fix_point == 1);
	}
	
	public boolean notEqualsLists(HashSet<Integer> firstList,HashSet<Integer> secondList){
		// return true if the lists are not equal
		if(firstList.isEmpty() && !(secondList.isEmpty())) return true;
		if(secondList.isEmpty() && !(firstList.isEmpty())) return true;
		for(Integer arg:firstList){
			if(!(secondList.contains(arg)))
				return true;
		}
		for(Integer arg:secondList){
			if(!(firstList.contains(arg)))
				return true;
		}
		return false;		
	}
	
	public void printControlFlowGraph(){
		int counter = 1;
		for(ArrayList<CommandNode> graph:this.controlGraph){
			System.out.format("******** graph number:%d ********\n",counter);
			for(CommandNode command:graph)
				command.printCommand();
		}
	}
	
	public void printLiveControlFlowGraph(){
		int counter = 1;
		for(ArrayList<CommandNode> graph:this.controlGraph){
			System.out.format("******** graph number:%d ********\n",counter);
			for(CommandNode command:graph)
				command.printCommandLive();
			counter++;
		}
	}
	
	
}
