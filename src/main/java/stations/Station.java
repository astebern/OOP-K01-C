package stations;

import entities.Chef;
import utils.Position;

public abstract class Station {
    protected Position position;

    public Station(Position position) {
        this.position = position;
    }

    public Position getPosition(){
        return position;
    }

    public void setPosition(Position position){
        this.position = position;
    }

    public abstract void interact(Chef chef);

    
}

