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
    public static final String BOMB_EXPLOSION = "Retro Impact Effect Pack 1 A.png";
    public static final String VECTOR_45 = "Vector 45 acp spritesheet - 75 ms per frame.png";
    public static final String STONE_1 = "Stone-1.png";
    public static final String STONE_2 = "Stone-2.png";
    public static final String EARTH_TILESET = "Earth-tileset.png";
    public static final String GRASS_TILESET = "Set 1.2.png";
    public static final String TREE_4 = "Tree-3-4.png";
    public static final String ARROW_PROJECTILE = "arrowProjectile.png";
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