package stations;

public abstract class Station {
    protected float progressPercent = 0.0f; // 0.0 to 100.0
    protected boolean isInProgress = false;
    protected boolean showProgressBar = false; // Controls visibility of progress bar
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

    public boolean shouldShowProgressBar() {
        return showProgressBar;
    }

    public void setShowProgressBar(boolean show) {
        this.showProgressBar = show;
    }

    public void resetProgress() {
        this.progressPercent = 0.0f;
        this.isInProgress = false;
        this.showProgressBar = false;
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
            showProgressBar = false; // Hide progress bar when paused
        }
    }

    public void resume() {
        if (lastPauseTime > 0) {
            pausedTime += System.currentTimeMillis() - lastPauseTime;
            lastPauseTime = 0;
            showProgressBar = true; // Show progress bar when resumed
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

