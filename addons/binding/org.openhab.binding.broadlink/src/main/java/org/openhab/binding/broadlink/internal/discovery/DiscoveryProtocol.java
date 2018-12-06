package org.openhab.binding.broadlink.internal.discovery;

import org.openhab.binding.broadlink.internal.BroadlinkProtocol;
import org.openhab.binding.broadlink.internal.socket.BroadlinkSocket;
import org.openhab.binding.broadlink.internal.socket.BroadlinkSocketListener;
import org.openhab.binding.broadlink.internal.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.*;

 /* 
 * @author John Marshall
 */
public class DiscoveryProtocol {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryProtocol.class);

    private static class AsyncDiscoveryThread extends Thread {
		private final BroadlinkSocketListener listener;
		private final long timeoutMillis;
		private final DiscoveryFinishedListener finishedListener;
		
		AsyncDiscoveryThread(BroadlinkSocketListener listener, long timeoutMillis, DiscoveryFinishedListener finishedListener) {
			this.listener = listener;
			this.timeoutMillis = timeoutMillis;
			this.finishedListener = finishedListener;
		}
        public void run() {
        	BroadlinkSocket.registerListener(listener);
        	DiscoveryProtocol.discoverDevices();
        	DiscoveryProtocol.waitUntilEnded(timeoutMillis);
        	logger.warn("Ended Broadlink device scan...");
        	BroadlinkSocket.unregisterListener(listener);
			finishedListener.onDiscoveryFinished();
        }
	}

    public static void beginAsync(BroadlinkSocketListener listener, long discoveryTimeoutMillis, DiscoveryFinishedListener discoveryFinishedListener) {
        logger.warn("Beginning async Broadlink device scan; will wait " + discoveryTimeoutMillis + "ms for responses");
        AsyncDiscoveryThread adt = new AsyncDiscoveryThread(listener, discoveryTimeoutMillis, discoveryFinishedListener);
		adt.start();
    }

    public static void discoverDevices() {
        try {
            InetAddress localAddress = NetworkUtils.getLocalHostLANAddress();
            int localPort = NetworkUtils.nextFreePort(localAddress, 1024, 3000);
            byte message[] = BroadlinkProtocol.buildDiscoveryPacket(localAddress.getHostAddress(), localPort);
            BroadlinkSocket.sendMessage(message, "255.255.255.255", 80);
        } catch (UnknownHostException e) {
	    	logger.error("Failed to initiate discovery", e);
        }
    }

    private static void waitUntilEnded(long discoveryTimeoutMillis) {
        try {
            logger.warn("Broadlink device scan waiting for " + discoveryTimeoutMillis + " ms to complete ...");
            Thread.sleep(discoveryTimeoutMillis);
            logger.warn("Device scan: wait complete ...");

        } catch (InterruptedException e) {
            logger.error("problem {}", e.getMessage());
        }
    }
}
