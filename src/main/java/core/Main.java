package core;

import utils.BetterComments;

import javax.swing.*;

public class Main {

    @BetterComments(description = "Get GameMaster singleton instance and start game", type ="method")
    public static void main(String[] args){
        GameMaster master = GameMaster.getInstance();
        master.main();

    }
}
