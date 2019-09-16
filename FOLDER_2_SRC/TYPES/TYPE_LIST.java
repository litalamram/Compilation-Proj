package TYPES;

public class TYPE_LIST extends TYPE
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public TYPE head;
	public TYPE_LIST tail;
	public TYPE_LIST lastElement;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public TYPE_LIST(TYPE head,TYPE_LIST tail)
	{
		this.head = head;
		this.tail = tail;
		if(tail == null)
			this.lastElement = this;
		else
			this.lastElement = tail.lastElement;
		
	}
	
	public TYPE_LIST addElement(TYPE NewElement){
		if(this.tail == null){
			this.tail = new TYPE_LIST(NewElement,null);
			this.lastElement = this.tail;
			return this.lastElement;
		}
		else 
			return this.lastElement = this.lastElement.addElement(NewElement);
	}
}
