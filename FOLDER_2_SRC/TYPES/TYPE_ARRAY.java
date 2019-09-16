package TYPES;

public class TYPE_ARRAY extends TYPE{

    public TYPE t;

    public boolean isMaster;
    /****************/
    /* CTROR(S) ... */
    /****************/
    public TYPE_ARRAY(String name, TYPE t, boolean isMaster)
    {
        this.name = name;
        this.t  = t;
        this.isMaster = isMaster;
    }
    public boolean isArray(){
        return true;
    }
}
