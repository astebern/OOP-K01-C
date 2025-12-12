package stations;


import java.util.LinkedList;
import java.util.Queue;
import entities.Chef;
import items.Item;
import items.equipment.Plate;
import utils.Position;


public class WashingStation extends Station {

    private Queue<Plate> dirtyPlates = new LinkedList<>();

    private Queue<Plate> cleanPlates = new LinkedList<>();

    private int progress;

    public WashingStation(Position position) {
        super(position);
        this.progress = 0;
    }

    

    @Override
    public void interact(Chef chef) {
        Item carried = chef.getInventory();

        if (carried != null) {
            if (carried instanceof Plate) {
                Plate plate = (Plate) carried;
                if (plate.isDirty()) {
                    dirtyPlates.add(plate);
                    chef.setInventory(null);

                    washNextPlate();
                } else {
                    cleanPlates.add(plate);
                    chef.setInventory(null);
                }
            }

            return;
        }

        if (!cleanPlates.isEmpty()) {
            Plate cleaned = cleanPlates.poll();
            chef.setInventory(cleaned);
        }
    }

    private void washNextPlate() {
        if(dirtyPlates.isEmpty()) return;
        Plate plate = dirtyPlates.poll();
        plate.clearContents();
        cleanPlates.add(plate);
        progress = 100;
    }

    
    public int getProgress() {
        return progress;
    }
    public void setProgress(int newProgress) {
        this.progress = newProgress;
    }
    
    public Queue<Plate> getDirtyPlates() {
        return dirtyPlates;
    }
    public Queue<Plate> getCleanPlates() {
        return cleanPlates;
    }

    

    
}

