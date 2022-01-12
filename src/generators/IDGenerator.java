package generators;

public class IDGenerator {
    private static int idCount = 0;

    public static int getID() {
        return idCount++;
    }
}
