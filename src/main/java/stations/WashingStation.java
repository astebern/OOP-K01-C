package stations;

import items.equipment.Plate;
import map.GameMap;
import utils.BetterComments;

@BetterComments(description = "Station for washing dirty plates. Drop dirty plate, wash it, then it's sent to CleanPlateStation.", type = "class")
public class WashingStation extends Station {
    private static final int WASH_DURATION = 3000; // 3 seconds in milliseconds
    private Plate plateOnStation; // Plate placed on the station (can be dirty or clean)
    private boolean isWashing;
    private long washStartTime;
    private GameMap gameMap; // Reference to find CleanPlateStation

    public WashingStation() {
        this.plateOnStation = null;
        this.isWashing = false;
        this.washStartTime = 0;
    }

    @BetterComments(description = "Sets the GameMap reference to find CleanPlateStation", type = "method")
    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    @BetterComments(description = "Checks if there's a plate on the station", type = "method")
    public boolean hasPlate() {
        return plateOnStation != null;
    }

    @BetterComments(description = "Gets the plate on the station", type = "method")
    public Plate getPlate() {
        return plateOnStation;
    }

    @BetterComments(description = "Places a plate on the station (for washing)", type = "method")
    public boolean placePlate(Plate plate) {
        if (plateOnStation != null) {
            System.out.println("WashingStation: Already has a plate");
            return false;
        }

        if (plate == null) {
            System.out.println("WashingStation: Cannot place null plate");
            return false;
        }

        this.plateOnStation = plate;
        System.out.println("WashingStation: Plate placed on station");
        return true;
    }

    @BetterComments(description = "Removes the plate from the station", type = "method")
    public Plate removePlate() {
        Plate plate = this.plateOnStation;
        this.plateOnStation = null;

        // Stop washing if in progress
        if (isWashing) {
            stopWashing();
        }

        return plate;
    }

    @BetterComments(description = "Starts washing the plate on the station", type = "method")
    public boolean startWashing() {
        if (plateOnStation == null) {
            System.out.println("WashingStation: No plate to wash");
            return false;
        }

        if (!plateOnStation.getIsDirty()) {
            System.out.println("WashingStation: Plate is already clean");
            return false;
        }

        if (isWashing) {
            System.out.println("WashingStation: Already washing");
            return false;
        }

        this.isWashing = true;
        this.washStartTime = System.currentTimeMillis();
        this.isInProgress = true;
        this.showProgressBar = true;
        this.progressPercent = 0;

        System.out.println("WashingStation: Started washing plate");

        // Start a thread to update progress and complete washing
        new Thread(() -> {
            try {
                Plate plateBeingWashed = plateOnStation;

                while (isWashing && plateOnStation == plateBeingWashed &&
                       System.currentTimeMillis() - washStartTime < WASH_DURATION) {
                    // Update progress
                    long elapsed = System.currentTimeMillis() - washStartTime;
                    progressPercent = Math.min(100f, (elapsed * 100f) / WASH_DURATION);
                    Thread.sleep(50); // Update every 50ms for smooth progress
                }

                if (isWashing && plateOnStation == plateBeingWashed) {
                    completeWashing();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return true;
    }

    @BetterComments(description = "Completes the washing process and sends plate to CleanPlateStation", type = "method")
    private void completeWashing() {
        if (plateOnStation != null) {
            plateOnStation.clean(); // Clear contents and set clean
            System.out.println("WashingStation: Plate is now clean!");

            // Try to send to CleanPlateStation
            if (gameMap != null) {
                CleanPlateStation nearestCleanStation = findNearestCleanPlateStation();
                if (nearestCleanStation != null && !nearestCleanStation.isFull()) {
                    if (nearestCleanStation.addCleanPlate(plateOnStation)) {
                        System.out.println("WashingStation: Clean plate sent to CleanPlateStation");
                        plateOnStation = null; // Remove from washing station
                    } else {
                        System.out.println("WashingStation: Failed to send to CleanPlateStation, plate stays here");
                    }
                } else {
                    System.out.println("WashingStation: No available CleanPlateStation, plate stays here");
                }
            }
        }

        this.isWashing = false;
        this.isInProgress = false;
        this.showProgressBar = false;
        this.progressPercent = 0;
        this.washStartTime = 0;
    }

    @BetterComments(description = "Finds the nearest CleanPlateStation", type = "method")
    private CleanPlateStation findNearestCleanPlateStation() {
        if (gameMap == null) return null;

        // Search the map for CleanPlateStation
        for (int y = 0; y < gameMap.getMapHeight(); y++) {
            for (int x = 0; x < gameMap.getMapWidth(); x++) {
                Station station = gameMap.getStationAt(x, y);
                if (station instanceof CleanPlateStation) {
                    CleanPlateStation cleanStation = (CleanPlateStation) station;
                    if (!cleanStation.isFull()) {
                        return cleanStation;
                    }
                }
            }
        }
        return null;
    }

    @BetterComments(description = "Stops the washing process", type = "method")
    public void stopWashing() {
        System.out.println("WashingStation: Washing interrupted - progress reset!");
        this.isWashing = false;
        this.isInProgress = false;
        this.showProgressBar = false;
        this.progressPercent = 0;
        this.washStartTime = 0;
    }

    @BetterComments(description = "Pauses the washing if chef moves away", type = "method")
    @Override
    public void pause() {
        if (isWashing) {
            stopWashing();
        }
    }

    @BetterComments(description = "Gets the washing progress (0-100%)", type = "method")
    public int getWashingProgress() {
        if (!isWashing || washStartTime == 0) {
            return 0;
        }

        long elapsed = System.currentTimeMillis() - washStartTime;
        return Math.min(100, (int)((elapsed * 100) / WASH_DURATION));
    }

    @BetterComments(description = "Checks if the station is currently washing", type = "method")
    @Override
    public boolean isInProgress() {
        return isWashing;
    }

    @BetterComments(description = "Gets the remaining wash time in seconds", type = "method")
    public int getRemainingTime() {
        if (!isWashing) return 0;

        long elapsed = System.currentTimeMillis() - washStartTime;
        long remaining = WASH_DURATION - elapsed;
        return Math.max(0, (int)(remaining / 1000));
    }

    @Override
    public String toString() {
        return "WashingStation - Plate: " + (plateOnStation != null ? "Yes" : "No") +
               ", Washing: " + isWashing;
    }
}

