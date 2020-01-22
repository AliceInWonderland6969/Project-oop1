 

import java.util.HashMap;

/**
 * This class is part of the "NolsPotLex" application. "NolsPotLex" is a
 * very simple, text based adventure game.
 * 
 * This class holds an enumeration of all command words known to the game. It is
 * used to recognise commands as they are typed in.
 * 
 * @author Michael Kolling and David J. Barnes and aAlexandre Boursier and Nolan Potier
 * @version 2011.10.28
 */

public class CommandWords
{
    // a constant array that holds all valid command words
    private static final String[] validCommands = {
        "go", "quit", "help", "look" , "splash", "back", "inventory", "get",
        "drop"
    };

    /**
     * Constructor - initialise the command words.
     */
    public CommandWords()
    {
        // nothing to do at the moment...
    }

    /**
     * Check whether a given String is a valid command word. 
     * @return true if a given string is a valid command,
     * false if it isn't.
     */
    public boolean isCommand(String aString)
    {
        for(int i = 0; i < validCommands.length; i++) {
            if(validCommands[i].equals(aString))
                return true;
        }
        // if we get here, the string was not found in the commands
        return false;
    }
    /** Druk alle geldige opdrachten af naar System.out.
     * 
     */
    public String showAll()
    {
        String commandString = "";
        for(String command : validCommands){
            commandString += (command);
        }
        return commandString;
    }
}