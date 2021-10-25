import java.io.IOException;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) throws IOException {
    rc5 rc5 = new rc5(32,2, new byte[]{(byte) 0x91,0x5F,0x46,0x19, (byte) 0xBE,0x41, (byte) 0xB2,0x51,0x63,0x55, (byte) 0xA5,0x01,0x10, (byte) 0xA9, (byte) 0xCE, (byte) 0x91});
    rc5.encryptFile("str","str");
    rc5.decryptFile("str", "str");
    }
}
