package stations;

public class CookingStation extends Station {
    
    public enum CookingEquipment {
        BOILING_POT,
        FRYING_PAN
    }

    private CookingEquipment equipment;

    public CookingStation() {
        this.equipment = null; // Will be determined by utensil placed on the station
    }

    public CookingStation(CookingEquipment equipment) {
        this.equipment = equipment;
    }

    public CookingEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(CookingEquipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return "CookingStation(" + equipment + ")";
    }
}

