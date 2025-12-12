package stations;

import items.Item;
import items.Preparable;
import items.food.Ingredient;

public class CuttingStation extends Station {
    private static final long CUTTING_DURATION = 3000; // 3 seconds in milliseconds
    private Thread cuttingThread;
    private volatile boolean shouldStop = false;

    /**
     * Starts the cutting process for an item on this station.
     * Returns true if cutting started successfully.
     */
    public boolean startCutting(Item item, Runnable onComplete) {
        if (item == null) {
            System.out.println("CuttingStation: No item to cut");
            return false;
        }

        if (!(item instanceof Ingredient)) {
            System.out.println("CuttingStation: Item is not an ingredient");
            return false;
        }

        Ingredient ingredient = (Ingredient) item;

        if (!(item instanceof Preparable)) {
            System.out.println("CuttingStation: Item cannot be prepared");
            return false;
        }

        Preparable preparable = (Preparable) item;

        if (!preparable.canBeChopped()) {
            System.out.println("CuttingStation: " + ingredient.getName() +
                             " cannot be chopped (current state: " + ingredient.getState() + ")");
            return false;
        }

        // If already in progress, just resume
        if (isInProgress) {
            resume();
            System.out.println("CuttingStation: Resuming cutting of " + ingredient.getName());
            return true;
        }

        // Start new cutting process
        System.out.println("CuttingStation: Starting to cut " + ingredient.getName());
        isInProgress = true;
        startTime = System.currentTimeMillis();
        pausedTime = 0;
        lastPauseTime = 0;
        progressPercent = 0.0f;
        shouldStop = false;

        cuttingThread = new Thread(() -> {
            try {
                while (!shouldStop && isInProgress) {
                    long elapsed = getElapsedTime();
                    progressPercent = Math.min(100.0f, (elapsed / (float) CUTTING_DURATION) * 100.0f);

                    if (elapsed >= CUTTING_DURATION) {
                        // Cutting complete
                        preparable.chop();
                        System.out.println("CuttingStation: Successfully chopped " +
                                         ingredient.getName() + " from RAW to CHOPPED");
                        resetProgress();
                        if (onComplete != null) {
                            onComplete.run();
                        }
                        break;
                    }

                    Thread.sleep(50); // Update every 50ms
                }
            } catch (InterruptedException e) {
                System.out.println("CuttingStation: Cutting interrupted");
                Thread.currentThread().interrupt();
            }
        });

        cuttingThread.start();
        return true;
    }

    /**
     * Pauses the cutting process.
     */
    public void pauseCutting() {
        if (isInProgress) {
            pause();
            System.out.println("CuttingStation: Paused cutting (progress: " +
                             String.format("%.1f", progressPercent) + "%)");
        }
    }

    /**
     * Resumes the cutting process.
     */
    public void resumeCutting() {
        if (isInProgress && lastPauseTime > 0) {
            resume();
            System.out.println("CuttingStation: Resumed cutting (progress: " +
                             String.format("%.1f", progressPercent) + "%)");
        }
    }

    /**
     * Stops and resets the cutting process.
     */
    public void stopCutting() {
        shouldStop = true;
        if (cuttingThread != null && cuttingThread.isAlive()) {
            cuttingThread.interrupt();
        }
        resetProgress();
        System.out.println("CuttingStation: Cutting stopped and reset");
    }

    /**
     * Checks if there's an ingredient on this station that can be chopped.
     */
    public boolean hasChoppableIngredient(Item item) {
        if (item instanceof Ingredient && item instanceof Preparable) {
            return ((Preparable) item).canBeChopped();
        }
        return false;
    }
}

