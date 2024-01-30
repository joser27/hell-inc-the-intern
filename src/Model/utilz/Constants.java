package Model.utilz;

public class Constants {

    public static class PlayerConstants {
//        public static final int RUNNING_DOWN_ROW = 0;
//        public static final int RUNNING_DOWN_COL = 2;
//        public static final int IDLE = 1;
//        public static final int ATTACKING = 2;
        public static final String  RUNNING_DOWN = "DOWN";
        public static final String RUNNING_UP = "UP";
        public static final String RUNNING_LEFT = "LEFT";
        public static final String RUNNING_RIGHT = "RIGHT";
        public static int[] GetSpriteAmountColRow(String player_action) {
            // Return number of sprites in animation
            switch(player_action) {
                case RUNNING_DOWN -> {
                    return new int[]{2,0,2};//COL,ROW,ANIMATION LENGTH
                }
                case RUNNING_UP -> {
                    return new int[]{2,4,2};//COL,ROW,ANIMATION LENGTH
                }
                case RUNNING_LEFT -> {
                    return new int[]{2,6,2};//COL,ROW,ANIMATION LENGTH
                }
                case RUNNING_RIGHT -> {
                    return new int[]{2,2,2};//COL,ROW,ANIMATION LENGTH
                }

                default -> {
                    return new int[]{0,0,0};
                }
            }
        }
//        public static int GetSpriteAmountRow(int player_action) {
//            // Return number of sprites in animation
//            switch(player_action) {
//                case RUNNING_DOWN_ROW -> {
//                    return 2;
//                }
//                default -> {
//                    return 1;
//                }
//            }
//        }
//        public static int GetSpriteAmountCol(int player_action) {
//            // Return number of sprites in animation
//            switch(player_action) {
//                case RUNNING_DOWN_ROW -> {
//                    return 4;
//                }
//
//                default -> {
//                    return 1;
//                }
//            }
//        }
    }
}
