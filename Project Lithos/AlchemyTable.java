import java.util.Random;
import java.util.Scanner;

public class AlchemyTable {
    private final Inventory inventory;     // Reference to player's inventory
    private final Scanner sc = new Scanner(System.in); // For user input

    public AlchemyTable(Inventory inventory) {
        this.inventory = inventory;
    }
    
    public void enterAlchemyMenu() {
        // Check if player has crafted an Alchemy Table
        if (!inventory.hasAlchemyTable()) {
            System.out.println("You need to craft the Alchemy Table first!");
            return;
        }

        // Main alchemy interaction loop
        while (true) {
            System.out.println("\n--- Alchemy Table Experiments ---");
            System.out.println("1. Ferment Grapes into Alcohol (Grapes: " + inventory.getMaterialCount("Grapes") + ")");
            System.out.println("2. Mix Revival Potion (Needs: Alcohol: " + inventory.getMaterialCount("Alcohol") + 
                               ", Nitric Acid: " + inventory.getMaterialCount("Nitric Acid") + ")");
            System.out.println("   (Optional: Add Platinum for **2x Potion Yield**)");
            System.out.println("Type 'back' to return.");
            System.out.print("Enter choice (1/2/back): ");
            String input = sc.nextLine().trim();

            // Exit menu
            if (input.equalsIgnoreCase("back")) break;

            try {
                int choice = Integer.parseInt(input);

                // Direct to correct method
                switch (choice) {
                    case 1 -> processGrapes();
                    case 2 -> mixRevivalPotion();
                    default -> System.out.println("Invalid choice.");
                }

            } catch (NumberFormatException e) {
                // Handles non-number inputs
                System.out.println("Invalid input! Please enter a number or 'back'.");
            }
        }
    }

    public void processGrapes() {
        int grapeCount = inventory.getMaterialCount("Grapes");
        
        // Must have at least 1 grape
        if (grapeCount < 1) {
            System.out.println("You need at least 1 Grape to ferment into Alcohol.");
            return;
        }

        // Confirmation prompt
        System.out.print("Ferment 1 Grape into Alcohol? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {

            // Remove grape and add alcohol
            if (inventory.useMaterial("Grapes")) {
                inventory.addRawMaterial("Alcohol", 1);
                System.out.println("Success! You turned 1 Grape into **Alcohol**.");
            }
        }
    }
    
    private void mixRevivalPotion() {
        // Check materials
        if (inventory.hasMaterial("Nitric Acid") && inventory.hasMaterial("Alcohol")) {
            
            int potionsCreated = 1;
            boolean usedPlatinum = inventory.hasMaterial("Platinum"); // Optional bonus

            // Ask player if they want to use Platinum
            if (usedPlatinum) {
                System.out.print("Platinum detected. Use 1 Platinum for 2x Revival Potions? (y/n): ");
                if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                    potionsCreated = 2;
                } else {
                    usedPlatinum = false; // They refused to use it
                }
            }

            // Final confirmation before crafting
            System.out.print("Final mix confirmation (Needs: Nitric Acid + Alcohol)? (y/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("y")) {

                // SUCCESS CASE
                if (checkCraftSuccess()) {
                    inventory.useMaterial("Nitric Acid");
                    inventory.useMaterial("Alcohol");

                    // If platinum used, consume it and notify
                    if (usedPlatinum) {
                        inventory.useMaterial("Platinum");
                        System.out.println("💎 Platinum added! The yield is doubled!");
                    }

                    // Add crafted potions to inventory
                    for (int i = 0; i < potionsCreated; i++) {
                        inventory.addCraftedItem(new CraftedItem("Revival Potion", "Revives a character"));
                    }
                    
                    System.out.println("You mixed the materials: **" + potionsCreated + " Revival Potion(s)** created!");

                } else {
                    // FAILURE CASE — all materials lost
                    inventory.useMaterial("Nitric Acid");
                    inventory.useMaterial("Alcohol");

                    if (usedPlatinum) {
                        inventory.useMaterial("Platinum");
                        System.out.println("Failure! The mixture boiled over. Materials lost (including Platinum).");
                    } else {
                        System.out.println("Failure! The mixture boiled over. Materials lost.");
                    }
                }
            }

        } else {
            // Missing needed materials
            System.out.println("Insufficient materials to craft Revival Potion (Needs: Nitric Acid, Alcohol).");
        }
    }
    
    private boolean checkCraftSuccess() {
        // Auto-success state (e.g., for debugging or special buff)
        if (inventory.getGameState().isCraftAlwaysSuccessful()) {
            System.out.println("Bem's presence ensures success!");
            return true;
        }

        // 70% success rate
        return new Random().nextInt(100) < 70;
    }
}
