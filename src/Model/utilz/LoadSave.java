package Model.utilz;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LoadSave {

    // Fonts
    public static final String FONT_DEFAULT = "res\\Daydream.ttf";
    public static final String FONT_MINECRAFT = "res\\Minecraft.ttf";

    public static final String START_BUTTON = "startButton.png";
//    public static final String PLAYER1_ATLAS = "Character-Base.png";
    public static final String PLAYER1_ATLAS = "characterSprites/Puny-Characters/Archer-Green.png";
    public static final String PLAYER2_ATLAS = "Orc-Peon-Cyan.png";
    /** Hell Inc. intern sprite sheet: 384x960, 8 cols x 20 rows, 48x48 per frame. Idle/walk only. */
    public static final String INTERN_ATLAS = "characterSprites/boyIntern.png";
    /** Player run sheet: 632x237, 8 cols x 3 rows. Row 0=right, 1=down, 2=up; left = right flipped. */
    public static final String RUN_ATLAS = "characterSprites/run.png";
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

    /** NPC encounter frame (first-person door view) — Hell Inc. design. */
    public static final String WIDOW_FRAME = "widowFrame.png";
    /** GTA-style encounter: door frame (16:9), shown smaller on black background. */
    public static final String GENERIC_DOOR_FRAME = "genericDoorFrame.png";
    /** GTA-style encounter: widow portrait (9:16), slightly larger than door. */
    public static final String WIDOW_PORTRAIT = "widowPortrait.png";

    /** Main menu background — GTA-style frame/scene. */
    public static final String MENU_BACKGROUND = "menuBackground.png";
    /** Main menu: intern/contractor portrait (9:16), GTA-style. */
    public static final String DEMON_PORTRAIT = "demonPortrait3.png";

    /** Door knock one-shot (res/audio/knock.ogg). */
    public static final String KNOCK_SOUND = "audio/knock.ogg";

    // Player 1 Abilities
    public static final String Death_Surge = "Death_Surge.png";
    public static final String Decimating_Smash = "Decimating_Smash1.png";
    public static final String Glory_in_Death = "Glory_in_Death.png";
    public static final String Roar_of_the_Slayer = "Roar_of_the_Slayer.png";
    public static final String Soul_Furnace = "Soul_Furnace.png";
    public static final String Unstoppable_Onslaught = "Unstoppable_Onslaught.png";

    // Player 2 Abilities
    public static final String Enchanted_Crystal_Arrow = "Enchanted_Crystal_Arrow.png";
    public static final String Frost_Shot = "Frost_Shot.png";
    public static final String Volley = "Volley.png";
    public static final String Ranger_Focus = "Ranger_Focus.png";
    public static final String Hawkshot = "Hawkshot.png";
    //PIXEL P1
    public static final String Death_Surge_PX = "Death_Surge_PX.png";
    public static final String Decimating_Smash_PX = "Decimating_Smash_PX.png";
    public static final String Glory_in_Death_PX = "Glory_in_Death_PX.png";
    public static final String Roar_of_the_Slayer_PX = "Roar_of_the_Slayer_PX.png";
    public static final String Soul_Furnace_PX = "Soul_Furnace_PX.png";
    public static final String Unstoppable_Onslaught_PX = "Unstoppable_Onslaught_PX.png";



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

    public static Image GetImage(String fileName) {
        Image img = null;
        String imagePathWalk = "/" + fileName;
        InputStream is = LoadSave.class.getResourceAsStream(imagePathWalk);
        try {
            img = ImageIO.read(is);

        } catch (IOException e) {
            throw new RuntimeException("Error reading image", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
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