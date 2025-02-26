import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  Now changed it is a very simple, text based adventure game.  Users 
 *  can walk around some underground rooms and must find certain items 
 *  in order to acitvate a win condition, meeting characters along the way.
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes and Omar Kassam
 * @version 2016.02.29
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;

    private HashSet<String> items;            
    private ArrayList<String> footsteps;
    private HashSet<String> characters;
    private ArrayList<Room> rooms;
    private Random rand;

    private int weight;
    private boolean win;
    private boolean isDead;
    private int health;
    private String direction;
    private String footstep;

    /**
     * Create the game and initialise its internal map, items and characters.
     * 
     * Also sets the player's intial conditions(win = false and health)
     */
    public Game() 
    {
        parser = new Parser();

        items = new HashSet<>(); 
        items.add("NOTHING");

        footsteps = new ArrayList<>(); // Array List used for the goBack method later on
        rooms = new ArrayList<>();     // used for the random movement of a character later on
        rand = new Random();
        characters = new HashSet<>();  

        this.direction = direction;
        createRooms();

        win = false;
        health = 100;
    }

    /**
     * Create all the rooms and link their exits together.
     * 
     * Also after each room created stores it in an ArrayList of rooms 
     */
    private void createRooms()
    {
        Room entrance, tunnel, armory, chest, cupboard, book, store, ladder, glow, door, fountain, TELEPORT;

        // create the rooms
        entrance = new Room("are at the bottom of a dark pit...", items, characters); 
        rooms.add(entrance);

        tunnel = new Room("are crawling a slimy tunnel with a metal door at the end", items, characters);
        rooms.add(tunnel);

        armory = new Room("have entered room filled with weapons, one cuboard and a chest you can equip a Green sword", items, characters);
        rooms.add(armory);

        chest = new Room(" have opened a chest with a golden bow and silver arrows you can only carry one weapon at a time...", items, characters);
        rooms.add(chest);

        cupboard = new Room(" are opening a cuboard with an ancient book you can also equip this.", items, characters);
        rooms.add(cupboard);

        ladder = new Room("have begun to climb a ladder with a Green glowing object at the top", items, characters);
        rooms.add(ladder);

        glow = new Room("have chosen to reach out only to feel a sharp jolt of pain causing you to become injured defeat this to escape", items, characters); //not added to rooms DWARF could be in as this is the win condition

        door = new Room("pass through a large door with a fountain inside it", items, characters);
        rooms.add(door);

        fountain = new Room("are in the fountain of good health drink up to ease your pain", items, characters);
        rooms.add(fountain);

        book = new Room("are reading the ancient book of the underworld, BE WARY TRAVELLER of the orb of chaos " + 
            "many succumb to it's might, to overpower it you must use it's own energy but be aware of it's minions", items, characters);
        rooms.add(book);

        store = new Room("are speaking to Ernie:  Welcome to underworld, you are in trouble friend but fear not, I'm here to help equip this free potion on me...(type equip friend)", items, characters);
        //no need to add this to rooms as having both characters(Ernie and DWARF) in the terminal is too much input for user

        TELEPORT = new Room("are in THE TELEPORT ROOM", items, characters);
        // Also no point in adding teleport to rooms DWARF can be in because you will teleport OUT of this room once entering

        // initialise room exits
        entrance.setExit("north", tunnel);
        entrance.setExit("south", store);
        entrance.setExit("up", ladder);
        entrance.setExit("east", door);
        entrance.setExit("down", TELEPORT);

        tunnel.setExit("south", entrance);
        tunnel.setExit("north", armory);

        armory.setExit("south", tunnel);
        armory.setExit("east", chest);
        armory.setExit("west", cupboard);

        chest.setExit("west", armory);  

        cupboard.setExit("east", armory);
        cupboard.setExit("down", book);

        book.setExit("up", cupboard);

        store.setExit("north", entrance);

        ladder.setExit("down", entrance);
        ladder.setExit("up", glow);

        glow.setExit("down", ladder);

        door.setExit("west", entrance);
        door.setExit("east", fountain);

        fountain.setExit("west", door);

        currentRoom = entrance;  // start game in dark pit
    }

    /**
     *  Main play routine.  Loops until end of play.
     *  
     *  Conditions it loops on considers health, win condition OR the command quit giving different statements for each outcome
     */
    public void play() 
    {            
        printWelcome();
        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (! finished && ! win) {
            Command command = parser.getCommand();
            finished = processCommand(command);
            if(health == 0) {
                finished = true;
                System.out.println("YOU DIED...");
            } 
            gameWinChecker(); 
        }

        if (win == false){
            System.out.println("Thank you for playing. Good bye.");
        }

        if (win == true) {
            System.out.println("YOU WON!!!!!");
        }
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("The objective of this game is to use friends and items to understand what is happening");
        System.out.println("You may have to use these items to either help yourself or save others...");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
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

        //all commands 
        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        else if (commandWord.equals("equip")) {
            equip(command);
        }
        else if (commandWord.equals("back")) {
            goBack(command);
        }
        else if (commandWord.equals("heal")) {
            heal(command);
        }
        else if (commandWord.equals("unequip")) {
            unequip(command);
        }
        else if (commandWord.equals("fight")) {
            fight(command);
        }
        else if (commandWord.equals("give")) {
            give(command);
        }
        else if (commandWord.equals("shoot")) {
            shootArrowDwarf(command);
        }
        // else command not recognised.
        return wantToQuit;
    }

    /**
     * Given you are in a certain room and do not have an item (the if condition)  you will lose health
     * If health reaches 0 ends the game
     */
    private void healthLoss()
    {
        String checker = "" + currentRoom.getShortDescription(); //Gives us string of the currentRoom description
        //Useful for using if statements to apply events in the game

        if (checker.contains("jolt") && items.contains("SWORD of VICTORS") == false)
        {
            health = health - 50;
        }    
    }

    /**
     * Checks if the win condition has been met, if so will end game.
     */
    private void gameWinChecker()
    {
        String checker = "" + currentRoom.getShortDescription();
        if (checker.contains("glowing") && items.contains("SWORD of VICTORS") == true)
        {
            win = true; // boolean instance field win is set to true once room entered with correct item
            System.out.println("You DEFEATED the evil orb of misery.");
        }
    }

    /**
     * This method implements the appearance of both characters Ernie and the Dwarf.
     * 
     */
    private void characters()
    {
        int index = rand.nextInt(rooms.size()); // index set to a random number within the rooms ArrayList
        Room randomRoom = rooms.get(index);     // stores the room at the index to randomRoom so we can check if we are in this room

        // Given conditions a character will be added and shown on the terminal with their appropriate text

        if (randomRoom == currentRoom && isDead == false && items.contains("SWORD of VICTORS") == true)
        {
            characters.add("DWARF");
            System.out.println("DWARF: OI, i need some help getting me friend he's stuck could ya lend me something!");

        }
        else if(randomRoom == currentRoom && isDead == false && items.contains("GOLDEN BOW") == true){
            characters.add("DWARF");
            System.out.println("DWARF: OI, i need some help getting me friend he's stuck could ya lend me something!");
        }

        String checker = currentRoom.getShortDescription();
        if (checker.contains("potion") && items.contains("HEALTH POTION") == false)
        {
            characters.add("ERNIE");
        }
    }

    /**
     * This method teleports player into a random room
     * 
     * Checks if you are in the teleport room before applying teleport 
     * 
     * Uses boolean conditions in order to not repeat the printing of room's description twice shown later in goRoom method
     */
    private boolean MagicTeleport()
    {
        String checker = currentRoom.getShortDescription();
        if(checker.contains("TELEPORT")){
            footsteps.clear(); //RESTARTS footstep directions in order to prevent crash from goBack due to null error
            int index = rand.nextInt(rooms.size());
            Room randomRoom = rooms.get(index);
            currentRoom = randomRoom;
            healthLoss();      
            System.out.println("You have been TELEPORTED: " + currentRoom.getLongDescription());
            characters();     
            return true;
        }
        else{
            return false;
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
        System.out.println("No purpose...");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
        System.out.println("Note: shoot, arrow and dwarf are used as one three-worded command in this order");
    }

    /** 
     * Try to in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        direction = command.getSecondWord();

        // Try to leave current room.

        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null ) {
            System.out.println("There is no door!");
        }
        else {
            footsteps.add(direction);   
            //stores direction string to ArrayList so it can later be reversed in goBack method            
            //Used here as outside else statement it will add null values for when user tries to go into room that is null
            currentRoom = nextRoom;
            healthLoss(); //calls method to check if the room takes away health or not
            boolean checker = MagicTeleport(); 
            //Stores value returned at Teleport 
            //IF true then we have teleported so no need to print description below

            if(checker == false){
                System.out.println(currentRoom.getLongDescription() + " HEALTH: " + health);
                characters();  // called here so the dwarf moves to a random room each time when you move 
            }
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }

    /** 
     * "equip" entered so a series of if statements are applied 
     * 
     * Weight is added after adding each item, will not equip new items after MAX weight of int 3 reached.
     * 
     * The if statements check if you are in a certain room AND if you have already equipped the weapon 
     * this is in order to make sure firstly, you are in the room in which you can equip the item 
     * and secondly you haven't already got the weapon so the weight does not increment each time equip called.
     */
    private void equip(Command command)
    {
        String checker = currentRoom.getShortDescription();

        if (checker.contains("sword") && weight < 3 && items.contains("SWORD of VICTORS") == false && items.contains("GOLDEN BOW") == false)
        {
            weight = weight + 2;

            if (weight <= 3){
                items.add("SWORD of VICTORS");  
                items.remove("NOTHING"); 
            }
            else{
                System.out.println("Your Backpack is FULL." + weight);
            }
        }
        else if (checker.contains("bow") && weight < 3 && items.contains("GOLDEN BOW") == false)
        {
            items.add("GOLDEN BOW");
            weight = weight + 1;
            items.remove("NOTHING");
            if(items.contains("SWORD of VICTORS") == true){
                items.remove("SWORD of VICTORS");
                weight = weight - 2;
            }
        }
        else if (checker.contains("ancient") && weight < 3 && items.contains("ANCIENT BOOK") == false)
        {
            items.add("ANCIENT BOOK");
            weight = weight + 1;
            items.remove("NOTHING");
        }
        else if (checker.contains("potion") && weight < 3 && items.contains("HEALTH POTION") == false){
            items.add("HEALTH POTION");
            items.remove("NOTHING");
            weight = weight + 1;
        }
        //Condition in which you can on longer add items
        else if(weight >= 3)
        {
            System.out.println("Your Backpack is FULL." + "WEIGHT: " + weight);
        }
        System.out.println("Equipped: " + items); 
    }

    /** 
     * "unequip" essentially equip command in reverse 
     */
    private void unequip(Command command)
    {
        String checker = currentRoom.getShortDescription();

        if (items.contains("SWORD of VICTORS") == true)
        {
            items.remove("SWORD of VICTORS");  // could create a command to pick up items 
            weight = weight - 2;
        }
        else if (items.contains("GOLDEN BOW") == true)
        {
            items.remove("GOLDEN BOW");
            weight = weight - 1;
        }
        else if (items.contains("ANCIENT BOOK") == true)
        {
            items.remove("ANCIENT BOOK");
            weight = weight - 1;
        }
        else if (items.contains("HEALTH POTION") == true)
        {
            items.remove("HEALTH POTION");
            weight = weight - 1;
        }
        System.out.println("Equipped: " + items);
    }

    /** 
     * "heal" entered and so given you are in a certain room OR have health item your health is fully restored
     * 
     *  once applying either you will lose health potion
     */
    private void heal(Command command)
    {
        String checker = currentRoom.getShortDescription();

        if (checker.contains("fountain") || items.contains("HEALTH POTION") == true)
        {
            health = 100;
            items.remove("HEALTH POTION");
        }
        System.out.println("HEALTH: " + health);
    }

    /** 
     * "back" entered and so will cause player to move into the previous room they were in
     * 
     * Will continue to work until back at the start (either from where you teleported OR the game start)
     * 
     * Uses the footsteps ArrayList which tracks every valid direction player has gone
     */
    private void goBack(Command command)
    {
        boolean error = false; // using boolean local variable as an exit condition for while loop

        //condition to check if arraylist empty to prevent crash
        if(footsteps.isEmpty()){
            System.out.println("You are at the start.");
            error = true; // prevents entering while loop
        }

        while (error == false)
        {
            String footstep = footsteps.getLast();  //receive last direction player entered, and stores it
            footsteps.removeLast();               // and then removes it from ArrayList so next direction is in last position

            //Series of if statements that converts stored direction to the opposite way in order to go Back
            if (footstep.equals("north")){
                footstep = "south";  
            }
            else if (footstep.equals("south")){
                footstep = "north";  
            }
            else if (footstep.equals("east")){
                footstep = "west";  
            }
            else if (footstep.equals("west")){
                footstep = "east";  
            }
            else if (footstep.equals("up")){
                footstep = "down";  
            }
            else if (footstep.equals("down")){
                footstep = "up";  
            }
            else if (footstep == null){
                footsteps.removeLast(); //code was inputting null values into footsteps array so to stop crashes added this condition to remove those null values
            }
            Room lastRoom = currentRoom.getExit(footstep); // causes you to enter room you just left

            currentRoom = lastRoom;                       
            healthLoss();
            System.out.println(currentRoom.getLongDescription() + " HEALTH: " + health);
            error = true;

        }
    }

    /** 
     * "fight" entered so given Dwarf is alive and you have correct items you will 'kill' the Dwarf
     */
    private void fight(Command command)
    {
        isDead = false;
        // to prevent null crash we check if characters.isEmpty()
        if(characters.isEmpty() == false && isDead == false && items.contains("SWORD of VICTORS") == true)
        {
            characters.remove("DWARF");
            System.out.println("You have used your sword to slay the wicked DWARF now fight the orb and FREE YOURSELF.");
            isDead = true;
        }
        else{
            System.out.println("Use this command only after encountering people you deem a threat or once you have items to use.");
        }
    }

    /** 
     * "give" entered so given conditions are met you are either warned of the consequences of helping the Dwarf
     * Or you are punished by dying
     */
    private void give(Command command)
    {
        //Checks if you are given a warning or not by having certain items
        if(characters.isEmpty() == false && items.contains("ANCIENT BOOK") == true && items.contains("SWORD of VICTORS") == true)
        {
            System.out.println("Remember what the book told you, beware of minions...");
        }
        else if(characters.isEmpty() == false && items.contains("ANCIENT BOOK") == false && items.contains("SWORD of VICTORS") == true){
            characters.remove("DWARF");
            items.remove("SWORD OF VICTORS");
            System.out.println("You have given your sword to the DWARF. He laughs and hits you with it...");
            health = 0;
        }
        else{
            System.out.println("Use this command only after encountering people you deem a threat or once you have items to use.");
        }
    }

    /**  
     * If "shoot" entered will come here and the conditions determine whether "shoot arrow dwarf" entered
     * 
     * Given conditions are met you will fight the dwarf and defeat it whilst sustaining a bit of damage
     */
    private void shootArrowDwarf(Command command)
    {
        if(!command.hasThirdWord()) {
            // if there is no third word, not specific to dwarf...
            System.out.println("shoot arrow at who?");
            return;
        }

        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know what is needed to shoot...
            System.out.println("shoot what?");
            return;
        }

        String thirdWord = command.getThirdWord();
        String secondWord = command.getSecondWord();

        //this condition checks for correct command given and correct items and characters present
        if (secondWord.equals("arrow") && thirdWord.equals("dwarf") 
        && items.contains("GOLDEN BOW") == true && characters.contains("DWARF") == true){
            characters.remove("DWARF");
            isDead = true;
            health = health - 50;
            System.out.println("In a hard fought battle you take out the DWARF..." + " HEALTH:  " + health);
        }
        else{
            System.out.println("Only use this command when you have appropiate weapons AND you reference the evil DWARF");
        }
    }
}