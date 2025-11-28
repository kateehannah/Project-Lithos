import java.util.Random;
import java.util.Scanner;

public class Create {
    private final Inventory inventory;     // Player inventory reference
    private final Scanner sc = new Scanner(System.in); // Input reader

    public Create(Inventory inventory) {
        this.inventory = inventory;
    }

    public void enterCreateMenu() {
        // Main crafting menu loop
        while(true) {
            showCraftable(); // Show what player can craft
            
            System.out.println("\nEnter item name to craft, or type 'back' to return to Main Menu:");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                break; // Exit crafting menu
            }
            
            craftItem(input); // Attempt to craft chosen item
        }
    }
    
    public void showCraftable() {
        System.out.println("\n--- Crafting Suggestions (70% Base Success Rate) ---");
        
        // Conditions for each craftable item
        boolean canMakeFurnace = inventory.getMaterialCount("Stone") >= 3 && !inventory.hasFurnace();
        boolean canMakeAlchemy = inventory.hasMaterial("Stone") && inventory.hasMaterial("Wood") && inventory.getMaterialCount("Painite") > 0 && !inventory.hasAlchemyTable();
        boolean canMakeSpear = inventory.hasMaterial("Wood") && inventory.hasMaterial("Stone"); 
        boolean canMakeBatea = inventory.getMaterialCount("Wood") >= 3; 
        
        // Game state that forces success
        if (inventory.getGameState().isCraftAlwaysSuccessful()) {
             System.out.println("\n*** Bem's Effect: 100% Craft Success! ***");
        }

        System.out.println("These are the items you can make:");

        // Only show items the player currently has materials for
        if (canMakeFurnace) System.out.println("- **Furnace** (Needs: 3 Stone - Permanent Structure)");
        if (canMakeAlchemy) System.out.println("- **Alchemy Table** (Needs: Wood + Stone + Painite - Permanent Structure)");
        if (canMakeBatea) System.out.println("- **Wooden Batea** (Needs: 3 Wood - Reusable Tool for Gold Panning)"); 
        if (canMakeSpear) System.out.println("- **Spear** (Needs: Wood + Stone - Consumable Weapon for Hunting)"); 
        
        // If none are available
        if (!canMakeFurnace && !canMakeAlchemy && !canMakeSpear && !canMakeBatea) {
             System.out.println("No immediate craftable items available.");
        }
        System.out.println("---");
    }
    
    private boolean checkCraftSuccess() {
        // Instant success if buff is active
        if (inventory.getGameState().isCraftAlwaysSuccessful()) {
            System.out.println("Bem's presence ensures success!");
            return true;
        }

        // Normal success: 70% probability
        return new Random().nextInt(100) < 70;
    }

    private void craftItem(String itemName) {
        // Normalize input for flexible matching
        String normalized = itemName.toLowerCase();
        
        // Routes to the correct crafting method
        switch (normalized) {
            case "spear" -> craftSpear();
            case "furnace" -> craftFurnace();
            case "alchemy table", "alchemy" -> craftAlchemyTable();
            case "wooden batea", "batea" -> craftWoodenBatea();
            default -> System.out.println("Unknown craftable: " + itemName);
        }
    }
    
    private void craftFurnace() {
        // Can't craft twice (structures are permanent)
        if (inventory.hasFurnace()) {
            System.out.println("The Furnace is already built!");
            return;
        }
        
        // Check material requirement
        if (inventory.getMaterialCount("Stone") >= 3) {
            if (checkCraftSuccess()) {
                // Use materials then register structure
                inventory.useMaterial("Stone", 3);
                inventory.registerStructure("Furnace");
                System.out.println("Success! You constructed: **Furnace**! (Used 3 Stone)");
            } else {
                // Materials consumed even on failure
                inventory.useMaterial("Stone", 3);
                System.out.println("Failure! The furnace collapsed during construction. Materials lost.");
            }
        } else {
            System.out.println("Insufficient materials to craft Furnace (Needs: 3 Stone).");
        }
    }

    private void craftAlchemyTable() {
        // Prevent building duplicates
        if (inventory.hasAlchemyTable()) {
             System.out.println("The Alchemy Table is already built!");
             return;
        }

        // Check all required materials
        if (inventory.hasMaterial("Stone") && inventory.hasMaterial("Wood") && inventory.getMaterialCount("Painite") > 0) {
            
            if (checkCraftSuccess()) {
                // Consume materials
                inventory.useMaterial("Stone");
                inventory.useMaterial("Wood");
                inventory.useMaterial("Painite");

                // Register the structure
                inventory.registerStructure("Alchemy Table");
                System.out.println("Success! You constructed: **Alchemy Table**!");
            } else {
                // Fail: consume everything
                inventory.useMaterial("Stone");
                inventory.useMaterial("Wood");
                inventory.useMaterial("Painite");
                System.out.println("Failure! The table shattered during assembly. Materials lost.");
            }

        } else {
            System.out.println("Insufficient materials to craft Alchemy Table (Needs: Wood, Stone, Painite).");
        }
    }
    
    private void craftWoodenBatea() { 
        // Needs 3 wood
        if (inventory.getMaterialCount("Wood") >= 3) {
            if (checkCraftSuccess()) {
                inventory.useMaterial("Wood", 3);
                inventory.addCraftedItem(new CraftedItem("Wooden Batea", "Reusable Tool for Gold Panning"));
                System.out.println("Success! You crafted: **Wooden Batea**! (Used 3 Wood)");
            } else {
                inventory.useMaterial("Wood", 3);
                System.out.println("Failure! The wood split badly. Materials lost.");
            }
        } else {
            System.out.println("Insufficient materials to craft Wooden Batea (Needs: 3 Wood).");
        }
    }
    
    private void craftSpear() {
        // Requirements: 1 wood, 1 stone
        if (inventory.hasMaterial("Wood") && inventory.hasMaterial("Stone")) {
            if (checkCraftSuccess()) {
                inventory.useMaterial("Wood");
                inventory.useMaterial("Stone");
                inventory.addCraftedItem(new CraftedItem("Spear", "Consumable Weapon for Hunting"));
                System.out.println("Success! You crafted: **Spear** (Consumable Weapon)");
            } else {
                inventory.useMaterial("Wood");
                inventory.useMaterial("Stone");
                System.out.println("Failure! The stone broke when you tried to bind it. Materials lost.");
            }
        } else {
            System.out.println("Insufficient materials to craft Spear (Needs: Wood, Stone).");
        }
    }
}
