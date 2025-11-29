import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Explore {
    private final GameState gameState;        // Holds current day, weather, flags, explorations left, etc.
    private final Inventory inventory;        // Player's inventory (materials, crafted items, fullness)
    private final Random random = new Random(); // For RNG events (Akio, hazards)
    private final Scanner sc = new Scanner(System.in); // For user input
    private final DropTable dropTable;        // Handles material drops for each Place

    public Explore(GameState gameState) {
        this.gameState = gameState;                  // Save reference to game state
        this.inventory = gameState.getInventory();   // Get inventory from game state
        this.dropTable = new DropTable(inventory);   // Initialize drop table using same inventory
    }

    public void enterExploreMenu() {
        while(true) { // Main exploration loop
            if (gameState.getExplorationsLeft() <= 0) {
                // Reached daily exploration cap
                System.out.println("\n--- Energy Exhausted ---");
                System.out.println("You have reached the daily exploration limit (10).");
                System.out.println("You must \'Sleep\' to advance to the next day, or eat \'Cooked Meat\' to continue today.");
                System.out.print("Type 'back' to return to Main Menu: ");
                sc.nextLine(); 
                break;
            }
            
            // Header showing day, attempts left, and weather
            System.out.println("\n--- Exploration (Day " + gameState.getCurrentDay() + ": " + gameState.getExplorationsLeft() + " attempts left) ---");
            System.out.println("Weather: " + gameState.getTodayWeather().description + " (" + gameState.getTodayWeather().flavorText + ")");
            
            // Base available places; Cave requires map fragment, Deep Sea unlocks at Day 5+
            String places = "1. River  2. Forest  3. Rocky Beach" + (gameState.hasMapFragment() ? "  4. Cave" : "");
            if (gameState.getCurrentDay() >= 5) {
                places += "  5. Deep Sea Shore";
            }
            
            System.out.println("Available Places: " + places);
            System.out.print("Enter place (Name/Number) or 'back': ");
            String placeInput = sc.nextLine().trim();

            if (placeInput.equalsIgnoreCase("back")) {
                break; // Exit explore menu back to main menu
            }
            
            explorePlace(placeInput);   // Handle the exploration logic for chosen place
            inventory.decreaseFullness(); // Exploring costs fullness each time
        }
    }

    public void explorePlace(String placeInput) {
        try {
            Place selectedPlace;
            String normalized = placeInput.trim().toLowerCase(); // Normalize input for easier matching

            // Map user input to a Place enum or throw if invalid/locked
            selectedPlace = switch (normalized) {
                case "1", "river" -> Place.RIVER;
                case "2", "forest" -> Place.FOREST;
                case "3", "rocky beach" -> Place.ROCKY_BEACH;
                case "4", "cave" -> {
                    // Cave requires map fragment to be discovered
                    if (!gameState.hasMapFragment()) {
                        throw new PlaceNotFoundException("The Cave is hidden. You need to find a **Map Fragment** first!");
                    }
                    yield Place.CAVE;
                }
                case "5", "deep sea shore" -> {
                    // Deep Sea cannot be accessed before Day 5
                    if (gameState.getCurrentDay() < 5) {
                        throw new PlaceNotFoundException("The Deep Sea Shore is too dangerous before Day 5.");
                    }
                    yield Place.DEEP_SEA_SHORE;
                }
                default -> throw new PlaceNotFoundException("Unknown place: " + placeInput);
            };

            // Akio-related mechanics if he is revived
            if (gameState.isAkioRevived()) {
                // After Day 10, 25% chance that Akio steals a crafted item at the start of exploration
                if (gameState.getCurrentDay() >= 10 && random.nextInt(100) < 25) { 
                    if (inventory.getCraftedItemCount() > 0) {
                        inventory.removeRandomCraftedItem();
                        System.out.println("\n!!! AKIO ATTACK !!! Akio stole a **Crafted Item** and vanished!");
                        gameState.decrementExploration();
                        return;
                    }
                }
                // Main Akio effect: inventory reset and exploration ends
                System.out.println("\nAkio revived! He stole all your things... Inventory reset.");
                gameState.reviveAkio();
                return;
            }

            // Announce exploration start
            System.out.println("\nExploring the " + selectedPlace.name + "...");

            // Check for hazard (using DropTable's hazard roll)
            if (dropTable.checkHazard()) {
                System.out.println("Oh no! You encountered a hazard.");
                
                // Priority of what is lost to escape the hazard
                if (inventory.hasMaterial("Stone")) {
                    inventory.useMaterial("Stone", 1);
                    System.out.println("You used 1 Stone to patch a hole in your bag. You are safe.");
                } else if (inventory.getMaterialCount("Cooked Meat") > 0) {
                     inventory.useMaterial("Cooked Meat", 1);
                     System.out.println("You dropped a piece of Cooked Meat while running. You are safe.");
                } else if (inventory.getCraftedItemCount() > 0) {
                    System.out.println("You barely escaped, but you lost a crafted item!");
                    inventory.removeRandomCraftedItem(); 
                } else {
                    System.out.println("You narrowly escaped, but are exhausted (Fullness reset).");
                    inventory.decreaseFullness(); 
                }

                gameState.decrementExploration(); // Exploration attempt consumed
                return;
            }
            
            // Base success rate depends on fullness; weather further modifies drops
            int baseSuccessRate = inventory.getFullnessLevel() > 0 ? 65 : 50; 
            double weatherMultiplier = gameState.getTodayWeather().multiplier;

            // Ask DropTable to generate materials based on place, success, weather
            List<String> found = dropTable.getMaterials(selectedPlace, baseSuccessRate, weatherMultiplier); 

            // Nothing found this time
            if (found.isEmpty()) {
                System.out.println("No luck. You didn't find any material.");
                gameState.decrementExploration(); 
                return;
            }
            
            // Kino doubles all findings when revived
            if (gameState.isKinoRevived()) {
                System.out.println("Kino's blessing doubled your findings!");
                List<String> duplicated = new ArrayList<>(found);
                found.addAll(duplicated);
            }

            // Bem currently provides flavor/immersion message
            if (gameState.isBemRevived()) {
                System.out.println("Bem accompanies you. You feel safer.");
            }

            // Add each found item to inventory as raw materials
            for (String item : found) {
                inventory.addRawMaterial(item, 1);
            }

            System.out.println("You found: " + found);
            gameState.decrementExploration();  // Use up 1 exploration attempt
            
        } catch (PlaceNotFoundException e) {
            // Handles invalid or locked location choices
            System.out.println("Explore error: " + e.getMessage());
        } catch (Exception e) {
            // Fallback in case of unexpected runtime errors
            System.out.println("Unexpected error during explore: " + e.getMessage());
        }
    }
}
