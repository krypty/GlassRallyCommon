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
}
