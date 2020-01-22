import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.*;

/**
 * This class is the main class of the "Zuul" application.
 * "Zuul" is a very simple, text based adventure game. Users can walk
 * around some scenery. That's all. It should really be extended to make it more
 * interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * method.
 * 
 * This main class creates and initializes all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates and executes the
 * commands that the parser returns.
 * 
 * - A level can be chosen, which determine the number of moves a player can have.
 * - The trap door will slows the game if the user tries to go through it.
 * - To access the last door, the player must take the key placed in the "DELIVERY_ROOM"
 * - A beamer can be used to teleport the player wherever he had chosen
 * - The room "TOILETS" randomly teleports the player into another.
 * - To win the game, you have to reach the room "OUTSIDE".
 * 
 * @author Michael Kolling and David J. Barnes and Alexandre Boursier and Nolan Potier
 * @version 2011.10.24
 */

public class Game {
    private Parser parser;
    private static Player player;
    // Count the number of current number of moves
    private static int numberOfMoves;
    // Fix a limit to the number of moves
    private static int limitOfMoves;
    // Fix a number of rooms for choosing the teleport room
    private static final int NB_ROOM_TELEPORT = 8;
    // Build a list which contains all the current rooms of the game
    private static ArrayList<Room> rooms;
    
    private Room currentRoom; 
    private Stack<Room> roomHistory; 
    private Room previousRoom;
    ArrayList<Item> inventory = new ArrayList<Item>();
    private static Room randomRoom;
    private static Room beamerRoom;
    private HashMap<String, Item> items;
    private ArrayList<Door> doors;

    private Room win;
    private Room outside;
    private Room theater;
    private Room pub;
    private Room teleport;
    private Room teleporter;
    private Room lab;
    private Room office;
    private Room cellar;
    private Room theEnd;

    /**
     * Create the game and initialize its internal map.
     */
    public Game() {
        rooms = new ArrayList<Room>();
        items = new HashMap<String, Item>();  
        doors = new ArrayList<Door>();
        roomHistory = new Stack<Room>();
        numberOfMoves = 0;

        createDoors();        
        setPlayer(new Player());
        createRooms();
        setRoomsDoors();
        new Trap();
    }
    
    public static void main(String[] args)
    {
        Game startGame = new Game();
        startGame.play();
    }

    /**
     * Create all the rooms and link their exits together.
     * Create a random trap door to make the game harder.
     * 
     */
    private void createRooms() {

        // Create the rooms
        outside = new Room("outside the main entrance of the university", Type.OUTSIDE);
        theater = new Room("in a lecture theater", Type.THEATER);
        pub = new Room("in the campus pub", Type.PUB);
        lab = new Room("in a computing lab", Type.LAB);
        office = new Room("in the computing admin office\nThere is a key laying on the desk", Type.OFFICE);
        cellar = new Room("in the cellar", Type.CELLAR);
        win = new Room("You win big faggot", Type.WIN);
        teleport = new Room("in a room with a", Type.TELEPORT);
        teleporter = new Room("telported to room with four doors and a ladder going up and down.", Type.TELEPORTER);
        theEnd = new Room("in the end", Type.END);
        // start game in the bedroom
        getPlayer().setCurrentRoom(outside); 
        beamerRoom = teleporter;
        randomRoom = theater;
    }

    /**
     * Initialise room doors and respective locks
     */
    private void setRoomsDoors()
    {        
        // Initialise room exits
        outside.setDoor("north", win, true);
        outside.setDoor("east", theater, false);
        outside.setDoor("south", lab, false);
        outside.setDoor("west", pub, false);
        
        theater.setDoor("west", outside, false);
        
        pub.setDoor("east", outside, false);
        pub.setDoor("north", teleport, false);
        
        teleport.setDoor("up", teleporter, false);
        teleport.setDoor("south", pub, false);
        
        lab.setDoor("north", outside, false);
        lab.setDoor("east", office, false);
        
        office.setDoor("west", lab, false);
        office.setDoor("down", cellar, false);
        
        cellar.setDoor("up", office, false);
        
        theEnd.setDoor("", outside, false);
        teleporter.setDoor("north", pub, false);
        teleporter.setDoor("east", office, false);
        teleporter.setDoor("south", theater, false);
        teleporter.setDoor("west", outside, false);
        teleporter.setDoor("up", theEnd, false);
        teleporter.setDoor("down", cellar, false);
    }

    /**
     * Create the Doors for the game.
     */
    private void createDoors(){
        Door north, east, south, west,up, down;

        north = new Door("north");
        east = new Door("east");
        south = new Door("south");
        west = new Door("west");
        // To get out of the trap
        up = new Door("up");
        down = new Door("down");

        //add each door to doors collection
        doors.add(north);
        doors.add(east);
        doors.add(south);
        doors.add(west);
        doors.add(up);
        doors.add(down);
    }

    /**
     * Adding a room to the dictionary
     * @param r
     */
    public static void addRoom(Room r){
        rooms.add(r);
    }   

    /**
     * Main play routine. Loops until end of play.
     * 
     */
    public void play() {

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
     * New form of time limit : a level is asked at the beginning
     * of the game defined by the maximum tolerated number of moves.
     * @return 
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to Zuul!");
        
        chooseLevel();

        System.out.println("Type help if you need help.");
        System.out.println();
        System.out.println(getPlayer().getCurrentRoom().getLongDescription());

        // Instantiate a parser which will read the command words
        parser = new Parser();
    }
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
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:" );
        System.out.println("go, quit, help, look, splash, back, inventory, get, drop");
    }
    private void splash()
    {
        System.out.println("It's not very effective....");
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
        Room nextRoom = currentRoom.getExit();

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
    private void goBack()
    { 
        if (roomHistory.empty())
        {   System.out.println("U kunt niet verder terug dan uw beginpunt.");
        } else {
            currentRoom = roomHistory.pop();
            System.out.println(currentRoom.getLongDescription());
        }
    }
    /**
     * Choosing the level of the game :
     * - Easy is for beginners 
     * - Medium brings a little bit more challenge
     * - Hard is the "no-mistake way"
     * 
     */
    private void chooseLevel()
    {
        // Choosing a level (asking to the user through the terminal)
        Scanner reader = new Scanner(System.in);
        //System.out.println("Please choose a level : Easy 20 moves(0) - Medium 16 moves(1) - Hard 14 moves (2)");
        System.out.println("Press any key to start");
        // Find the chosen level and alter the number of moves accorfing to the chosen one
        try {
            switch (reader.nextInt()) {
            /*
            case 0:
                limitOfMoves = 20;
                System.out.println("You've chosen the easy way to win ! - Number of moves : " + limitOfMoves);
                break;
                
            case 1:
                limitOfMoves = 16;
                System.out.println("You've chosen the medium level :)- Number of moves : " + limitOfMoves);
                break;
            case 2:
                limitOfMoves = 14;
                System.out.println("It's gonna be hard this way :@  - Number of moves : " + limitOfMoves);
                break;
                */
            default:
                limitOfMoves = 20;
                System.out.println("Unkown command - Default level : Easy - Number of moves : " + limitOfMoves);
                break;
            }
        } catch(Exception e){
            limitOfMoves = 1000;
        }
    }

    /**
     * Counting the current move of the player
     * @return false if the player has executed too many moves, true otherwise
     */
    public static boolean countMove(){
        // Count a move
        numberOfMoves++;

        // Give some informations concerning the number of moves
        if (numberOfMoves < limitOfMoves) {
            //System.out.println("Current number of moves : " + numberOfMoves);
            //System.out.println("Moves left : " + (limitOfMoves - numberOfMoves));
            return false;
            // Ending the game if the number of moves is reached
        } else {
            System.out.println("You have reached the maximum number of moves");
            System.out.println("By the way, GAME OVER ! ");
            System.out.println();
            System.out.println();
            return true;
        }
    }

    /**
     * @return the numberOfMoves
     */
    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    /**
     * @return the limitOfMoves
     */
    public int getLimitOfMoves() {
        return limitOfMoves;
    }

    /**
     * @param limitOfMoves the limitOfMoves to set
     */
    public void setLimitOfMoves(int lom) {
        limitOfMoves = lom;
    }

    /**
     * @return the beamerRoom
     */
    public static Room getBeamerRoom() {
        return beamerRoom;
    }

    /**
     * @param beamerRoom the beamerRoom to set
     */
    public static void setBeamerRoom(Room beamer) {
        beamerRoom = beamer;
    }
    /**
     * @return the player
     */
    public static Player getPlayer() {
        return player;
    }

    /**
     * @param player the player to set
     */
    public static void setPlayer(Player player) {
        Game.player = player;
    }

}
