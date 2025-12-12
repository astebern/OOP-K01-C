package items.equipment;

public class Plate extends KitchenUtensil {
    private boolean isDirty;

    public Plate() {
        super(); 
        this.isDirty = false;
    }

    public boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public void setIsDirty() {
        this.contents.clear();
        this.isDirty = true;
    }
    
    @Override
    public String toString() {
        String status = isDirty ? "Dirty" : "Clean";
        return "Plate [" + status + "] - Contents: " + contents.size();
    }
}