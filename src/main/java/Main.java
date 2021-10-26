import java.io.IOException;
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) throws IOException {
    rc5 rc5 = new rc5(32,2, utils.genBytes(8));
    rc5.encryptFile("D:\\Users\\Alex\\IdeaProjects\\kriptolab\\inp.exe","D:\\Users\\Alex\\IdeaProjects\\kriptolab\\out.enc");
    rc5.decryptFile("D:\\Users\\Alex\\IdeaProjects\\kriptolab\\out.enc", "D:\\Users\\Alex\\IdeaProjects\\kriptolab\\out.exe");
    }
}
