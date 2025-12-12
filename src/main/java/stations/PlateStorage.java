package stations;


import java.util.Stack;

import entities.Chef;
import items.Item;
import items.equipment.Plate;
import utils.Position;



public class PlateStorage extends Station {

    private Stack<Plate> plates = new Stack<>();

    private static final int INITIAL_PLATE_COUNT = 4;

    public PlateStorage(Position position) {
        super(position);
       
        for (int i = 0; i < INITIAL_PLATE_COUNT; i++) {
            Plate plate = new Plate();
            plates.push(plate);
        }
    }

    @Override
    public void interact(Chef chef) {
        Item carried = chef.getInventory();

        if (carried == null) {
            if (plates.isEmpty()) {
                return;
            }
            Plate top = plates.peek();

            Plate taken = plates.pop();
            chef.setInventory(taken);
            return;

            }

        if (carried instanceof Plate) {
            Plate plate = (Plate) carried;
            plates.push(plate);
            chef.setInventory(null);
        } else {

        }
    }

    public Stack<Plate> getPlates() {
        return plates;
    }

    
}

