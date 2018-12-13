package org.openhab.binding.broadlink.internal.socket;

import org.openhab.binding.broadlink.config.BroadlinkDeviceConfiguration;
import org.openhab.binding.broadlink.internal.ThingLogger;

import java.io.IOException;
import java.net.*;

public class RetryableSocket {
    private DatagramSocket socket = null;
    private final ThingLogger thingLogger;
    private final BroadlinkDeviceConfiguration thingConfig;

    public RetryableSocket(BroadlinkDeviceConfiguration thingConfig, ThingLogger thingLogger) {
        this.thingConfig = thingConfig;
        this.thingLogger = thingLogger;
    }

    public boolean sendDatagram(byte message[], String purpose) {
        try {
            thingLogger.logTrace("Sending " + purpose + " to " + thingConfig.getIpAddress() + ":" + thingConfig.getPort());
            if (socket == null || socket.isClosed()) {
                thingLogger.logTrace("No existing socket ... creating");
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setReuseAddress(true);
                socket.setSoTimeout(5000);
            }
            InetAddress host = InetAddress.getByName(thingConfig.getIpAddress());
            int port = thingConfig.getPort();
            DatagramPacket sendPacket = new DatagramPacket(message, message.length, new InetSocketAddress(host, port));
            socket.send(sendPacket);
        } catch (IOException e) {
            thingLogger.logError("IO error during UDP command sending: {}", e.getMessage());
            return false;
        }
        thingLogger.logTrace("Sending " + purpose + " complete");
        return true;
    }

    public byte[] receiveDatagram(String purpose) {
        thingLogger.logTrace("Receiving " + purpose);

        try {
            if (socket == null) {
                thingLogger.logError("receiveDatagram " + purpose + " for socket was unexpectedly null");
            } else {
                byte response[] = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(response, response.length);
                socket.receive(receivePacket);
                response = receivePacket.getData();
                return response;
            }
        } catch (SocketTimeoutException ste) {
            thingLogger.logDebug("No further " + purpose + " response received for device");
        } catch (Exception e) {
            thingLogger.logError("While {} - IO Exception: '{}'", purpose, e.getMessage());
        }

        return null;
    }

    /**
     * Send a packet to the device, and expect a response.
     * If retries in the thingConfig is > 0, we will send
     * and receive repeatedly if we fail to get any response.
     */
    public byte[] sendAndReceive(byte message[], String purpose) {
        return sendAndReceiveOneTime(message, purpose);
    }

    private byte[] sendAndReceiveOneTime(byte message[], String purpose) {
        if (sendDatagram(message, purpose)) {
            return receiveDatagram(purpose);
        }

        return null;
    }

    public void close() {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }
}
