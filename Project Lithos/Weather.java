import java.util.Random;

// Enum representing different types of weather,
// each with a description, flavor text, and yield multiplier.
public enum Weather {
    STORM("Storm Day", "Exploration yields reduced by 50%.", 0.50),
    BRIGHT_SUN("Bright Sunny Day", "Exploration yields increased by 10%.", 1.10),
    CLEAR_SUN("Clear Sunny Day", "Exploration yields increased by 25%.", 1.25),
    NORMAL("Normal Day", "Exploration yields are standard.", 1.00);

    // Human-readable description of the weather
    public final String description;
    // Extra flavor text for immersion
    public final String flavorText;
    // Multiplier applied to exploration yields
    public final double multiplier;

    // Constructor for the enum constants
    Weather(String description, String flavorText, double multiplier) {
        this.description = description;
        this.flavorText = flavorText;
        this.multiplier = multiplier;
    }
    
    // Generates a random weather condition based on weighted chances:
    public static Weather generate() {
        Random r = new Random();
        int roll = r.nextInt(100); // Generates a number from 0 to 99
        
        if (roll < 10) return STORM;        // 10% chance
        if (roll < 25) return CLEAR_SUN;    // Next 15% (total 25%)
        if (roll < 50) return BRIGHT_SUN;   // Next 25% (total 50%)
        return NORMAL;                      // Remaining 50%
    }
}
