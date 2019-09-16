package TYPES;

public class TYPE_CLASS extends TYPE
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TYPE_CLASS father;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TYPE_LIST data_members;
	public TYPE_LIST class_methods;
	public int fieldNum = 0;
	public int funcNum = 0;
	public String[] methodsLabels = null;

	/******************************************************/
	/* Check if the current class or one of its ancestors */
	/* IS the given ancestor type                         */
	/******************************************************/
	public boolean hasAncestor(TYPE ancestor){
		return (ancestor == this) || (null != father && father.hasAncestor(ancestor));
	}

	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_CLASS(TYPE_CLASS father,String name,TYPE_LIST data_members,TYPE_LIST class_methods)
	{
		this.name = name;
		this.father = father;
		this.data_members = data_members;
		this.class_methods = class_methods;
	}
	
	public boolean isClass(){ return true;}

	public Boolean gotFromAncestor(TYPE_CLASS sonClass){
		if (sonClass == null)
			return false;
		if (sonClass.isEquals(this))
			return true;
		return this.gotFromAncestor(sonClass.father);
	}

	public TYPE getFieldType(String fieldName){
		TYPE dataMember;
		if(this.data_members == null)
			return null;
		for(TYPE_LIST it= this.data_members; it!=null; it=it.tail){
			dataMember = (TYPE) it.head;
			if(dataMember.name.equals(fieldName))
				return dataMember;
		}
		return null;
	}
	
	public TYPE getInheritedFieldType(String fieldName){
		TYPE memberType = this.getFieldType(fieldName);
		if (memberType != null)
			return memberType;
		if (this.father == null)
			return null;
		return this.father.getInheritedFieldType(fieldName);
	}

	public TYPE_FUNCTION getMethod(String fieldName){
		TYPE_FUNCTION method;
		if(this.class_methods == null)
			return null;
		for(TYPE_LIST it= this.class_methods; it!=null; it=it.tail){
			method = (TYPE_FUNCTION) it.head;
			if(method.name.equals(fieldName))
				return method;
		}
		return null;
	}

	public TYPE_FUNCTION doesAncestorsHaveMethod(String fieldName){
		TYPE_FUNCTION method = this.getMethod(fieldName);
		if(method != null)
			return method;
		if(this.father == null)
			return null;
		return this.father.doesAncestorsHaveMethod(fieldName);
	}
	public void createMethodsLabels(){
		TYPE_FUNCTION method;
		if (father != null) {
			this.createInharitedMethodsLabels();
		}
		int index = 0;

		if(this.class_methods == null){
			return;
		}
		for (TYPE_LIST it= this.class_methods; it!=null; it=it.tail){
			method = (TYPE_FUNCTION) it.head;
			index = method.offset;
			this.methodsLabels[index] = method.labelName;
		}
	}

	public void createInharitedMethodsLabels(){
		for (int i=0; i < father.funcNum; i++){
			this.methodsLabels[i] = father.methodsLabels[i];
		}
	}

	public String methodsLabelsToString(){
		String res = this.methodsLabels[0];
		for (int i=1; i<this.funcNum; i++){
			res += (", " + this.methodsLabels[i]);
		}
		res += "\n";
		return res;
	}

}
