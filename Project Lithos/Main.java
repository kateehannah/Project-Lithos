import java.util.*;

public class Main {

    // Scanner for user input
    private final Scanner sc = new Scanner(System.in);

    // Core game systems
    private final Inventory inventory;
    private final GameState gameState;
    private final Explore explore;
    private final Create create;
    private final GuideBook guideBook;
    private final AlchemyTable alchemyTable;
    
    // Ensures intro only plays once
    private static boolean introPlayed = false; 

    // Intro story text
    private static final String INTRO_TEXT_PART_1 = "Over the horizon, a vast blue light swelled and crept across the land.";
    private static final String INTRO_TEXT_PART_2 = "When it finally reached you !BOOM! everything changed. The moment it touched your skin, you turned to stone. Every conscious being did.";
    private static final String INTRO_TEXT_PART_3 = "Civilization shattered. Time washed over the world like a tidal wave.";
    private static final String INTRO_TEXT_PART_4 = "3,000 years later, you awaken.";
    private static final String INTRO_TEXT_PART_5 = "Alone.";
    private static final String INTRO_TEXT_PART_6 = "All around you stand the petrified faces of the past.";
    private static final String INTRO_TEXT_PART_7 = "Kino, your high-school best friend, a genius in geography.";
    private static final String INTRO_TEXT_PART_8 = "Bem, the master craftsman of your class.";
    private static final String INTRO_TEXT_PART_9 = "Akio, the athletic, ambitious senior who never backed down from a challenge.";
    private static final String INTRO_TEXT_PART_10 = "They're frozen, waiting.";
    private static final String INTRO_TEXT_PART_11 = "Humanity has fallen silent and you are its first voice in millennia.";
    private static final String INTRO_TEXT_PART_12 = "Your mission: restore civilization's greatest weapons; knowledge, technology, and HOPE.";
    private static final String INTRO_TEXT_PART_13 = "Your choices will determine the future.";

    public Main() {
        // Create a new game state
        this.gameState = new GameState(null); 
        
        // Create inventory and link it to gameState
        this.inventory = new Inventory(this.gameState); 
        this.gameState.setInventory(this.inventory); 
        
        // Initialize other systems
        this.explore = new Explore(this.gameState); 
        this.create = new Create(this.inventory); 
        this.guideBook = new GuideBook(); 
        this.alchemyTable = new AlchemyTable(this.inventory); 
    }

    public static void main(String[] args) {
        Main game = new Main();
        game.run(); // Start the game
    }

    public void run() {
        System.out.println("Welcome to Project Lithos!");
        
        // Show intro only once
        if (!introPlayed) {
            startStoryIntroduction();
            introPlayed = true;
        }

        // Main game loop
        while (true) {

            // Check if player won
            if (checkGameOver()) {
                exitGame();
                return;
            }
            
            // Show menu options
            displayMainMenu();
            String input = sc.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);

                // Handle menu selection
                switch (choice) {
                    case 1 -> explore.enterExploreMenu();
                    case 2 -> create.enterCreateMenu();
                    case 3 -> enterInventoryMenu();
                    case 4 -> guideBook.displayGuideBook();
                    case 5 -> enterAlchemyMenu();
                    case 6 -> enterSleepMenu();
                    case 7 -> { exitGame(); return; }
                    default -> System.out.println("Invalid choice. Please try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    // Ending condition check
    private boolean checkGameOver() {
        if (gameState.allCompanionsRevived()) {
            System.out.println("\n-----------------------------------------------------");
            System.out.print("You have successfully restored humanity's greatest tool . . . . ");
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            System.out.print("HOPE.");
            System.out.println("\n-----------------------------------------------------");
            System.out.print("[Enter] Thank you for playing, Project Lithos.");
            sc.nextLine();
            return true;
        }
        return false;
    }
    
    // Sleep → advances the day
    private void enterSleepMenu() {
        System.out.println("\nDo you wish to Sleep? This will advance the day.");
        System.out.print("Type 'y' to sleep, or 'n' to cancel: ");

        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            gameState.sleep();
        }
    }

    // Plays the intro story text
    private void startStoryIntroduction() {
        System.out.println("\n");
        TextUtil.type(INTRO_TEXT_PART_1);
        System.out.println();
        TextUtil.type(INTRO_TEXT_PART_2);
        TextUtil.type(INTRO_TEXT_PART_3);
        System.out.println();
        TextUtil.typeDotCrack();
        System.out.println();
        TextUtil.type(INTRO_TEXT_PART_4);
        System.out.println();
        TextUtil.type(INTRO_TEXT_PART_5);
        System.out.println();
        TextUtil.type(INTRO_TEXT_PART_6);
        TextUtil.type(INTRO_TEXT_PART_7);
        TextUtil.type(INTRO_TEXT_PART_8);
        TextUtil.type(INTRO_TEXT_PART_9);
        TextUtil.type(INTRO_TEXT_PART_10);
        System.out.println();
        TextUtil.type(INTRO_TEXT_PART_11);
        TextUtil.type(INTRO_TEXT_PART_12);
        TextUtil.type(INTRO_TEXT_PART_13);
        System.out.println("--------------------------");
        
        System.out.print("\nPress ENTER to begin Day " + gameState.getCurrentDay() + "...");
        sc.nextLine();
        System.out.println("\n");
    }

    // Displays the main option menu
    private void displayMainMenu() {
        System.out.println("\n--- (Day " + gameState.getCurrentDay() + ") ---");
        System.out.println("1. Explore [" + gameState.getExplorationsLeft() + " attempts left]");
        System.out.println("2. Create");
        System.out.println("3. Inventory & Tools");
        System.out.println("4. Guide Book");
        System.out.println("5. Alchemy");
        System.out.println("6. Sleep");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    // Inventory screen + tool usage
    private void enterInventoryMenu() {
        while(true) {
            System.out.println("\n--- INVENTORY & TOOLS ---");
            
            // Show resource list
            inventory.showInventory(); 
            
            // Show crafted tools and food
            inventory.processToolsAndFood();

            int craftedCount = inventory.getCraftedItemCount();

            // Display instructions
            if (craftedCount == 0) {
                 System.out.println("\nNo crafted items to use. Type 'back' to return.");
            } else {
                 System.out.println("\nSelect crafted item to use (1-" + craftedCount + "), or type 'back' to return:");
            }
            
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("back")) break;

            try {
                int choice = Integer.parseInt(input);

                // Use selected crafted item
                if (choice >= 1 && choice <= craftedCount) {
                    inventory.useCraftedItem(choice - 1);
                } else {
                    System.out.println("Invalid selection.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number or 'back'.");
            }
        }
    }
    
    // Opens alchemy crafting menu
    private void enterAlchemyMenu() {
        alchemyTable.enterAlchemyMenu();
    }

    // Quit game and close scanner
    private void exitGame() {
        System.out.println("Thank you for playing. Goodbye!");
        try { sc.close(); } catch (Exception ignored) {}
    }
}
