package ch.hes_so.glassrallylibs.command;

import android.location.Location;

public class CommandFactory {
    private static final String TAG = CommandFactory.class.getSimpleName();

    // TODO: 29.03.16 keep or remove this command
    public static Command createConnectedCommand() {
        return new Command(Command_E.CONNECTED);
    }

    public static Command createDebugCommand(String message) {
        return new Command(Command_E.DEBUG, message);
    }

    public static Command createVectorCommand(Location currentLocation, Location targetLocation) {
        String parameters = currentLocation.getLatitude() + Command.PARAMETER_DELIMITER +
                currentLocation.getLongitude() + Command.PARAMETER_DELIMITER +
                targetLocation.getLatitude() + Command.PARAMETER_DELIMITER +
                targetLocation.getLongitude();

        return new Command(Command_E.NEW_VECTOR, parameters);
    }

    public static Command createRewardCommand(String reward) {
        return new Command(Command_E.REWARD, reward);
    }

    public static Command createVictoryCommand() {
        return new Command(Command_E.VICTORY);
    }

    public static Command createDistanceCommand(float distance) {
        return new Command(Command_E.NEW_DISTANCE, String.valueOf(distance));
    }
}
