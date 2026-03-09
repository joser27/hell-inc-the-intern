package Model.gamestates;

public enum Gamestate {
    PLAYING, MENU, GAMEOVER, PAUSEMENU, OPTIONS, ABOUT, LOADING, MODE_SELECT, DAY_SUMMARY;

    public static Gamestate state = MENU;
}
