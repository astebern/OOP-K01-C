package core;

import utils.BetterComments;

import javax.swing.*;

public class Main {

    @BetterComments(description = "Create Gamemaster", type ="method")
    public static void main(String[] args){
        GameMaster master = new GameMaster();
        master.main();

    }
}
