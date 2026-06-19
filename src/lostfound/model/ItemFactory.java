
package lostfound.model;

import java.time.LocalDate;


public class ItemFactory {
    public static Item createItem(String itemType, String itemName, String description, 
                                  Category category, String color, String location, 
                                  LocalDate date, String storeAt, String accountID) {
        
        if (itemType == null) {
            throw new IllegalArgumentException("Item type cannot be null.");
        }

        switch (itemType.toUpperCase()) {
            case "LOST":
                // Maps directly to the ItemLost constructor:
                // ItemLost(itemName, description, category, color, locationLost, dateLost, reporterID)
                return new ItemLost(itemName, description, category, color, location, date, accountID);

            case "FOUND":
                // Maps directly to your ItemFound constructor:
                // ItemFound(itemName, description, category, color, locationFound, dateFound, storeAt, submitterID)
                return new ItemFound(itemName, description, category, color, location, date, storeAt, accountID);

            default:
                throw new IllegalArgumentException("Unknown item type: " + itemType);
        }
    }
}
