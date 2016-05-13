package ch.hes_so.glassrallylibs.command;

public enum Command_E {
    //TODO: add or remove commands
    DEBUG, START_GAME, ASK_NEW_POS, CONNECTED, NEW_VECTOR, REWARD, VICTORY, NEW_DISTANCE;

    public static boolean contains(String s) {
        for (Command_E cmd : values()) {
            if (cmd.name().equals(s)) {
                return true;
            }
        }
        return false;
    }
}
