package ch.hes_so.glassrallylibs.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import ch.hes_so.glassrallylibs.command.Command;
import ch.hes_so.glassrallylibs.command.CommandEncoder;

public class BluetoothService extends Service {
    private static final String TAG = BluetoothService.class.getSimpleName();

    // Binder given to client
    private BluetoothBinder bluetoothBinder = new BluetoothBinder();

    private IBluetoothService callback;
    private BluetoothThread bluetoothThread;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "service created");

        // Initialize the BluetoothThread to perform bluetooth connections
        bluetoothThread = new BluetoothThread(getApplicationContext(), handler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothThread.stop();
    }

    // ==== Services provided
    public void sendCommand(Command cmd) throws IllegalStateException {
        if (this.bluetoothThread.getState() != BluetoothThread.STATE_CONNECTED) {
            throw new IllegalStateException("Not connected, cannot send command");
        }

        this.bluetoothThread.write(cmd);
    }

    public boolean isConnected() {
        return this.bluetoothThread.getState() == BluetoothThread.STATE_CONNECTED;
    }

    public void connect(BluetoothDevice device) {
        boolean secure = true;
        this.bluetoothThread.connect(device, secure);
    }

    // ==== Communication with THE client
    public class BluetoothBinder extends android.os.Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.bluetoothBinder;
    }

    public interface IBluetoothService {
        void onCommandReceived(Command cmd);

        /**
         * When the Bluetooth state (connected, connecting,...) has changed
         *
         * @param state one of the state in the ch.hes_so.glassrallylibs.bluetooth.Constants class
         */
        void bluetoothStateChanged(int state);

        /**
         * The device you are connected has changed
         *
         * @param device the new device name
         */
        void deviceNameChanged(String device);
    }

    public void register(IBluetoothService callback) {
        this.callback = callback;
    }

    public void unregister(IBluetoothService callback) {
        if (this.callback == callback)
            this.callback = null;
        else
            Log.w(TAG, "no previous callback was registered");
    }

    /**
     * The Handler that gets information back from the BluetoothThread
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE: {
//                    switch (msg.arg1) {
//                        case BluetoothThread.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            break;
//                        case BluetoothThread.STATE_CONNECTING:
//                            setStatus(R.string.title_connecting);
//                            break;
//                        case BluetoothThread.STATE_LISTEN:
//                        case BluetoothThread.STATE_NONE:
//                            setStatus(R.string.title_not_connected);
//                            break;
//                    }

                    // notify client for the new state
                    int state = msg.arg1;
                    if (callback != null) {
                        callback.bluetoothStateChanged(state);
                    }
                    break;
                }
                case Constants.MESSAGE_READ:
                    // construct a string from the valid bytes in the buffer
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, "msg read: " + readMessage);
                    Command cmd = CommandEncoder.fromStream(readMessage);

                    if (callback != null) {
                        callback.onCommandReceived(cmd);
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Log.d(TAG, "Connected to " + connectedDeviceName);
                    if (callback != null) {
                        callback.deviceNameChanged(connectedDeviceName);
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
