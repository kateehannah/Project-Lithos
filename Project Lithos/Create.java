import java.util.Random;
import java.util.Scanner;

public class Create {
    private final Inventory inventory;
    private final Scanner sc = new Scanner(System.in);

    public Create(Inventory inventory) {
        this.inventory = inventory;
    }

    public void enterCreateMenu() {
        while(true) {
            showCraftable();
            
            System.out.println("\nEnter item name to craft, or type 'back' to return to Main Menu:");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                break;
            }
            
            craftItem(input);
        }
    }
    
    public void showCraftable() {
        System.out.println("\n--- Crafting Suggestions (70% Base Success Rate) ---");
        
        boolean canMakeFurnace = inventory.getMaterialCount("Stone") >= 3 && !inventory.hasFurnace();
        boolean canMakeAlchemy = inventory.hasMaterial("Stone") && inventory.hasMaterial("Wood") && inventory.getMaterialCount("Painite") > 0 && !inventory.hasAlchemyTable();
        boolean canMakeSpear = inventory.hasMaterial("Wood") && inventory.hasMaterial("Stone"); 
        boolean canMakeBatea = inventory.getMaterialCount("Wood") >= 3; 
        
        if (inventory.getGameState().isCraftAlwaysSuccessful()) {
             System.out.println("\n*** Bem's Effect: 100% Craft Success! ***");
        }

        System.out.println("These are the items you can make:");
        if (canMakeFurnace) System.out.println("- **Furnace** (Needs: 3 Stone - Permanent Structure)");
        if (canMakeAlchemy) System.out.println("- **Alchemy Table** (Needs: Wood + Stone + Painite - Permanent Structure)");
        if (canMakeBatea) System.out.println("- **Wooden Batea** (Needs: 3 Wood - Reusable Tool for Gold Panning)"); 
        if (canMakeSpear) System.out.println("- **Spear** (Needs: Wood + Stone - Consumable Weapon for Hunting)"); 
        
        if (!canMakeFurnace && !canMakeAlchemy && !canMakeSpear && !canMakeBatea) {
             System.out.println("No immediate craftable items available.");
        }
        System.out.println("---");
    }
    
    private boolean checkCraftSuccess() {
        if (inventory.getGameState().isCraftAlwaysSuccessful()) {
            System.out.println("Bem's presence ensures success!");
            return true;
        }
        return new Random().nextInt(100) < 70;
    }


    private void craftItem(String itemName) {
        String normalized = itemName.toLowerCase();
        
        switch (normalized) {
            case "spear" -> craftSpear();
            case "furnace" -> craftFurnace();
            case "alchemy table", "alchemy" -> craftAlchemyTable();
            case "wooden batea", "batea" -> craftWoodenBatea();
            default -> System.out.println("Unknown craftable: " + itemName);
        }
    }
    
    private void craftFurnace() {
        if (inventory.hasFurnace()) {
            System.out.println("The Furnace is already built!");
            return;
        }
        
        if (inventory.getMaterialCount("Stone") >= 3) {
            if (checkCraftSuccess()) {
                inventory.useMaterial("Stone", 3);
                inventory.registerStructure("Furnace");
                System.out.println("Success! You constructed: **Furnace**! (Used 3 Stone)");
            } else {
                inventory.useMaterial("Stone", 3);
                System.out.println("Failure! The furnace collapsed during construction. Materials lost.");
            }
        } else {
            System.out.println("Insufficient materials to craft Furnace (Needs: 3 Stone).");
        }
    }

    private void craftAlchemyTable() {
        if (inventory.hasAlchemyTable()) {
             System.out.println("The Alchemy Table is already built!");
             return;
        }
        if (inventory.hasMaterial("Stone") && inventory.hasMaterial("Wood") && inventory.getMaterialCount("Painite") > 0) {
            
            if (checkCraftSuccess()) {
                inventory.useMaterial("Stone");
                inventory.useMaterial("Wood");
                inventory.useMaterial("Painite"); 

                inventory.registerStructure("Alchemy Table");
                System.out.println("Success! You constructed: **Alchemy Table**!");
            } else {
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