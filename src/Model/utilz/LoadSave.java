package Model.utilz;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LoadSave {

    public static final String FONT_DEFAULT = "res\\Daydream.ttf";
    public static final String FONT_MINECRAFT = "res\\Minecraft.ttf";
    public static final String START_BUTTON = "startButton.png";
    public static final String PLAYER1_ATLAS = "Character-Base.png";
    public static final String PLAYER2_ATLAS = "Orc-Peon-Cyan.png";
    public static final String PISTOL_STATIC_IMG = "GUN_01_[square_frame]_01_V1.00.png";
    public static final String BOMB_EXPLOSION = "Retro Impact Effect Pack 1 A.png";
    public static final String VECTOR_45 = "Vector 45 acp spritesheet - 75 ms per frame.png";
    public static final String STONE_1 = "Stone-1.png";
    public static final String STONE_2 = "Stone-2.png";
    public static final String MEDKIT_TILE = "452_Zombie Apocalypse Tileset Reference.png";
    public static final String EARTH_TILESET = "Earth-tileset.png";
    public static final String GRASS_TILESET = "Set 1.2.png";
    public static final String TREE_4 = "Tree-3-4.png";
    public static final String ARROW_PROJECTILE = "arrowProjectile.png";
    public static final String UI_SQUARES = "gui_free.png";
    public static final String UI_ICONS = "UiIconsPack_Transparent_Icons.png";
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

    public static Font GetFont(String fileName, int size) {
        Font font = null;
        try {
            File fontFile = new File(fileName);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            // Use the font in your application
            font = new Font(customFont.getFontName(), Font.PLAIN, size);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return font;
    }




}