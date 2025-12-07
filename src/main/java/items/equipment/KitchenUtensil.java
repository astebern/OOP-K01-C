package items.equipment;

import items.Item;
import items.Preparable;
import java.util.ArrayList;
import java.util.List;

public abstract class KitchenUtensil extends Item {
    protected List<Preparable> contents;

    public KitchenUtensil() {
        this.contents = new ArrayList<>();
    }

    public List<Preparable> getContents() {
        return contents;
    }

    public void addContent(Preparable item) {
        contents.add(item);
    }
    
    public void clearContents() {
        contents.clear();
    }
}