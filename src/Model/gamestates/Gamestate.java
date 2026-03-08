package Model.gamestates;

public enum Gamestate {
    PLAYING, MENU, GAMEOVER, PAUSEMENU, OPTIONS, ABOUT, LOADING, MODE_SELECT;

    public static Gamestate state = MENU;
}
