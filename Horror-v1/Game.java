import java.util.*;

public class Game 
{
    private Parser parser;
    private static Player player;
    private Room currentRoom;
    private Stack<Room> roomHistory;    
    private Room previousRoom;
    ArrayList<Item> inventory = new ArrayList<Item>();
    private static final int NB_ROOM_TELEPORT = 8;
    
    private static ArrayList<Room> rooms;
    private static Room randomRoom;
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
        roomHistory = new Stack<Room>();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room outside, theater, pub, lab, office, cellar, teleport;
      
        // create the rooms
        outside = new Room("outside the main entrance of the university", Type.OUTSIDE);
        theater = new Room("in a lecture theater", Type.THEATER);
        pub = new Room("in the campus pub", Type.PUB);
        lab = new Room("in a computing lab", Type.LAB);
        office = new Room("in the computing admin office", Type.OFFICE);
        cellar = new Room("in the cellar", Type.CELLAR);
        teleport = new Room("in the cellar", Type.TELEPORT);
        // initialise room exits
        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);
        
        theater.setExit("west", outside);
        
        pub.setExit("east", outside);
        
        lab.setExit("north", outside);
        lab.setExit("east", office);
        
        office.setExit("west", lab);
        office.setExit("down", cellar);
        office.setExit("down", teleport);
        
        cellar.setExit("up", office);
        previousRoom = outside;
        currentRoom = outside;  // start game outside
        
        inventory.add(new Item("computer"));
        office.setItem(new Item("Robot"));
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println("You are " + currentRoom.getShortDescription());
        System.out.print(currentRoom.getExitString());
        System.out.println();
    }
    
    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("look")){
            look() ;
        }
         else if (commandWord.equals("splash")) {
            splash();
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        else if (commandWord.equals("back")) {
            goBack();
        }
        else if (commandWord.equals("inventory")) {
            printInventory();
        }
        else if (commandWord.equals("get")) {
            getItem(command);
        }
        else if (commandWord.equals("drop")) {
            dropItem(command);
        }
        return wantToQuit;
    }
    private void printInventory()
    {
        String output = "";
        for (int i = 0; i < inventory.size(); i++)
        {
            output += inventory.get(i).getDescription() + "\n";
        }
        System.out.println("You are carrying: ");
        System.out.println(output);
    }
    private void dropItem(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("drop what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to leave current room.
        Item newItem = null;
        int index = 0;
        for (int i = 0; i <inventory.size(); i++)
            {
                if(inventory.get(i).getDescription().equals(item)) 
                {
                    newItem = inventory.get(i); 
                    index = i;
                } 
            }
        if (newItem == null) {
            System.out.println("That item is not in your inventory");
        }
        else {
            inventory.remove(index);
            currentRoom.setItem(new Item(item));
            System.out.println("Dropped " + item);
        }
    }
    private void getItem(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Get what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to leave current room.
        Item newItem = currentRoom.getItem(item);

        if (newItem == null) {
            System.out.println("That item is not here!");
        }
        else {
            inventory.add(newItem);
            currentRoom.removeItem(item);
            System.out.println("Picked up " + item);
        }
    }
    
    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:" );
        System.out.println("go, quit, help, look, splash, back, inventory, get, drop");
    }

    /** 
     * Try to go in one direction. If there is an exit, enter
     * the new room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            roomHistory.push(currentRoom);
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
    }
    public static void goRandomRoom(){

        int random = (int)(Math.random() * NB_ROOM_TELEPORT);
        // Select a random room
        Type teleport = Type.values()[random];
        for(Room r : rooms){
            if(r.getType().equals(teleport)){
                getPlayer().setCurrentRoom(r);
            }
        }
        System.out.println("\n ------- Aaaaah !! you're sucked into a black hole -------\n");
        System.out.println(getPlayer().getCurrentRoom().getLongDescription()); 
    }
    public static ArrayList<Room> getRooms() {
        return rooms;
    }
     public static Room getRandomRoom() {
        return randomRoom;
    }
    /**
     * @param randomRoom the randomRoom to set
     */
    public static void setRandomRoom(Room random) {
        randomRoom = random;
    }
                /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command)     {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
    private void look()
    {
        System.out.println(currentRoom.getLongDescription());
        
    }
    private void splash()
    {
        System.out.println("It's not very effective....");
    }
    /** de speler gaat terug naar de vorige kamer 
     * geeft een foutmelding wanneer je "back back" invoert 
     */
    private void goBack()
    { 
        if (roomHistory.empty())
        {   System.out.println("U kunt niet verder terug dan uw beginpunt.");
        } else {
            currentRoom = roomHistory.pop();
            System.out.println(currentRoom.getLongDescription());
        }
    }
    public static Player getPlayer() 
    {
     return player;   
    }
    public static void setPlayer( Player player) 
    {
        Game.player = player;
    }
}
