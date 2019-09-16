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

public class InterferenceNode implements Comparable<InterferenceNode>
{
	/*****************/
	/* Class Fields  */
	/*****************/
	Integer tempNum;
	Integer regNum;
	int deg;
	HashSet<Integer> interfereTemps;
	
	public InterferenceNode(Integer tempNum){
		this.tempNum = tempNum;
		this.regNum = -1;
		this.deg = 0;
		this.interfereTemps = new HashSet<>();
	}
	
//	public void addInterferNode(int tempNum){
//		this.interfereTemp.add(tempNum);
//	}
	
	public void refreshDeg(){
		this.deg = this.interfereTemps.size();
	}
	
	public void addInterfere(HashSet<Integer> set){
		for(Integer interNum: set){
			if(interNum.compareTo(this.tempNum)!=0){
				this.interfereTemps.add(interNum);
			}
		}
	}
	
	public void printNode(){
		System.out.format(" Temp number:%d \n",this.tempNum);
		System.out.format(" interfereNodes: { ");
		for(Integer num:this.interfereTemps){
			System.out.format(" %d, ", num);
		}
		System.out.format(" }\n");
	}
	
	
	  @Override
	  public int compareTo(InterferenceNode obj) {
	    return (int)(this.deg - obj.deg);
	  }
}
