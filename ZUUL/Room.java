import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Michael Kölling and David J. Barnes and Omar Kassam
 * @version 2016.02.29
 */

public class Room 
{
    private String description;
    private String back;
    private HashMap<String, Room> exits; // stores exits of this room.
    //private Items item;
    private HashSet<String> items;        // Both items and characters used for longdescription message
    private HashSet<String> characters;

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     * @param items are those that can be equipped in each room
     * @param characters that have been met 
     */
    public Room(String description, HashSet<String> items, HashSet<String> characters) 
    {
        this.description = description;
        this.items = items;
        this.characters = characters;

        exits = new HashMap<>();

    }

    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor);

    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription()
    {
        //descriptions.add(description);
        return "Currently you " + description;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Exits: north west
     * @return A long description of this room including items and characters
     */
    public String getLongDescription()
    {
        return "Currently you " + description + ".\n" + getExitString() + " Equipped: " + items + "  Characters: " + characters ;
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) 
    {
        return exits.get(direction);
    }

}

