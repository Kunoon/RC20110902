package com.koobest.bluetooth;

import java.util.UUID;

public class BluetoothConstant {
	
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDR = "device_addr";
    public static final String TOAST = "bluetooth_toast";
    
    // SDP�����¼��(Name for the SDP record when creating server socket)
    public static final String NAME = "BluetoothChat";

    // ������������Ҫ�ķ����UUID(Unique UUID for this application)
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    
	// ��ServerOfUSB Handler����������Ϣ������(Message type from ServerOfUSB Handler)
    public static final int MESSAGE_STATE_CHANGE = 91,
    						MESSAGE_READ = 92,
    						MESSAGE_WRITE = 93,
    						MESSAGE_DEVICE_NAME = 94,
    						MESSAGE_TOAST = 95;
    
    // �����豸��ǰ����״̬(BluetoothDevice Currently Connection State)
    public static final int STATE_NONE = 96,   // Do nothing
    						STATE_LISTEN = 97,   // Listening for incoming connections
    						STATE_CONNECTING = 98,   // Initiating an outgoing connection
    						STATE_CONNECTED = 99;   // Connected to a remote device 
}
