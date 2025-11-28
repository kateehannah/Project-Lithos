public class GameState { 
    // Flags for companion revival states — only one can be active at a time
    private boolean kinoRevived = false;
    private boolean bemRevived = false;
    private boolean akioRevived = false;
    
    // Flag to indicate crafting success is always guaranteed (granted by Bem)
    private boolean alwaysSuccessfulCraft = false; 
    
    // Reference to the player's inventory
    private Inventory inventory;
    
    // Chance multiplier for finding platinum resources
    private int platinumChance = 1; 
    
    // Indicates if the player has obtained the special map fragment
    private boolean hasMapFragment = false; 
    
    // Current day number in the game
    private int currentDay = 1;
    
    // Number of exploration attempts left for the current day
    private int explorationsLeft = 10;
    
    // Maximum exploration attempts allowed per day
    private final int MAX_EXPLORATIONS = 10; 
    
    // Weather condition for the current day
    private Weather todayWeather = Weather.NORMAL; 

    // Constructor to initialize GameState with the player's inventory
    public GameState(Inventory inventory) { 
        this.inventory = inventory;
    }

    // Setter to replace the player's inventory with a new one
    public void setInventory(Inventory inventory) { 
        this.inventory = inventory;
    }
    
    // Getter to retrieve today's weather
    public Weather getTodayWeather() { return todayWeather; } 
    
    // Advances the game to the next day and resets exploration limits
    public void sleep() { 
        currentDay++;
        explorationsLeft = MAX_EXPLORATIONS;
        todayWeather = Weather.generate(); 
        System.out.println("\n--- Day " + currentDay + " ---");
        System.out.println("The weather is: " + todayWeather.description + ". " + todayWeather.flavorText);
        System.out.println("You rested well. Exploration limit reset to " + MAX_EXPLORATIONS + ".");
    }
    
    // Decrease exploration attempts left if any remain
    public void decrementExploration() {
        if (explorationsLeft > 0) {
            explorationsLeft--;
        }
    }
    
    // Resets exploration attempts back to the daily maximum
    public void resetExplorationLimit() {
        explorationsLeft = MAX_EXPLORATIONS;
        System.out.println("You feel completely re-energized! Exploration limit reset to " + MAX_EXPLORATIONS + ".");
    }

    // Revives Kino and enables double resource gathering effect
    public void reviveKino() { 
        kinoRevived = true;
        bemRevived = false;
        akioRevived = false;
        alwaysSuccessfulCraft = false; 
        inventory.setDoubleEffect(true);
        System.out.println("Kino revived! Your raw materials will double each exploration.");
    }

    // Revives Bem and makes all crafting 100% successful
    public void reviveBem() { 
        bemRevived = true;
        kinoRevived = false;
        akioRevived = false;
        alwaysSuccessfulCraft = true; 
        inventory.setDoubleEffect(false);
        System.out.println("Bem revived! She stabilizes your crafting. All crafts will now be 100% successful!");
    }

    // Revives Akio, clears inventory, and disables other companion effects
    public void reviveAkio() { 
        akioRevived = true;
        kinoRevived = false;
        bemRevived = false;
        alwaysSuccessfulCraft = false; 
        inventory.setDoubleEffect(false);
        inventory.clearInventory();
    }
    
    // Getter for the current day number
    public int getCurrentDay() { return currentDay; }
    
    // Getter for remaining exploration attempts today
    public int getExplorationsLeft() { return explorationsLeft; } 
    
    // Setter for platinum chance multiplier
    public void setPlatinumChance(int multiplier) { 
        this.platinumChance = multiplier; 
    }
    
    // Getter for platinum chance multiplier
    public int getPlatinumChance() { return platinumChance; }
    
    // Setter for whether the player has the map fragment
    public void setHasMapFragment(boolean value) {
        this.hasMapFragment = value;
    }
    
    // Returns true if the player has the map fragment
    public boolean hasMapFragment() {
        return hasMapFragment;
    }
    
    // Returns true if crafting is always successful
    public boolean isCraftAlwaysSuccessful() {
        return alwaysSuccessfulCraft;
    }

    // Getters for companion revival states
    public boolean isKinoRevived() { return kinoRevived; }
    public boolean isBemRevived() { return bemRevived; }
    public boolean isAkioRevived() { return akioRevived; }
    
    // Getter for the player's inventory
    public Inventory getInventory() { return inventory; }
    
    // Returns true only if all companions are revived
    public boolean allCompanionsRevived() { 
        return kinoRevived && bemRevived && akioRevived;
    }
}
