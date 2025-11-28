import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DropTable {
    private final Random random = new Random();
    private final Inventory inventory;

    public DropTable(Inventory inventory) {
        this.inventory = inventory;
    }

    public List<String> getMaterials(Place place, int baseSuccessRate, double weatherMultiplier) { 
        List<String> found = new ArrayList<>();
        
        if (place == Place.DEEP_SEA_SHORE) {
             found.addAll(rollDeepSeaGacha(weatherMultiplier));
             return found;
        }

        for (Map.Entry<String, Integer> entry : place.commonDrops.entrySet()) {
            double baseChance = entry.getValue() * (baseSuccessRate / 50.0); 
            int effectiveChance = (int) (baseChance * weatherMultiplier / 2.0); 
            
            if (random.nextInt(100) < effectiveChance) {
                found.add(entry.getKey());
            }
        }
        
        for (Map.Entry<String, Integer> entry : place.rareDrops.entrySet()) {
            int chance = (int) (entry.getValue() * weatherMultiplier);
            if (random.nextInt(100) < chance) {
                found.add(entry.getKey());
            }
        }
        
        return found;
    }
    
    private List<String> rollDeepSeaGacha(double weatherMultiplier) {
        List<String> found = new ArrayList<>();
        Random r = new Random();
        
        System.out.println("Deep Sea Scan initiated (20% base drop chance)...");
        
        List<String> foundThisTurn = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : Place.DEEP_SEA_SHORE.commonDrops.entrySet()) {
            int effectiveChance = (int) (20 * weatherMultiplier); 

            if (r.nextInt(100) < effectiveChance) {
                found.add(entry.getKey());
                foundThisTurn.add(entry.getKey());
            }
        }
        
        // This relies on Inventory methods now set to public
        for (String item : foundThisTurn) {
            inventory.addDeepSeaItemFound(item);
        }
        
        if (inventory.getUniqueDeepSeaItemsCount() >= 3) {
            found.add("Painite");
            inventory.resetDeepSeaItemTracker();
            System.out.println("\n*** GACHA BONUS! You found 3 different minerals! Guaranteed **Painite** drop! ***");
        }

        return found;
    }

    public boolean checkHazard() {
        return random.nextInt(100) < 5;
    }
}