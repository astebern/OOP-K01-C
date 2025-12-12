package stations;



import entities.Chef;
import items.Item;
import items.equipment.KitchenUtensil;
import utils.Position;


public class TrashStation extends Station {

    public TrashStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {
        Item carried = chef.getInventory();

        if (carried == null) {
            return;
        }
        if (carried instanceof KitchenUtensil) {
            KitchenUtensil utensil = (KitchenUtensil) carried;
            utensil.clearContents();
            return;
        } 
            destroyItem(carried);
            chef.setInventory(null);
        }

    

    private void destroyItem(Item item) {
        
    }

}

