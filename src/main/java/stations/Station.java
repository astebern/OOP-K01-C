package stations;

public class Station {
    protected float progressPercent = 0.0f; // 0.0 to 100.0
    protected boolean isInProgress = false;
    protected long startTime = 0;
    protected long pausedTime = 0; // Total time spent paused
    protected long lastPauseTime = 0;

    public float getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(float progressPercent) {
        this.progressPercent = Math.max(0.0f, Math.min(100.0f, progressPercent));
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.isInProgress = inProgress;
    }

    public void resetProgress() {
        this.progressPercent = 0.0f;
        this.isInProgress = false;
        this.startTime = 0;
        this.pausedTime = 0;
        this.lastPauseTime = 0;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void pause() {
        if (isInProgress && lastPauseTime == 0) {
            lastPauseTime = System.currentTimeMillis();
        }
    }

    public void resume() {
        if (lastPauseTime > 0) {
            pausedTime += System.currentTimeMillis() - lastPauseTime;
            lastPauseTime = 0;
        }
    }

    public long getElapsedTime() {
        if (!isInProgress) return 0;
        long currentTime = System.currentTimeMillis();
        if (lastPauseTime > 0) {
            // Currently paused
            return lastPauseTime - startTime - pausedTime;
        }
        return currentTime - startTime - pausedTime;
    }
}

