package Model.utilz;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class LoadSave {

    public static final String START_BUTTON = "startButton.png";
    public static final String PLAYER1_ATLAS = "Character-Base.png";
    public static final String PLAYER2_ATLAS = "Orc-Peon-Cyan.png";
    public static final String PISTOL_STATIC_IMG = "GUN_01_[square_frame]_01_V1.00.png";

    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage image = null;
        String imagePathWalk = "/" + fileName;
        InputStream is = LoadSave.class.getResourceAsStream(imagePathWalk);
        try {
            image = ImageIO.read(is);

        } catch (IOException e) {
            throw new RuntimeException("Error reading image", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }




}