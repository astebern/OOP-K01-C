package stations;


import java.util.ArrayList;
import java.util.List;

import entities.Chef;
import items.Item;
import items.Preparable;
import items.equipment.Plate;
import utils.Position;


public class AssemblyStation extends Station {

    private List<Preparable> contents = new ArrayList<>();

    private Item itemOnStation;

    public AssemblyStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {
        Item carried = chef.getInventory();

        if (itemOnStation != null) {
            if (carried == null) {
                chef.setInventory(itemOnStation);

                if (itemOnStation instanceof Preparable) {
                    contents.clear();
                }

                itemOnStation = null;
                return;
            }

            if (canDoPlating(carried, itemOnStation)) {
                doPlating(carried, itemOnStation, chef);
                return;
        }

        return;
    }
        
        if (carried == null) {

            return;
        }

            if (carried instanceof Preparable || carried instanceof Plate) {
                itemOnStation = carried;
                
                if (carried instanceof Preparable) {
                    contents.clear();
                    contents.add((Preparable) carried);
                }
                chef.setInventory(null);
            }
            
        }

        private boolean canDoPlating(Item a, Item b) {
            if (a instanceof Plate && b instanceof Preparable) {
                return ((Preparable) b).canBePlacedOnPlate();
            }

            if (a instanceof Preparable && b instanceof Plate) {
                return ((Preparable) a).canBePlacedOnPlate();
            }
            return false;
        }

        private void doPlating(Item carried, Item onStation, Chef chef) {
            

        if (carried instanceof Plate && onStation instanceof Preparable) {
            Plate plate = (Plate) carried;
            Preparable prep = (Preparable) onStation;

            if (!prep.canBePlacedOnPlate()) return;

            plate.addContent(prep);   
            if (onStation == itemOnStation) {
                itemOnStation = null;
                contents.clear();
            }
            return;
        }

        if (onStation instanceof Plate && carried instanceof Preparable) {
            Plate plate = (Plate) onStation;
            Preparable prep = (Preparable) carried;

            if (!prep.canBePlacedOnPlate()) return;

            plate.addContent(prep);
            contents.add(prep);

            chef.setInventory(null);

        }
    }

    public Item getItemOnStation() {
        return itemOnStation;
    }

    public List<Preparable> getContents() {
        return contents;
    }

    public void setItemOnStation(Item itemOnStation) {
        this.itemOnStation = itemOnStation;
    }

    public void setContents(List<Preparable> contents) {
        this.contents = contents;
    }


}