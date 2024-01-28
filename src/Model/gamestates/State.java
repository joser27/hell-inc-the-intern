package Model.gamestates;

import Model.Game;

public class State {
    private Game game;
    public State(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
