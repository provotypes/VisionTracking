package model.networking;

import java.util.List;

import controller.Controller;
import model.vision.hsvIsolate.HSVIsolateController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkServerController extends Thread {
	int port;
	ServerSocket server;
	List<Client> clients;
	HSVIsolateController frameController;
	Controller controller;

	public NetworkServerController(int port, Controller controller, HSVIsolateController frameController) {
		this.port = port;
		try {
			this.frameController = frameController;
			this.controller = controller;
			this.server = new ServerSocket(port);
			server.setSoTimeout(1);
		} catch (IOException e) {
			System.out.println("IOException while creating the server");
		}
		clients = new ArrayList<Client>();
	}
	
	public void setVisionFrameController(Controller controller, HSVIsolateController visionFrameController) {
		this.frameController = visionFrameController;
		this.controller = controller;
	}

	public void run() {
		while (server != null && !server.isClosed() && !this.isInterrupted()) {

			// add connecting clients
			try {
				clients.add(new Client(controller, frameController, server.accept()));
//				System.out.println("Client Connected");
			} catch (IOException e) {
			}

			// remove disconnected clients
			try {
				for (int i = 0; i < clients.size(); i++) {
					if (clients.get(i).isClosed()) {
						clients.remove(i);
						i--;
//						System.out.println("Client Disconnected")
					}
				}
			} catch (IndexOutOfBoundsException e) {

			}

			// respond to all connected clients
			for (Client currentClient : clients) {
				currentClient.run();
			}

		}
		System.out.println("Server Thread Terminated");
	}
}
