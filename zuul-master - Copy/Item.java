 

/**
 * This class is part of the "World of NolsPotLex" application. "World of NolsPotLex" is a
 * very simple, text based adventure game.
 * 
 * A "item" represents a device that you can be used in the scenery of the game.
 * 
 * @author Alexandre Boursier & Nolan Potier
 * @version 2011.10.25
 */

public class Item {

    private String name;
    private String description;
    
    public Item(String newdescription)
    {
        description = newdescription;
    }
    public String getDescription()
    {
        return description;
    }

}
