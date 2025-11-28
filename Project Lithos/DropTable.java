import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DropTable {
    private final Random random = new Random(); // Used for drop chances
    private final Inventory inventory;          // Reference to player's inventory

    public DropTable(Inventory inventory) {
        this.inventory = inventory; // Store inventory reference
    }

    // Main material drop generator based on location, success rate, and weather multiplier
    public List<String> getMaterials(Place place, int baseSuccessRate, double weatherMultiplier) { 
        List<String> found = new ArrayList<>();
        
        // Special case: Deep Sea uses its own gacha-based drop method
        if (place == Place.DEEP_SEA_SHORE) {
             found.addAll(rollDeepSeaGacha(weatherMultiplier));
             return found;
        }

        // Roll for common drops
        for (Map.Entry<String, Integer> entry : place.commonDrops.entrySet()) {
            // Scale chance by player's tools or skill (baseSuccessRate)
            double baseChance = entry.getValue() * (baseSuccessRate / 50.0); 
            
            // Weather boosts or reduces chances
            int effectiveChance = (int) (baseChance * weatherMultiplier / 2.0); 
            
            // Roll against chance
            if (random.nextInt(100) < effectiveChance) {
                found.add(entry.getKey());
            }
        }
        
        // Roll for rare drops
        for (Map.Entry<String, Integer> entry : place.rareDrops.entrySet()) {
            int chance = (int) (entry.getValue() * weatherMultiplier); // Weather applies directly
            if (random.nextInt(100) < chance) {
                found.add(entry.getKey());
            }
        }
        
        return found; // Return all found materials
    }
    
    // Special loot system for Deep Sea: 20% base chance gacha mechanic
    private List<String> rollDeepSeaGacha(double weatherMultiplier) {
        List<String> found = new ArrayList<>();
        Random r = new Random();
        
        System.out.println("Deep Sea Scan initiated (20% base drop chance)...");
        
        List<String> foundThisTurn = new ArrayList<>(); // Track unique drops this scan

        // Roll for each Deep Sea common item
        for (Map.Entry<String, Integer> entry : Place.DEEP_SEA_SHORE.commonDrops.entrySet()) {
            int effectiveChance = (int) (20 * weatherMultiplier); // 20% base × weather

            if (r.nextInt(100) < effectiveChance) {
                found.add(entry.getKey());
                foundThisTurn.add(entry.getKey());
            }
        }
        
        // Log found minerals into inventory to track unique finds
        for (String item : foundThisTurn) {
            inventory.addDeepSeaItemFound(item);
        }
        
        // Gacha bonus: if 3 different items have been found across attempts, drop Painite
        if (inventory.getUniqueDeepSeaItemsCount() >= 3) {
            found.add("Painite"); // Guaranteed rare bonus
            inventory.resetDeepSeaItemTracker(); // Reset tracker for next gacha cycle
            System.out.println("\n*** GACHA BONUS! You found 3 different minerals! Guaranteed **Painite** drop! ***");
        }

        return found;
    }

    // 5% chance to trigger a hazard (storm, monster, etc.)
    public boolean checkHazard() {
        return random.nextInt(100) < 5;
    }
}
