import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;


public class ImageLoader {
    public static @Nullable BufferedImage loadImage(String path) {
        try {return ImageIO.read(Objects.requireNonNull(ImageLoader.class.getResource(path)));}
        catch (IOException e) {e.printStackTrace();}

        return null;
    }

    public static BufferedImage resize(int newHeight, int newWidth, @NotNull BufferedImage img) {
        double heightFactor = (double) newHeight /img.getHeight();
        double widthFactor = (double) newWidth /img.getWidth();

        BufferedImage scaledImg = new BufferedImage((int)(heightFactor*img.getWidth()), newHeight, BufferedImage.TYPE_4BYTE_ABGR);
        AffineTransform at = new AffineTransform();
        at.scale(heightFactor, widthFactor);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

        return scaleOp.filter(img, scaledImg);
    }
}
