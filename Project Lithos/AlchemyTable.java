import java.util.Random;
import java.util.Scanner;

public class AlchemyTable {
    private final Inventory inventory;
    private final Scanner sc = new Scanner(System.in);

    public AlchemyTable(Inventory inventory) {
        this.inventory = inventory;
    }
    
    public void enterAlchemyMenu() {
        if (!inventory.hasAlchemyTable()) {
            System.out.println("You need to craft the Alchemy Table first!");
            return;
        }

        while (true) {
            System.out.println("\n--- Alchemy Table Experiments ---");
            System.out.println("1. Ferment Grapes into Alcohol (Grapes: " + inventory.getMaterialCount("Grapes") + ")");
            System.out.println("2. Mix Revival Potion (Needs: Alcohol: " + inventory.getMaterialCount("Alcohol") + 
                               ", Nitric Acid: " + inventory.getMaterialCount("Nitric Acid") + ")");
            System.out.println("   (Optional: Add Platinum for **2x Potion Yield**)");
            System.out.println("Type 'back' to return.");
            System.out.print("Enter choice (1/2/back): ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("back")) break;

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> processGrapes();
                    case 2 -> mixRevivalPotion();
                    default -> System.out.println("Invalid choice.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number or 'back'.");
            }
        }
    }

    public void processGrapes() {
        int grapeCount = inventory.getMaterialCount("Grapes");
        
        if (grapeCount < 1) {
            System.out.println("You need at least 1 Grape to ferment into Alcohol.");
            return;
        }

        System.out.print("Ferment 1 Grape into Alcohol? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            if (inventory.useMaterial("Grapes")) {
                inventory.addRawMaterial("Alcohol", 1);
                System.out.println("Success! You turned 1 Grape into **Alcohol**.");
            }
        }
    }
    
    private void mixRevivalPotion() {
        if (inventory.hasMaterial("Nitric Acid") && inventory.hasMaterial("Alcohol")) {
            
            int potionsCreated = 1;
            boolean usedPlatinum = inventory.hasMaterial("Platinum");

            if (usedPlatinum) {
                System.out.print("Platinum detected. Use 1 Platinum for 2x Revival Potions? (y/n): ");
                if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                    potionsCreated = 2;
                } else {
                    usedPlatinum = false;
                }
            }

            System.out.print("Final mix confirmation (Needs: Nitric Acid + Alcohol)? (y/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("y")) {

                if (checkCraftSuccess()) {
                    inventory.useMaterial("Nitric Acid");
                    inventory.useMaterial("Alcohol");

                    if (usedPlatinum) {
                        inventory.useMaterial("Platinum");
                        System.out.println("💎 Platinum added! The yield is doubled!");
                    }

                    for (int i = 0; i < potionsCreated; i++) {
                        inventory.addCraftedItem(new CraftedItem("Revival Potion", "Revives a character"));
                    }
                    
                    System.out.println("You mixed the materials: **" + potionsCreated + " Revival Potion(s)** created!");

                } else {
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
            System.out.println("Insufficient materials to craft Revival Potion (Needs: Nitric Acid, Alcohol).");
        }
    }
    
    private boolean checkCraftSuccess() {
        if (inventory.getGameState().isCraftAlwaysSuccessful()) {
            System.out.println("Bem's presence ensures success!");
            return true;
        }
        return new Random().nextInt(100) < 70;
    }
}
