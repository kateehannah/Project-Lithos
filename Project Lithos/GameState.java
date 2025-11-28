public class GameState { 
    
    // Companion revival states — only one can be active at a time
    private boolean kinoRevived = false;
    private boolean bemRevived = false;
    private boolean akioRevived = false;

    // Crafting flag granted by Bem
    private boolean alwaysSuccessfulCraft = false; 

    // Player’s inventory reference
    private Inventory inventory;

    // Chance modifier for finding platinum
    private int platinumChance = 1; 

    // Whether the player has obtained a special map fragment
    private boolean hasMapFragment = false; 
    
    // Day progression and exploration limits
    private int currentDay = 1;
    private int explorationsLeft = 10;
    private final int MAX_EXPLORATIONS = 10; 

    // Weather for the current day
    private Weather todayWeather = Weather.NORMAL; 

    // Constructor sets the initial inventory reference
    public GameState(Inventory inventory) { 
        this.inventory = inventory;
    }

    // Replace the inventory with a new one
    public void setInventory(Inventory inventory) { 
        this.inventory = inventory;
    }

    // Returns the weather for the current day
    public Weather getTodayWeather() { 
        return todayWeather; 
    } 
    
    // Advances the game to the next day
    // Resets exploration count and generates new weather
    public void sleep() { 
        currentDay++;
        explorationsLeft = MAX_EXPLORATIONS;
        todayWeather = Weather.generate(); 
        
        System.out.println("\n--- Day " + currentDay + " ---");
        System.out.println("The weather is: " + todayWeather.description + ". " + todayWeather.flavorText);
        System.out.println("You rested well. Exploration limit reset to " + MAX_EXPLORATIONS + ".");
    }

    // Decreases the number of exploration actions left for the day
    public void decrementExploration() {
        if (explorationsLeft > 0) {
            explorationsLeft--;
        }
    }

    // Restores the exploration limit to the maximum
    public void resetExplorationLimit() {
        explorationsLeft = MAX_EXPLORATIONS;
        System.out.println("You feel completely re-energized! Exploration limit reset to " + MAX_EXPLORATIONS + ".");
    }

    // Revives Kino
    // Enables double resource gathering and disables other companion effects
    public void reviveKino() { 
        kinoRevived = true;
        bemRevived = false;
        akioRevived = false;
        alwaysSuccessfulCraft = false; 
        
        inventory.setDoubleEffect(true);
        System.out.println("Kino revived! Your raw materials will double each exploration.");
    }

    // Revives Bem
    // Makes crafting always successful and disables other companion effects
    public void reviveBem() { 
        bemRevived = true;
        kinoRevived = false;
        akioRevived = false;
        alwaysSuccessfulCraft = true; 
        
        inventory.setDoubleEffect(false);
        System.out.println("Bem revived! She stabilizes your crafting. All crafts will now be 100% successful!");
    }

    // Revives Akio
    // Clears inventory and disables other companion effects
    public void reviveAkio() { 
        akioRevived = true;
        kinoRevived = false;
        bemRevived = false;
        alwaysSuccessfulCraft = false; 
        
        inventory.setDoubleEffect(false);
        inventory.clearInventory();
        System.out.println("Akio revived! Your inventory has been cleared, preparing you for a new path.");
    }

    // Returns the current day number
    public int getCurrentDay() { 
        return currentDay; 
    }

    // Returns how many explorations remain today
    public int getExplorationsLeft() { 
        return explorationsLeft; 
    }

    // Sets the platinum chance multiplier
    public void setPlatinumChance(int multiplier) { 
        this.platinumChance = multiplier; 
    }

    // Returns the platinum chance multiplier
    public int getPlatinumChance() { 
        return platinumChance; 
    }

    // Sets whether the map fragment is owned
    public void setHasMapFragment(boolean value) {
        this.hasMapFragment = value;
    }

    // Returns true if the player has the map fragment
    public boolean hasMapFragment() {
        return hasMapFragment;
    }

    // Returns true if crafting success is guaranteed
    public boolean isCraftAlwaysSuccessful() {
        return alwaysSuccessfulCraft;
    }

    // Returns revival states for each companion
    public boolean isKinoRevived() { return kinoRevived; }
    public boolean isBemRevived() { return bemRevived; }
    public boolean isAkioRevived() { return akioRevived; }

    // Returns true only if all three companions were revived at least once
    public boolean allCompanionsRevived() { 
        return kinoRevived && bemRevived && akioRevived;
    }
}
