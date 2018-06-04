package org.openhab.binding.broadlink.internal.discovery;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.broadlink.BroadlinkBindingConstants;
import org.openhab.binding.broadlink.internal.socket.BroadlinkSocket;
import org.openhab.binding.broadlink.internal.socket.BroadlinkSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BroadlinkDiscoveryService extends AbstractDiscoveryService
        implements BroadlinkSocketListener {

    private static final Set SUPPORTED_THING_TYPES;
    private final Logger logger = LoggerFactory.getLogger(BroadlinkDiscoveryService.class);

    static {
        SUPPORTED_THING_TYPES = BroadlinkBindingConstants.SUPPORTED_THING_TYPES_UIDS;
    }

    public BroadlinkDiscoveryService() {
        super(SUPPORTED_THING_TYPES, 10, true);
        logger.info("BroadlinkDiscoveryService - Constructed");
    }

    public void startScan() {
        BroadlinkSocket.registerListener(this);
        logger.warn("BroadlinkDiscoveryService - Beginning Broadlink device scan...");
        discoverDevices();
        waitUntilEnded();
        logger.warn("BroadlinkDiscoveryService - Ended Broadlink device scan...");

        BroadlinkSocket.unregisterListener(this);
    }

    protected synchronized void stopScan() {
        super.stopScan();
        removeOlderResults(getTimestampOfLastScan());
    }

    private void waitUntilEnded() {
        // No idea what was going on here; JAD seems to have made quite a mess of it...
//        final Semaphore discoveryEndedLock = new Semaphore(0);
//        scheduler.schedule(new Runnable() {
//
//            public void run() {
//                discoveryEndedLock.release();
//            }
//
//            final BroadlinkDiscoveryService this$0;
//            private final Semaphore val$discoveryEndedLock;
//
//
//            {
//                this$0 = BroadlinkDiscoveryService.this;
//                discoveryEndedLock = semaphore;
//                super();
//            }
//        }
//                , 10L, TimeUnit.SECONDS);
//        try {
//            discoveryEndedLock.acquire();
//        } catch (InterruptedException e) {
//            logger.error("Discovery problem {}", e.getMessage());
//        }


        try {
            logger.warn("BroadlinkDiscoveryService - Broadlink device scan waiting for 10 seconds to complete ...");
            Thread.sleep(10000L);
            logger.warn("BroadlinkDiscoveryService - 10 second wait complete ...");

        } catch (InterruptedException e) {
            logger.error("Discovery problem {}", e.getMessage());
        }
    }

    public void onDataReceived(String remoteAddress, int remotePort, String remoteMAC, ThingTypeUID thingTypeUID) {
        logger.info("Data received during Broadlink device discovery: from " + remoteAddress + ":" + remotePort + "[" + remoteMAC + "]");
        discoveryResultSubmission(remoteAddress, remotePort, remoteMAC, thingTypeUID);
    }

    private void discoveryResultSubmission(String remoteAddress, int remotePort, String remoteMAC, ThingTypeUID thingTypeUID) {
        if (logger.isDebugEnabled())
            logger.debug("Adding new Broadlink device on {} with mac '{}' to Smarthome inbox", remoteAddress, remoteMAC);
        Map properties = new HashMap(6);
        properties.put("ipAddress", remoteAddress);
        properties.put("port", Integer.valueOf(remotePort));
        properties.put("mac", remoteMAC);
        ThingUID thingUID = new ThingUID(thingTypeUID, remoteMAC.replace(":", "-"));
        if (thingUID != null) {
            if (logger.isDebugEnabled())
                logger.debug("Device '{}' discovered on '{}'.", thingUID, remoteAddress);
            if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_RM) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties).withLabel((new StringBuilder("Broadlink RM [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            } else if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_RM2) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties).withLabel((new StringBuilder("Broadlink RM2 [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            } else if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_RM3) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties).withLabel((new StringBuilder("Broadlink RM3 [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            } else if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_A1) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties).withLabel((new StringBuilder("Broadlink A1 [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            } else if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_SP1) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties).withLabel((new StringBuilder("SP1 [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            } else if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_SP2) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties).withLabel((new StringBuilder("SP2 [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            } else if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_SP3) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties).withLabel((new StringBuilder("SP3 [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            } else if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_MP1) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties).withLabel((new StringBuilder("Broadlink MP1 [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            } else if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_MP2) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties).withLabel((new StringBuilder("Broadlink MP2 [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            } else if (thingTypeUID == BroadlinkBindingConstants.THING_TYPE_S1C) {
                org.eclipse.smarthome.config.discovery.DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withThingType(BroadlinkBindingConstants.THING_TYPE_S1C).withProperties(properties).withLabel((new StringBuilder("Smart One Controller [")).append(remoteAddress).append("]").toString()).build();
                thingDiscovered(result);
            }
        }
    }

    private static InetAddress findNonLoopbackAddress() throws SocketException {
        Enumeration ifaces = NetworkInterface.getNetworkInterfaces();
        while (ifaces.hasMoreElements()) {
            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
            Enumeration inetAddrs = iface.getInetAddresses();
            while (inetAddrs.hasMoreElements()) {
                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                if (inetAddr.isLoopbackAddress()) {
                    continue; /* Loop/switch isn't completed */
                }

                if (inetAddr.isSiteLocalAddress()) {
                    return inetAddr;
                }
            }
        }

        return null;
    }

    private static InetAddress getLocalHostLANAddress()
            throws UnknownHostException {

        try {
            InetAddress candidateAddress = findNonLoopbackAddress();

            if (candidateAddress != null) return candidateAddress;

            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null)
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            else
                return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException((new StringBuilder("Failed to determine LAN address: ")).append(e).toString());
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    private static void discoverDevices() {
        try {
            InetAddress localAddress = getLocalHostLANAddress();
            int localPort = nextFreePort(localAddress, 1024, 3000);
            byte message[] = buildDisoveryPacket(localAddress.getHostAddress(), localPort);
            BroadlinkSocket.sendMessage(message, "255.255.255.255", 80);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static int nextFreePort(InetAddress host, int from, int to) {
        int port = randInt(from, to);
        do {
            if (isLocalPortFree(host, port))
                return port;
            port = ThreadLocalRandom.current().nextInt(from, to);
        } while (true);
    }

    private static boolean isLocalPortFree(InetAddress host, int port) {
        try {
            (new ServerSocket(port, 50, host)).close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static int randInt(int min, int max) {
        int randomNum = ThreadLocalRandom.current().nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private static byte[] buildDisoveryPacket(String host, int port) {
        String localAddress[] = null;
        localAddress = host.toString().split("\\.");
        int ipAddress[] = new int[4];
        for (int i = 0; i < 4; i++)
            ipAddress[i] = Integer.parseInt(localAddress[i]);

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(2);
        TimeZone timeZone = TimeZone.getDefault();
        int timezone = timeZone.getRawOffset() / 0x36ee80;
        byte packet[] = new byte[48];
        if (timezone < 0) {
            packet[8] = (byte) ((255 + timezone) - 1);
            packet[9] = -1;
            packet[10] = -1;
            packet[11] = -1;
        } else {
            packet[8] = 8;
            packet[9] = 0;
            packet[10] = 0;
            packet[11] = 0;
        }
        packet[12] = (byte) (calendar.get(1) & 0xff);
        packet[13] = (byte) (calendar.get(1) >> 8);
        packet[14] = (byte) calendar.get(12);
        packet[15] = (byte) calendar.get(11);
        packet[16] = (byte) (calendar.get(1) - 2000);
        packet[17] = (byte) (calendar.get(7) + 1);
        packet[18] = (byte) calendar.get(5);
        packet[19] = (byte) (calendar.get(2) + 1);
        packet[24] = (byte) ipAddress[0];
        packet[25] = (byte) ipAddress[1];
        packet[26] = (byte) ipAddress[2];
        packet[27] = (byte) ipAddress[3];
        packet[28] = (byte) (port & 0xff);
        packet[29] = (byte) (port >> 8);
        packet[38] = 6;
        int checksum = 48815;
        byte abyte0[];
        int k = (abyte0 = packet).length;
        for (int j = 0; j < k; j++) {
            byte b = abyte0[j];
            checksum += Byte.toUnsignedInt(b);
        }

        checksum &= 0xffff;
        packet[32] = (byte) (checksum & 0xff);
        packet[33] = (byte) (checksum >> 8);
        return packet;
    }


}
