import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OperatingSystem {
    private Floppy floppyDisk = new Floppy();

    public OperatingSystem(String s) {
        writeFileToFloppy(s);
    }

    private void writeFileToFloppy(String filename) {
        File file = new File(filename);
        InputStream in = null;

        try {
            in = new FileInputStream(file);
            byte[] buf = new byte[512];
            buf[510] = 0x55;
            buf[511] = (byte)0xaa;
            if (in.read(buf) != -1) {
                //将系统内核读入到磁盘第0面，第0柱面，第1扇区
                floppyDisk.writeFloppy(Floppy.MAGNETIC_HEAD.MAGNETIC_HEAD_0, 0, 1, buf);
            }
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void makeFloppy() {
        String s = "This is a text from cylinder 1 and sector 2 n hello sir by the way";
        floppyDisk.writeFloppy(Floppy.MAGNETIC_HEAD.MAGNETIC_HEAD_0, 1,2, s.getBytes());
        floppyDisk.makeFloppy("system.img");
    }

    public static void main (String[] args) {
        OperatingSystem op = new OperatingSystem("boot.bat");
        op.makeFloppy();
    }
}
