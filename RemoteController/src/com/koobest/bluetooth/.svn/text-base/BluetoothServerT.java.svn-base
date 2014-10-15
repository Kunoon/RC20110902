package com.koobest.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.koobest.R;
import com.koobest.RemoteController;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BluetoothServerT {
	// 成员变量(Member fields)
	private final BluetoothAdapter bluetoothAdapter;
	private final Handler myHandler;
	private AcceptThread acceptThread;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	private int deviceState;

	/**
	 * 创建构造函数(Create the Constructor function)
	 * 
	 * @param context
	 * @param handler
	 */
	public BluetoothServerT(Context context, Handler handler) {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		deviceState = BluetoothConstant.STATE_NONE;
		myHandler = handler;
	}

	/**
	 * 设置蓝牙当前状态(Set the current state of the BluetoothServer)
	 * 
	 * @param state
	 */
	private synchronized void SetDeviceState(int state) {
		System.out.println("-------SetDeviceState() -->" + state + "-------");
		deviceState = state;

		// 将新的设备状态传递给Handler，用于更新主程序设备状态(Give the new state to the Handler so
		// the UI Activity can update)
		myHandler.obtainMessage(BluetoothConstant.MESSAGE_STATE_CHANGE, state,
				-1).sendToTarget();
	}

	/**
	 * 返回连接状态(Return the current connection state)
	 * 
	 * @return
	 */
	public synchronized int GetDeviceState() {
		return deviceState;
	}

	/**
	 * 开始Bluetooth Remote Control的服务。(Start BluetoothServer)
	 * 首先使设备作为服务端，开启AcceptThread。它将被BTRemoteControl的onResume()调用。
	 */
	public synchronized void StartServer() {
		System.out.println("-------开启蓝牙服务-------");

		// 取消正在运行ConnectThread(Cancel any thread attempting to make a
		// connection)
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
		// 取消正在运行ConnectedThread(Cancel any thread currently running a
		// connection)
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
		// 开始线程监听BluetoothServerSocket(Start the thread to listen on a
		// BluetoothServerSocket)
		if (acceptThread == null) {
			acceptThread = new AcceptThread();
			acceptThread.start();
		}
		// 将程序状态设置为STATE_LISTEN(Set Device State)
		SetDeviceState(BluetoothConstant.STATE_LISTEN);
	}

	/**
	 * 连接远程设备，开始ConnectToRemoteDevice(Connect To remote devices)
	 * 
	 * @param device
	 */
	public synchronized void ConnectToRemoteDevice(BluetoothDevice device) {
		System.out.println("-------正在连接" + device + "-------");

		// 如果设备正处于连接状态，则取消正在运行的ConnectThread
		if (deviceState == BluetoothConstant.STATE_CONNECTING) {
			if (connectThread != null) {
				connectThread.cancel();
				connectThread = null;
			}
		}
		// 取消已建立且正在运行的连接ConnectedThread
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
		// 开启ConnectThread线程，连接远程设备
		connectThread = new ConnectThread(device);
		connectThread.start();
		// 将程序状态设置为STATE_CONNECTING
		SetDeviceState(BluetoothConstant.STATE_CONNECTING);
	}

	/**
	 * 启动ConnectedThread线程，管理蓝牙连接(Start the ConnectedThread to begin managing a
	 * Bluetooth connection)
	 * 
	 * @param socket
	 * @param device
	 */
	public synchronized void ConnectedRemoteDevice(BluetoothSocket socket,
			BluetoothDevice device) {
		System.out.println("-------已建立连接-------");

		// 建立连接后ConnectThread取消线程
		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
		// 取消正在运行的连接
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
		// 取消AcceptThread线程，因为我们已经连接到了一个远程设备
		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}
		// 开启线程来管理连接和执行传输(Start the thread to manage the connection and perform
		// transmissions)
		connectedThread = new ConnectedThread(socket);
		connectedThread.start();

		// 发送连接的设备名返回UI Activity(Send the name of the connected device back to
		// the UI Activity)
		Message msg = myHandler
				.obtainMessage(BluetoothConstant.MESSAGE_DEVICE_NAME);
		// 实例化用于传递数据的Bundle
		Bundle bundle = new Bundle();
		bundle.putString(BluetoothConstant.DEVICE_NAME, device.getName());
		bundle.putString(BluetoothConstant.DEVICE_ADDR, device.getAddress());
		msg.setData(bundle);
		myHandler.sendMessage(msg);
		// 将程序状态设置为STATE_CONNECTED
		SetDeviceState(BluetoothConstant.STATE_CONNECTED);
	}

	/**
	 * 终止所有线程(Stop all threads)
	 */
	public synchronized void StopServer() {
		System.out.println("-------停止蓝牙服务-------");

		if (connectThread != null) {
			connectThread.cancel();
			connectThread = null;
		}
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}
		// 将程序状态设置为STATE_NONE
		SetDeviceState(BluetoothConstant.STATE_NONE);
	}
	/**
	 * 终止蓝牙服务端
	 */
	public synchronized void StopService() {
		if (acceptThread != null) {
			acceptThread.cancel();
			acceptThread = null;
		}
	}

	/**
	 * 以异步方式写数据到ConnectedThread(Write to the ConnectedThread in an
	 * unsynchronized manner)
	 * 
	 * @param out
	 */
	public void write(byte[] out) {
		// 创建一个临时对象(Create temporary object)
		ConnectedThread tmpConnectedThread;
		// 同步ConnectedThread的副本(Synchronize a copy of the ConnectedThread)
		synchronized (this) {
			if (deviceState != BluetoothConstant.STATE_CONNECTED)
				return;
			tmpConnectedThread = connectedThread;
		}
		// 执行异步写操作(Perform the write unsynchronized)
		tmpConnectedThread.write(out);
	}

	/**
	 * 连接失败(Connection Failed)
	 */
	private void ConnectionFailed() {
		// 重置状态为STATE_LISTEN(Reset state STATE_LISTEN)
		SetDeviceState(BluetoothConstant.STATE_LISTEN);
		// 声明一个消息对象，并将它加入到消息队列，发送失败的信息返回到Activity(Send a failure message back to
		// the Activity)
		Message msg = myHandler.obtainMessage(BluetoothConstant.MESSAGE_TOAST);
		// 实例化用于传递数据的Bundle
		Bundle bundle = new Bundle();
		bundle.putInt(BluetoothConstant.TOAST, R.string.tip_NoConnectToRemote);
		msg.setData(bundle);
		myHandler.sendMessage(msg);
	}

	/**
	 * 连接丢失(Connection Lost)
	 */
	private void ConnectionLost() {
		// 重置状态为STATE_LISTEN
		SetDeviceState(BluetoothConstant.STATE_LISTEN);
		// StopServer();
		// StartServer();
		// 声明一个消息对象，并将它加入到消息队列
		Message msg = myHandler.obtainMessage(BluetoothConstant.MESSAGE_TOAST);
		// 实例化用于传递数据的Bundle
		Bundle bundle = new Bundle();
		bundle.putInt(BluetoothConstant.TOAST, R.string.tip_LostConnect);
		msg.setData(bundle);
		myHandler.sendMessage(msg);
	}

	/**
	 * 作为服务端，监听客户端发来的连接请求，当连接被接受（或直至取消）时该线程终止 (This thread runs while listening
	 * for incoming connections. It runs until a connection is accepted)
	 * 
	 * @author Yongkun
	 * 
	 */
	private class AcceptThread extends Thread {
		// 声明本地服务器套接字(The local server socket)
		private final BluetoothServerSocket bluetoothServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			// 创建本地蓝牙监听服务器(Create a new listening server socket)
			try {
				tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
						BluetoothConstant.NAME, BluetoothConstant.MY_UUID);
			} catch (IOException e) {
				System.out.println("-------蓝牙监听失败-------");
			}
			bluetoothServerSocket = tmp;
		}

		public void run() {
			System.out.println("-------开启蓝牙监听服务-------");
			setName("AcceptThread");
			// 声明一个用于处理连接的Socket
			BluetoothSocket bluetoothSocket = null;

			// 如果未连接则监听Server Socket(Listen to the server socket if we're not
			// connected)
			while (deviceState != BluetoothConstant.STATE_CONNECTED) {
				try {
					// 建立连接(This is a blocking call and will only return on a
					// successful connection or an exception)
					bluetoothSocket = bluetoothServerSocket.accept();
				} catch (IOException e) {
					System.out.println("-------蓝牙监听失败-------");
					break;
				}

				// 如果连接被接受(If a connection was accepted, Do something)
				if (bluetoothSocket != null) {
					synchronized (BluetoothServerT.this) {
						switch (deviceState) {
						case BluetoothConstant.STATE_NONE:
						case BluetoothConstant.STATE_LISTEN:
						case BluetoothConstant.STATE_CONNECTING:
							// 启动连接线程。(Start the connected thread)
							ConnectedRemoteDevice(bluetoothSocket,
									bluetoothSocket.getRemoteDevice());
							break;
						case BluetoothConstant.STATE_CONNECTED:
							// 没有准备好或已连接, 终止新的套接字。(Either not ready or already
							// connected. Terminate new socket)
							try {
								bluetoothSocket.close();
							} catch (IOException e) {
								System.out
										.println("-------无法关闭未知的Socket-------");
							}
							break;
						}
					}
				}
			}
			System.out.println("-------结束蓝牙监听-------");
		}

		public void cancel() {
			try {
				bluetoothServerSocket.close();
				stop();
			} catch (IOException e) {
				System.out.println("-------监听服务关闭失败-------");
			}
		}
	}

	/**
	 * 作为客户端向远程服务端设备发送连接请求(As a client device to the remote server sends a
	 * connection request.)
	 * 
	 * @author Yongkun
	 * 
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket bluetoothSocket;
		private final BluetoothDevice bluetoothDevice;

		public ConnectThread(BluetoothDevice device) {
			bluetoothDevice = device;
			BluetoothSocket tmp = null;

			// 为蓝牙设备获取一个BluetoothSocket(Get a BluetoothSocket for a connection
			// with the given BluetoothDevice)
			try {
				tmp = device
						.createRfcommSocketToServiceRecord(BluetoothConstant.MY_UUID);
			} catch (IOException e) {
				System.out.println("-------BluetoothSocket创建失败-------");
			}
			bluetoothSocket = tmp;
		}

		public void run() {
			System.out.println("-------开始连接远程设备-------");
			setName("ConnectThread");

			// 设置蓝牙为不可发现状态(Always cancel discovery because it will slow down a
			// connection)
			bluetoothAdapter.cancelDiscovery();

			// 建立连接到BluetoothSocket(Make a connection to the BluetoothSocket)
			try {
				// 阻塞调用，将返回一个连接成功或异常(A blocking call and will only return on a
				// successful connection or an exception)
				bluetoothSocket.connect();
			} catch (IOException e) {
				ConnectionFailed();
				// 关闭socket(Close Socket)
				try {
					bluetoothSocket.close();
				} catch (IOException e2) {
					System.out.println("-------不能关闭bluetoothSocket-------");
				}
				// 重启服务，进入监听模式(Restart the service, enter the listening mode)
				BluetoothServerT.this.StartServer();
				return;
			}

			// 重置ConnectThread(Reset ConnectThread)
			synchronized (BluetoothServerT.this) {
				connectThread = null;
			}

			// 开启ConnectedThread(Start ConnectedThread)
			ConnectedRemoteDevice(bluetoothSocket, bluetoothDevice);
		}

		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException e) {
				System.out.println("-------BluetoothSocket关闭失败-------");
			}
		}
	}

	/**
	 * 已建立连接，对通信数据进行处理的Thread(Connected, the communication data processing
	 * Thread)
	 * 
	 * @author Yongkun
	 * 
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket bluetoothSocket;
		private final InputStream inputStream;
		private final OutputStream outputStream;

		public ConnectedThread(BluetoothSocket socket) {
			System.out.println("-------创建蓝牙通信管理-------");
			bluetoothSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// 获取BluetoothSocket的输入输出流(Get the BluetoothSocket input and output
			// streams)
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				System.out.println("-------临时通信Socket创建失败-------");
			}

			inputStream = tmpIn;
			outputStream = tmpOut;
		}

		public void run() {
			System.out.println("-------开始蓝牙通信管理线程-------");
			byte[] buffer = new byte[1024];
			int bytes;

			// 连接后继续监听InputStream(Keep listening to the InputStream while
			// connected)
			while (true) {
				try {
					// 从InputStream读数据(Read from the InputStream)
					bytes = inputStream.read(buffer);
					for (int i = 0; i < buffer.length; i++)
						System.out.println(buffer[i]);
					// 当控制盒为解码模式时，获取到的数据通知主线程，并做相应操作
					if (RemoteController.markDecoding) {
						// 将读到的数据添加到消息队列，并将其发送至目标对象
						myHandler.obtainMessage(BluetoothConstant.MESSAGE_READ,
								bytes, -1, buffer).sendToTarget();
					}

				} catch (IOException e) {
					System.out.println("-------连接丢失-------");
					ConnectionLost();
					break;
				}
			}
		}

		/**
		 * 写数据到OutStream(Write to the connected OutStream.)
		 * 
		 * @param buffer
		 */
		public void write(byte[] buffer) {
			try {
				outputStream.write(buffer);

				// 添加数据到消息队列，并将其发送至目标对象
				myHandler.obtainMessage(BluetoothConstant.MESSAGE_WRITE, -1,
						-1, buffer).sendToTarget();
			} catch (IOException e) {
				System.out.println("-------写数据错误-------");
			}
		}

		public void cancel() {
			try {
				bluetoothSocket.close();
			} catch (IOException e) {
				System.out.println("-------BluetoothSocket关闭失败-------");
			}
		}
	}
}
