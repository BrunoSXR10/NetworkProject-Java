package datagram;
import java.net.*;

public class test {
    DatagramSocket socket = new DatagramSocket(12345);
    byte[] buffer = new byte[1024];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

}
