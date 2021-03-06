package ch.hes_so.glassrallylibs.command;

import android.util.Log;

import java.security.InvalidParameterException;

public class CommandEncoder {
    private static final String TAG = CommandEncoder.class.getSimpleName();

    public static Command fromStream(String data) {
        String[] cmd = data.split(Command.COMMAND_DELIMITER);

        if (Command_E.contains(cmd[0])) {
            Command_E cmdName = Command_E.valueOf(cmd[0]);
            String cmdParam = cmd[1];
            return new Command(cmdName, cmdParam);
        } else {
            throw new InvalidParameterException("data isn't a command");
        }

    }

    public static byte[] toStream(Command cmd) {
        StringBuilder builder = new StringBuilder();

        builder.append(cmd.getName().toString());
        builder.append(Command.COMMAND_DELIMITER);
        builder.append(cmd.getParameter());

        Log.d(TAG, "cmd sent: " + builder.toString());
        return builder.toString().getBytes();
    }
}
