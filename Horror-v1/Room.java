import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  The exits are labelled north, 
 * east, south, west.  For each direction, the room stores a reference
 * to the neighboring room, or null if there is no exit in that direction.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2011.07.31
 */
public class Room 
{
    private String description;
    private HashMap<String, Room> exits;
    ArrayList<Item> items = new ArrayList<Item>();
    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     */
    public Room (String description) 
    {
        this.description = description;
        exits = new HashMap<String, Room>();
    }

    /**
     * Define the exits of this room.  Every direction either leads
     * to another room or is null (no exit there).
     * @param north The north exit.
     * @param east The east east.
     * @param south The south exit.
     * @param west The west exit.
     */
    public void setExit(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor);
    }
   
    /**
     * @return The description of the room.
     */
    public String getShortDescription()
    {
        return description;
    }
    public String getLongDescription()
    {
        return "You are " + description + "\n" + getExitString();
    }
     public Room getExit(String direction) 
    {
        return exits.get(direction);
    }
     /** 
     * Retourneer een string met daarin de uitgangen van de ruimte, bijvoorbeeld 
     * "exits: north west". @return een omschrijving van de aanwezige uitgangen 
     * in de ruimte.
     */
    public String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        if(getRoomItems() != null) {
            returnString += "\nItems in the room: " + getRoomItems();
        }
        return returnString;
    }
    
    public Item getItem(int index)
    {
        return items.get(index);
    }
    
    public Item getItem(String itemName)
    {
            for (int i = 0; i <items.size(); i++)
            {
                if(items.get(i).getDescription().equals(itemName)) 
                {
                    return items.get(i);
                } 
            }
        return null;
    }
    public void removeItem(String itemName)
    {
            for (int i = 0; i <items.size(); i++)
            {
                if(items.get(i).getDescription().equals(itemName)) 
                {
                    items.remove(i);
                } 
            }
    }
    public void setItem(Item newitem)
    {
        items.add(newitem);
    }
    public String getRoomItems()
    {
        String output = null;
        for (int i = 0; i <items.size(); i++)
        {
            output = items.get(i).getDescription() + "";
        }
        return output;
    }
}
