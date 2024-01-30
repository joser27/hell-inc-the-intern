package Model.utilz;

public class Constants {

    public static class PlayerConstants {
//        public static final int RUNNING_DOWN_ROW = 0;
//        public static final int RUNNING_DOWN_COL = 2;
//        public static final int IDLE = 1;
//        public static final int ATTACKING = 2;
    public static final int RUNNING_DOWN = 0;
        public static int[] GetSpriteAmountColRow(int player_action) {
            // Return number of sprites in animation
            switch(player_action) {
                case RUNNING_DOWN -> {
                    return new int[]{0,2,2};//COL,ROW,ANIMATION LENGTH
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
