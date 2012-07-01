package ejb.util;

/**
 * Represents order of sorting data in tables
 * @author Danon
 */
public enum ReshakaSortOrder {
    ASCENDING("ASC"), DESCENDING("DESC"), UNSORTED(null);

    private final String value;
    
    private ReshakaSortOrder(String v) {
        value = v;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    
}
