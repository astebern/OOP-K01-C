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
    public boolean isPortable() {
        return true;
    }
}

