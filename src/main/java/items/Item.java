package items;

import java.awt.image.BufferedImage;

public abstract class Item {
    protected BufferedImage image;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Determines if this item can be picked up and moved by the chef.
     * By default, all items are portable unless explicitly overridden.
     */
    public boolean isPortable() {
        return true;
    }
}

