package TYPES;

public class TYPE_TRACE extends TYPE
{
    /**************************************/
    /* USUAL SINGLETON IMPLEMENTATION ... */
    /**************************************/
    private static TYPE_TRACE instance = null;

    /*****************************/
    /* PREVENT INSTANTIATION ... */
    /*****************************/
    protected TYPE_TRACE() {}

    /******************************/
    /* GET SINGLETON INSTANCE ... */
    /******************************/
    public static TYPE_TRACE getInstance()
    {
        if (instance == null)
        {
            instance = new TYPE_TRACE();
            instance.name = "trace";
        }
        return instance;
    }
}
