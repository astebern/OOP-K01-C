package items.equipment;

import items.Preparable;

public interface CookingDevice {
    boolean isPortable();

    int getCapacity();

    boolean canAccept(Preparable item);

    void addIngredient(Preparable item);

    void removeIngredient(Preparable item);

    void startCooking();
}