package lostfound.model;

public enum Category {

    KEYS("Keys"),
    WATER_BOTTLE("Water Bottle"),
    MATRIC_ID("Matric ID / Student Card"),
    ELECTRONICS("Electronics"),
    CLOTHING("Clothing"),
    WALLET("Wallet / Purse"),
    BAG("Bag / Backpack"),
    STATIONERY("Stationery"),
    OTHERS("Others");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
