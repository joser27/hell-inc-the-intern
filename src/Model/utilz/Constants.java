package Model.utilz;

public class Constants {

    public static class PlayerConstants {
        public static final String  IDLE = "IDLE";
        public static final String  RUNNING_DOWN = "DOWN";
        public static final String RUNNING_UP = "UP";
        public static final String RUNNING_LEFT = "LEFT";
        public static final String RUNNING_RIGHT = "RIGHT";
        public static final String OGRE_ATTACK_DOWN = "ATTACK_DOWN";
        public static final String OGRE_ATTACK_UP = "ATTACK_UP";
        public static final String OGRE_ATTACK_LEFT = "ATTACK_LEFT";
        public static final String OGRE_ATTACK_RIGHT = "ATTACK_RIGHT";
        //Ogre begin smash
        public static final String OGRE_SMASH_DOWN = "OGRE-ATTACK_DOWN";
        public static final String OGRE_SMASH_UP = "OGRE-ATTACK_UP";
        public static final String OGRE_SMASH_LEFT = "OGRE-ATTACK_LEFT";
        public static final String OGRE_SMASH_RIGHT = "OGRE-ATTACK_RIGHT";
        //Ogre finish smash
        public static final String OGRE_END_SMASH_DOWN = "OGRE-END_ATTACK_DOWN";
        public static final String OGRE_END_SMASH_UP = "OGRE-END_ATTACK_UP";
        public static final String OGRE_END_SMASH_LEFT = "OGRE-END_ATTACK_LEFT";
        public static final String OGRE_END_SMASH_RIGHT = "OGRE-END_ATTACK_RIGHT";
        public static final String HUMAN_ATTACK_DOWN = "HUMAN-ATTACK_DOWN";
        public static final String HUMAN_ATTACK_UP = "HUMAN_ATTACK_UP";
        public static final String HUMAN_ATTACK_LEFT = "HUMAN_ATTACK_LEFT";
        public static final String HUMAN_ATTACK_RIGHT = "HUMAN_ATTACK_RIGHT";
        public static int[] GetSpriteAmountColRow(String player_action) {
            // Return number of sprites in animation
            switch(player_action) {
                case RUNNING_DOWN -> {
                    return new int[]{0,0,8};// run.png: 8 frames per direction
                }
                case RUNNING_UP -> {
                    return new int[]{0,0,8};
                }
                case RUNNING_LEFT -> {
                    return new int[]{0,0,8};
                }
                case RUNNING_RIGHT -> {
                    return new int[]{0,0,8};
                }
                case IDLE -> {
                    return new int[]{0,0,4};// idle.png: 4 frames per direction (R0C0-R0C3, etc.)
                }
                case OGRE_ATTACK_DOWN -> {
                    return new int[]{5,0,4};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_ATTACK_UP -> {
                    return new int[]{5,4,4};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_ATTACK_LEFT -> {
                    return new int[]{5,6,4};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_ATTACK_RIGHT -> {
                    return new int[]{5,2,4};//COL,ROW,ANIMATION LENGTH
                }
                case HUMAN_ATTACK_RIGHT -> {
                    return new int[]{8,2,4};//COL,ROW,ANIMATION LENGTH
                }
                case HUMAN_ATTACK_LEFT -> {
                    return new int[]{8,6,4};//COL,ROW,ANIMATION LENGTH
                }
                case HUMAN_ATTACK_UP -> {
                    return new int[]{8,4,4};//COL,ROW,ANIMATION LENGTH
                }
                case HUMAN_ATTACK_DOWN -> {
                    return new int[]{8,0,0};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_SMASH_DOWN -> {
                    return new int[]{13,0,1};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_SMASH_UP -> {
                    return new int[]{13,4,1};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_SMASH_LEFT -> {
                    return new int[]{13,6,1};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_SMASH_RIGHT -> {
                    return new int[]{13,2,1};//COL,ROW,ANIMATION LENGTH
                }
                //Finish smash
                case OGRE_END_SMASH_DOWN -> {
                    return new int[]{15,0,1};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_END_SMASH_UP -> {
                    return new int[]{15,4,1};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_END_SMASH_LEFT -> {
                    return new int[]{15,6,1};//COL,ROW,ANIMATION LENGTH
                }
                case OGRE_END_SMASH_RIGHT -> {
                    return new int[]{15,2,1};//COL,ROW,ANIMATION LENGTH
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
