public class PlaceNotFoundException extends Exception {

    // Custom exception used when a player tries to explore a place
    // that is locked, unknown, or not accessible yet.
    public PlaceNotFoundException(String message) {
        super(message); // Pass the error message to the Exception superclass
    }
}
