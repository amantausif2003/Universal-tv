package com.remote.control.allsmarttv.Utils;

import android.os.SystemClock;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MdnsClientUtil {

    private static final int FLAGS_RESPONSE = 33792;
    private static final String PROTO_TOKEN_TCP = "_tcp";
    private static final String PROTO_TOKEN_UDP = "_udp";
    private final Set<SoftReference<Answer>> mKnownAnswers = new LinkedHashSet();
    private InetSocketAddress mMulticastAddress;
    private final NetworkInterface mNetworkInterface;
    private final byte[] mReceiveBuffer = new byte[65536];
    private Thread receiveThread;
    private Thread sendThread;
    private final String serviceName;
    private volatile boolean shouldStop;
    private MulticastSocket socket;
    private String[] serviceWords;

    public static class Answer {
        private final byte[] mAnswer;
        private final long mInitialTimeMillis = SystemClock.elapsedRealtime();
        private final int mInitialTtlSeconds;

        public Answer(byte[] bArr, int i) {
            this.mAnswer = bArr;
            this.mInitialTtlSeconds = i;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Answer) {
                return this.mAnswer.equals(((Answer) obj).mAnswer);
            }
            return false;
        }

        public byte[] getAnswer() {
            return this.mAnswer;
        }

        public int hashCode() {
            if (this.mAnswer != null) {
                return this.mAnswer.hashCode();
            }
            return 0;
        }

        public boolean isExpired() {
            return ((double) ((SystemClock.elapsedRealtime() - this.mInitialTimeMillis) / 1000)) > ((double) this.mInitialTtlSeconds) / 2.0d;
        }
    }

    public MdnsClientUtil(String str, NetworkInterface networkInterface) {
        this.serviceName = str;
        this.mNetworkInterface = networkInterface;
        this.serviceWords = str.split("\\.");
        String[] strArr = this.serviceWords;
        for (String str2 : strArr) {
        }
    }

    private DatagramPacket buildQueryPacket(String str, boolean z) {
        int i = 0;
        MdnsWriter mdnsWriter = new MdnsWriter();
        removeExpiredAnswers();
        synchronized (this.mKnownAnswers) {
            mdnsWriter.writeUInt16(0);
            mdnsWriter.writeUInt16(0);
            mdnsWriter.writeUInt16(1);
            mdnsWriter.writeUInt16(this.mKnownAnswers.size());
            mdnsWriter.writeUInt16(0);
            mdnsWriter.writeUInt16(0);
            mdnsWriter.writeLabels(str);
            mdnsWriter.writeUInt8(0);
            mdnsWriter.writeUInt16(12);
            if (z) {
                i = 32768;
            }
            mdnsWriter.writeUInt16(i | 1);
            for (SoftReference<Answer> softReference : this.mKnownAnswers) {
                Answer answer = softReference.get();
                if (answer != null) {
                    mdnsWriter.writeBytes(answer.getAnswer());
                }
            }
        }
        return mdnsWriter.getPacket();
    }

    private void dumpData(byte[] bArr, int i, int i2) {
    }

    private void receiveLoop() {
        byte[] bArr = new byte[4];
        byte[] bArr2 = new byte[16];
        DatagramPacket datagramPacket = new DatagramPacket(this.mReceiveBuffer, this.mReceiveBuffer.length);
        while (!this.shouldStop) {
            try {
                this.socket.receive(datagramPacket);
                MdnsReader mdnsReader = new MdnsReader(datagramPacket);
                mdnsReader.readUInt16();
                if (mdnsReader.readUInt16() == FLAGS_RESPONSE) {
                    mdnsReader.readUInt16();
                    int readUInt16 = mdnsReader.readUInt16();
                    int readUInt162 = mdnsReader.readUInt16();
                    int readUInt163 = mdnsReader.readUInt16();
                    int position = mdnsReader.getPosition();
                    HashMap hashMap = new HashMap();
                    HashMap hashMap2 = new HashMap();
                    HashMap<String, MdnsResUtil.Builder> hashMap3 = new HashMap();
                    HashMap hashMap4 = new HashMap();
                    for (int i = 0; i < readUInt16 + readUInt162 + readUInt163; i++) {
                        String[] readLabels = mdnsReader.readLabels();
                        int readUInt164 = mdnsReader.readUInt16();
                        mdnsReader.readUInt16();
                        long readUInt32 = mdnsReader.readUInt32();
                        int readUInt165 = mdnsReader.readUInt16();
                        String join = TextUtils.join(".", readLabels);
                        if (i < readUInt16) {
                            if (!(readUInt32 <= 0)) {
                                if (!(readUInt32 >= 604800)) {
                                    byte[] copyOfRange = Arrays.copyOfRange(datagramPacket.getData(), position, position + readUInt165);
                                    synchronized (this.mKnownAnswers) {
                                        this.mKnownAnswers.add(new SoftReference<>(new Answer(copyOfRange, (int) readUInt32)));
                                    }
                                }
                            }
                        }
                        switch (readUInt164) {
                            case 1:
                                mdnsReader.readBytes(bArr);
                                try {
                                    hashMap.put(join, (Inet4Address) InetAddress.getByAddress(bArr));
                                    break;
                                } catch (UnknownHostException e) {
                                    break;
                                }
                            case 12:
                                mdnsReader.readLabels();
                                break;
                            case 16:
                                int position2 = mdnsReader.getPosition() + readUInt165;
                                ArrayList arrayList = new ArrayList();
                                while (mdnsReader.getPosition() < position2) {
                                    arrayList.add(mdnsReader.readString());
                                }
                                hashMap4.put(join, arrayList);
                                break;
                            case 28:
                                mdnsReader.readBytes(bArr2);
                                try {
                                    hashMap2.put(join, (Inet6Address) InetAddress.getByAddress(bArr2));
                                    break;
                                } catch (UnknownHostException e2) {
                                    break;
                                }
                            case 33:
                                MdnsResUtil.Builder builder = new MdnsResUtil.Builder();
                                builder.updateTimeToLive(readUInt32);
                                builder.setFqdn(join);
                                builder.setServicePriority(mdnsReader.readUInt16());
                                builder.setServiceWeight(mdnsReader.readUInt16());
                                builder.setServicePort(mdnsReader.readUInt16());
                                builder.setServiceHost(TextUtils.join(".", mdnsReader.readLabels()));
                                builder.setServiceInstanceName(readLabels[0]);
                                builder.setServiceName(readLabels[1]);
                                String str = readLabels[2];
                                if (str.equals(PROTO_TOKEN_TCP)) {
                                    builder.setServiceProtocol(1);
                                } else if (str.equals(PROTO_TOKEN_UDP)) {
                                    builder.setServiceProtocol(2);
                                }
                                if (readLabels.length == 4) {
                                    int i2 = 1;
                                    boolean z = true;
                                    String[] strArr = this.serviceWords;
                                    int length = strArr.length;
                                    int i3 = 0;
                                    while (true) {
                                        if (i3 < length) {
                                            if (readLabels[i2].equalsIgnoreCase(strArr[i3])) {
                                                i2++;
                                                i3++;
                                            } else {
                                                z = false;
                                            }
                                        }

                                        if (z) {
                                            hashMap3.put(join, builder);
                                            break;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                else {
                                    break;
                                }
                            default:
                                mdnsReader.skip(readUInt165);
                                break;
                        }
                    }
                    for (Map.Entry<String, MdnsResUtil.Builder> entry : hashMap3.entrySet()) {
                        String str2 = (String) entry.getKey();
                        MdnsResUtil.Builder builder2 = (MdnsResUtil.Builder) entry.getValue();
                        String serviceHost = builder2.build().getServiceHost();
                        Inet4Address inet4Address = (Inet4Address) hashMap.get(serviceHost);
                        if (inet4Address != null) {
                            builder2.addInet4Address(inet4Address);
                        }
                        Inet6Address inet6Address = (Inet6Address) hashMap2.get(serviceHost);
                        if (inet6Address != null) {
                            builder2.addInet6Address(inet6Address);
                        }
                        List<String> list = (List) hashMap4.get(str2);
                        if (list != null) {
                            for (String str3 : list) {
                                builder2.addTextString(str3);
                            }
                        }
                        onResponseReceived(builder2.build());
                    }
                } else {
                    continue;
                }
            } catch (IOException e3) {
                if (this.shouldStop) {
                }
            }
        }
    }

    private void removeExpiredAnswers() {
        synchronized (this.mKnownAnswers) {
            Iterator<SoftReference<Answer>> it = this.mKnownAnswers.iterator();
            int i = 32;
            while (it.hasNext()) {
                if (i <= 32) {
                    Answer answer = it.next().get();
                    if (answer == null || answer.isExpired()) {
                        it.remove();
                    }
                } else {
                    it.remove();
                }
                i--;
            }
        }
    }

    private void sendLoop() {
        boolean z = true;
        int i = 1000;
        while (!this.shouldStop) {
            try {
                sendQuery(this.serviceName, z);
                z = false;
            } catch (IOException e) {
            }
            try {
                Thread.sleep((long) i);
                if (i < 15000) {
                    i *= 2;
                }
            } catch (InterruptedException e2) {
            }
        }
    }

    private void sendQuery(String str, boolean z) throws IOException {
        this.socket.send(buildQueryPacket(str, z));
    }

    private void waitForThread(Thread thread) {
        while (true) {
            try {
                thread.interrupt();
                thread.join();
                break;
            } catch (InterruptedException e) {
            }
        }
    }

    public abstract void onResponseReceived(MdnsResUtil mdnsResUtil);

    public synchronized void start() throws IOException {
        if (this.socket == null) {
            this.shouldStop = false;
            this.socket = new MulticastSocket((int) MdnsConst.MDNS_PORT);
            this.socket.setTimeToLive(1);
            if (this.mNetworkInterface != null) {
                this.socket.setNetworkInterface(this.mNetworkInterface);
            }
            this.mMulticastAddress = new InetSocketAddress(MdnsConst.MDNS_ADDRESS, (int) MdnsConst.MDNS_PORT);
            this.socket.joinGroup(this.mMulticastAddress, this.mNetworkInterface);
            this.receiveThread = new Thread(new Runnable() {

                public void run() {
                    MdnsClientUtil.this.receiveLoop();
                }
            });
            this.receiveThread.start();
            this.sendThread = new Thread(new Runnable() {

                public void run() {
                    MdnsClientUtil.this.sendLoop();
                }
            });
            this.sendThread.start();
        }
    }

    public synchronized void stop() {
        if (this.socket != null) {
            this.shouldStop = true;
            try {
                this.socket.leaveGroup(this.mMulticastAddress, this.mNetworkInterface);
            } catch (IOException e) {
            }
            this.socket.close();
            if (this.receiveThread != null) {
                waitForThread(this.receiveThread);
                this.receiveThread = null;
            }
            if (this.sendThread != null) {
                waitForThread(this.sendThread);
                this.sendThread = null;
            }
            this.socket = null;
        }
    }
}
