package items;

public interface Preparable {
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();
    void cook();
    void chop();
    void setChoppable(boolean choppable);
    void setCookable(boolean cookable);
    void setcanPlaceOnPlate (boolean canPlace);
}

