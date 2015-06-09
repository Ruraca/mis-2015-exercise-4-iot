package ruraca.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Pim on 08/06/2015.
 */
public class SecondWindow extends Activity {
    private static final String TAG = "GATT";
    TextView textView;
    BluetoothDevice device;
    BluetoothGatt mBluetoothGatt;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    private static final UUID Battery_Service_UUID = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb");
    private Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondwindow);
        Bundle bundle = getIntent().getExtras();
        device = (BluetoothDevice) bundle.get("Device");
        textView = (TextView) findViewById(R.id.textView);
        textView.append("\nName: " + device.getName().toString());
        textView.append("\nAddress: " + device.getAddress().toString());
        textView.append("\nBluetoothClass:" + device.getBluetoothClass().getDeviceClass());
        textView.append("\nBondState: " + device.getBondState());
        textView.append("\nType: " + device.getType());
        textView.append("\nUuids: " + Arrays.toString(device.getUuids()).toString());


    }

    public void cerrar(View view) {            /** método que da la funcionalidad al botón Regresar */
        finish();
    }
    private Runnable runnable = new Runnable() {
        public void run() {
            BluetoothGattCallback btGattCB = new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                            }
                        });

                       //gatt.discoverServices();



                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Disconected", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status)
                {
                    if(status == BluetoothGatt.GATT_SUCCESS)
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Service discovery success", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Service discovery failed", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            };

            mBluetoothGatt = device.connectGatt(getApplicationContext(), false, btGattCB);
            mBluetoothGatt.discoverServices();

            List<BluetoothGattService>btServices=mBluetoothGatt.getServices();
            if(btServices.size()>0){
                textView.setText("");
                for (int i = 0; i < btServices.size(); i++) {
                    textView.append("Service: " + i + btServices.get(i).toString());
                    for (int j = 0; j < btServices.get(i).getCharacteristics().size(); j++) {
                        textView.append("Characteristic: " + j + btServices.get(i).getCharacteristics().get(j).toString());

                    }
                }
            }
        }
    };
    public void GATTconection(View view) {

        mHandler.postDelayed(runnable, 1);


    }


}

