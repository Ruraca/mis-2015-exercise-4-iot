package ruraca.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;
public class MainActivity extends Activity {
    TextView out;
    ListView list;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter;
    private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
    ArrayList<String> s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        s=new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        out = (TextView) findViewById(R.id.out);
        //out.setMovementMethod(new ScrollingMovementMethod());
        list=(ListView)findViewById(R.id.listView);
        //Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(ActionFoundReceiver, filter); // Don't forget to unregister during onDestroy


        // Getting the Bluetooth adapter

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        CheckBTState();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
               lanzar(posicion);
            }
        });
    }

    public void lanzar(int posicion) {
        Intent i = new Intent(this, SecondWindow.class );
        i.putExtra("Device",btDeviceList.get(posicion));
        startActivity(i);


    }
    protected void UpdateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, s);


        // Assign adapter to ListView
        list.setAdapter(adapter);
    }
    /* This routine is called when an activity completes.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBTState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btAdapter != null) {
            btAdapter.cancelDiscovery();
        }
        unregisterReceiver(ActionFoundReceiver);
    }
    private void CheckBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // If it isn't request to turn it on
        // List paired devices
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            return;
        } else {
            if (btAdapter.isEnabled()) {

                // Starting the device discovery
                btAdapter.startDiscovery();
            } else {
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specifBluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();y a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //out.append("\n  Device: " + device.getName() + ", " + device);
                btDeviceList.add(device);
            } else {
                if(BluetoothDevice.ACTION_UUID.equals(action)) {

                } else {
                    if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                       // out.append("\nDiscovery Started...");
                        Toast.makeText(getApplicationContext(),"Discovery Started, wait please...",Toast.LENGTH_LONG).show();
                    } else {
                        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                            Toast.makeText(getApplicationContext(),"Discovery Finished",Toast.LENGTH_LONG).show();
                            Iterator<BluetoothDevice> itr = btDeviceList.iterator();
                            while (itr.hasNext()) {
                                // Get Services for paired devices
                                BluetoothDevice device = itr.next();
                                s.add(device.getName());


                                if(!device.fetchUuidsWithSdp()) {
                                   // out.append("\nSDP Failed for " + device.getName());
                                }

                            }
                            /*for(int i=0;i<s.size();i++){
                                for(int j=i+1;j<s.size();j++){
                                    if(s.get(i).compareToIgnoreCase(s.get(j))==0){
                                        s.remove(j);

                                    }
                                }
                            }*/
                        }
                    }
                }
            }
        }
    };


}
