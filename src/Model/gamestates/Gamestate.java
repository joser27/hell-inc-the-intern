package Model.gamestates;

public enum Gamestate {
    PLAYING, MENU, GAMEOVER, PAUSEMENU, OPTIONS, ABOUT, LOADING;

    public static Gamestate state = MENU;
}
