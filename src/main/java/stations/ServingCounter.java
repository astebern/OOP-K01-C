package stations;


import java.util.List;

import core.Order;
import entities.Chef;
import items.Item;
import items.food.Dish;
import utils.Position;


public class ServingCounter extends Station {

    private List<Order> activeOrders;


    public ServingCounter(Position position, List<Order> activeOrders) {
        super(position);
        this.activeOrders = activeOrders;
    }

    @Override
    public void interact(Chef chef) {
        Item carried = chef.getInventory();

        if(!(carried instanceof Dish)) {
            return;
        }

        Dish dish = (Dish) carried;

        Order matchedOrder = null;
        for (Order order : activeOrders) {
            if (order.compareDishAndRecipe(dish, order.getRecipe())) {
                matchedOrder = order;
                break;
               }
            
        }

                   

        chef.setInventory(null);
    }

    public List<Order> getActiveOrders(){
        return activeOrders;
    }

    public void setActiveOrders(List<Order> activeOrders){
        this.activeOrders = activeOrders;
    }

    
}

