package stations;

import items.equipment.Plate;

public class WashingStation extends Station {
    private static final int WASH_DURATION = 3000; // 3 seconds in milliseconds
    private Plate plateBeingWashed;
    private long washStartTime;
    private boolean isWashing;
    private Runnable onWashComplete;

    public WashingStation() {
        this.plateBeingWashed = null;
        this.isWashing = false;
        this.washStartTime = 0;
    }

    /**
     * Starts washing a dirty plate
     */
    public boolean startWashing(Plate plate, Runnable onComplete) {
        if (plate == null || !plate.getIsDirty()) {
            System.out.println("WashingStation: Plate is not dirty or null");
            return false;
        }

        if (isWashing) {
            System.out.println("WashingStation: Already washing a plate");
            return false;
        }

        this.plateBeingWashed = plate;
        this.isWashing = true;
        this.washStartTime = System.currentTimeMillis();
        this.onWashComplete = onComplete;
        this.isInProgress = true;
        this.showProgressBar = true; // Show progress bar
        this.progressPercent = 0; // Start at 0%

        System.out.println("WashingStation: Started washing plate");

        // Start a thread to update progress and complete washing
        new Thread(() -> {
            try {
                while (isWashing && System.currentTimeMillis() - washStartTime < WASH_DURATION) {
                    // Update progress
                    long elapsed = System.currentTimeMillis() - washStartTime;
                    progressPercent = Math.min(100f, (elapsed * 100f) / WASH_DURATION);
                    Thread.sleep(50); // Update every 50ms for smooth progress
                }

                if (isWashing && plateBeingWashed == plate) {
                    completeWashing();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return true;
    }

    /**
     * Completes the washing process
     */
    private void completeWashing() {
        if (plateBeingWashed != null) {
            plateBeingWashed.setIsDirty(false);
            System.out.println("WashingStation: Plate is now clean!");

            if (onWashComplete != null) {
                onWashComplete.run();
            }
        }

        this.plateBeingWashed = null;
        this.isWashing = false;
        this.isInProgress = false;
        this.showProgressBar = false;
        this.progressPercent = 0;
        this.washStartTime = 0;
    }

    /**
     * Stops the washing process (e.g., if chef moves away)
     */
    public void stopWashing() {
        System.out.println("WashingStation: Washing interrupted - progress reset!");
        this.isWashing = false;
        this.isInProgress = false;
        this.showProgressBar = false;
        this.progressPercent = 0;
        this.washStartTime = 0;
        this.plateBeingWashed = null;
    }

    /**
     * Pauses the washing (if chef moves away temporarily)
     */
    @Override
    public void pause() {
        // For washing, we'll just stop it completely
        if (isWashing) {
            stopWashing();
        }
    }

    /**
     * Gets the washing progress (0-100%)
     */
    public int getWashingProgress() {
        if (!isWashing || washStartTime == 0) {
            return 0;
        }

        long elapsed = System.currentTimeMillis() - washStartTime;
        return Math.min(100, (int)((elapsed * 100) / WASH_DURATION));
    }

    /**
     * Checks if the station is currently washing
     */
    @Override
    public boolean isInProgress() {
        return isWashing;
    }

    /**
     * Gets the remaining wash time in seconds
     */
    public int getRemainingTime() {
        if (!isWashing) return 0;

        long elapsed = System.currentTimeMillis() - washStartTime;
        long remaining = WASH_DURATION - elapsed;
        return Math.max(0, (int)(remaining / 1000));
    }
}

