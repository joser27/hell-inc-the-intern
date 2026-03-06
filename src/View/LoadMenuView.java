package View;

import Controller.GameController;
import Model.gamestates.LoadMenu;

import java.awt.*;

public class LoadMenuView {
    public void render(Graphics g, LoadMenu menu) {
        if (menu == null) return;
        g.setFont(menu.getFontStart());
        g.setColor(new Color(51, 0, 111));
        int screenWidth = GameController.GAME_WIDTH;
        FontMetrics fontMetrics = g.getFontMetrics(menu.getFontStart());
        int textStartWidth = fontMetrics.stringWidth("START");
        g.drawString("START", (screenWidth - textStartWidth) / 2, 600);

        g.setFont(menu.getFontMenu());
        g.setColor(Color.WHITE);
        FontMetrics fontMetricsMenu = g.getFontMetrics(menu.getFontMenu());
        int textMenuWidth = fontMetricsMenu.stringWidth("MENU");
        g.drawString("MENU", (screenWidth - textMenuWidth) / 2, 250);
    }
}
