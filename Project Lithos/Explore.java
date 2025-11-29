import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Explore {
    private final GameState gameState;
    private final Inventory inventory;
    private final Random random = new Random();
    private final Scanner sc = new Scanner(System.in);
    private final DropTable dropTable;

    public Explore(GameState gameState) {
        this.gameState = gameState;
        this.inventory = gameState.getInventory();
        this.dropTable = new DropTable(inventory);
    }

    public void enterExploreMenu() {
        while(true) {
            if (gameState.getExplorationsLeft() <= 0) {
                System.out.println("\n--- Energy Exhausted ---");
                System.out.println("You have reached the daily exploration limit (10).");
                System.out.println("You must \'Sleep\' to advance to the next day, or eat \'Cooked Meat\' to continue today.");
                System.out.print("Type 'back' to return to Main Menu: ");
                sc.nextLine(); 
                break;
            }
            
            System.out.println("\n--- Exploration (Day " + gameState.getCurrentDay() + ": " + gameState.getExplorationsLeft() + " attempts left) ---");
            System.out.println("Weather: " + gameState.getTodayWeather().description + " (" + gameState.getTodayWeather().flavorText + ")");
            
            String places = "1. River  2. Forest  3. Rocky Beach" + (gameState.hasMapFragment() ? "  4. Cave" : "");
            if (gameState.getCurrentDay() >= 5) {
                places += "  5. Deep Sea Shore";
            }
            
            System.out.println("Available Places: " + places);
            System.out.print("Enter place (Name/Number) or 'back': ");
            String placeInput = sc.nextLine().trim();

            if (placeInput.equalsIgnoreCase("back")) {
                break;
            }
            
            explorePlace(placeInput);
            inventory.decreaseFullness(); 
        }
    }

    public void explorePlace(String placeInput) {
        try {
            Place selectedPlace;
            String normalized = placeInput.trim().toLowerCase();

            selectedPlace = switch (normalized) {
                case "1", "river" -> Place.RIVER;
                case "2", "forest" -> Place.FOREST;
                case "3", "rocky beach" -> Place.ROCKY_BEACH;
                case "4", "cave" -> {
                    if (!gameState.hasMapFragment()) {
                        throw new PlaceNotFoundException("The Cave is hidden. You need to find a **Map Fragment** first!");
                    }
                    yield Place.CAVE;
                }
                case "5", "deep sea shore" -> {
                    if (gameState.getCurrentDay() < 5) {
                        throw new PlaceNotFoundException("The Deep Sea Shore is too dangerous before Day 5.");
                    }
                    yield Place.DEEP_SEA_SHORE;
                }
                default -> throw new PlaceNotFoundException("Unknown place: " + placeInput);
            };

            if (gameState.isAkioRevived()) {
                if (gameState.getCurrentDay() >= 10 && random.nextInt(100) < 25) { 
                    if (inventory.getCraftedItemCount() > 0) {
                        inventory.removeRandomCraftedItem();
                        System.out.println("\n!!! AKIO ATTACK !!! Akio stole a **Crafted Item** and vanished!");
                        gameState.decrementExploration();
                        return;
                    }
                }
                System.out.println("\nAkio revived! He stole all your things... Inventory reset.");
                gameState.reviveAkio();
                return;
            }

            System.out.println("\nExploring the " + selectedPlace.name + "...");

            if (dropTable.checkHazard()) {
                System.out.println("Oh no! You encountered a hazard.");
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
                gameState.decrementExploration(); 
                return;
            }
            
            int baseSuccessRate = inventory.getFullnessLevel() > 0 ? 65 : 50; 
            double weatherMultiplier = gameState.getTodayWeather().multiplier;

            List<String> found = dropTable.getMaterials(selectedPlace, baseSuccessRate, weatherMultiplier); 

            if (found.isEmpty()) {
                System.out.println("No luck. You didn't find any material.");
                gameState.decrementExploration(); 
                return;
            }
            
            if (gameState.isKinoRevived()) {
                System.out.println("Kino's blessing doubled your findings!");
                List<String> duplicated = new ArrayList<>(found);
                found.addAll(duplicated);
            }

            if (gameState.isBemRevived()) {
                System.out.println("Bem accompanies you. You feel safer.");
            }

            for (String item : found) {
                inventory.addRawMaterial(item, 1);
            }

            System.out.println("You found: " + found);
            gameState.decrementExploration(); 
            
        } catch (PlaceNotFoundException e) {
            System.out.println("Explore error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during explore: " + e.getMessage());
        }
    }
}