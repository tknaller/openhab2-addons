/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.broadlink.internal.socket;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.broadlink.internal.Hex;
import org.openhab.binding.broadlink.internal.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Threaded socket implementation 
 *
 * @author John Marshall/Cato Sognen - Initial contribution
 */

@NonNullByDefault
public class BroadlinkSocket {
    private static byte buffer[];
    private static DatagramPacket datagramPacket;
    @Nullable
    private static MulticastSocket socket = null;
    @Nullable
    private static Thread socketReceiveThread;
    private static List<BroadlinkSocketListener> listeners = new ArrayList<BroadlinkSocketListener>();
    private static final Logger logger = LoggerFactory.getLogger(BroadlinkSocket.class);

    static {
        buffer = new byte[1024];
        datagramPacket = new DatagramPacket(buffer, buffer.length);
    }

    @NonNullByDefault
    private static class ReceiverThread extends Thread {

        public void run() {
            receiveData(BroadlinkSocket.socket, BroadlinkSocket.datagramPacket);
        }

        private void receiveData(@Nullable MulticastSocket socket, DatagramPacket dgram) {
            try {
                while (true) {
                    socket.receive(dgram);
                    BroadlinkSocketListener listener;
                    byte remoteMAC[];
                    org.eclipse.smarthome.core.thing.ThingTypeUID deviceType;
                    int model;
                    for (Iterator<BroadlinkSocketListener> iterator = (new ArrayList<BroadlinkSocketListener>(BroadlinkSocket.listeners)).iterator(); iterator.hasNext(); listener.onDataReceived(dgram.getAddress().getHostAddress(), dgram.getPort(), Hex.decodeMAC(remoteMAC), deviceType, model)) {
                        listener = iterator.next();
                        byte receivedPacket[] = dgram.getData();
                        remoteMAC = Arrays.copyOfRange(receivedPacket, 58, 64);
                        model = Byte.toUnsignedInt(receivedPacket[52]) | Byte.toUnsignedInt(receivedPacket[53]) << 8;
                        deviceType = ModelMapper.getThingType(model);
                    }

                }
            } catch (IOException e) {
                if (!isInterrupted())
                    logger.error("Error while receiving '{}'", e);
            }
            BroadlinkSocket.logger.info("Receiver thread ended");
        }

        private ReceiverThread() {
        }
    }

    public static void registerListener(BroadlinkSocketListener listener) {
        listeners.add(listener);
        if (socket == null) {
            setupSocket();
        }
    }

    public static void unregisterListener(BroadlinkSocketListener listener) {
        listeners.remove(listener);
        if (listeners.isEmpty() && socket != null) {
            closeSocket();
        }
    }

    private static void setupSocket() {
        synchronized (BroadlinkSocket.class) {
            try {
                socket = new MulticastSocket();
            } catch (IOException e) {
                logger.error("Setup socket error '{}'.", e.getMessage());
            }
            socketReceiveThread = new ReceiverThread();
            socketReceiveThread.start();
        }
    }

    private static void closeSocket() {
        synchronized (BroadlinkSocket.class) {
            if (socketReceiveThread != null) {
                socketReceiveThread.interrupt();
            }
            if (socket != null) {
                logger.info("Socket closed");
                socket.close();
                socket = null;
            }
        }
    }

    public static void sendMessage(byte message[]) {
        sendMessage(message, "255.255.255.255", 80);
    }

    public static void sendMessage(byte message[], String host, int port) {
        try {
            DatagramPacket sendPacket = new DatagramPacket(message, message.length, InetAddress.getByName(host), port);
            socket.send(sendPacket);
        } catch (IOException e) {
            logger.error("IO Error sending message: '{}'", e.getMessage());
        }
    }
}
