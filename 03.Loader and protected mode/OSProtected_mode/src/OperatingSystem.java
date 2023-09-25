import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OperatingSystem {
    private Floppy floppyDisk = new Floppy();
    private int MAX_SECTOR_NUM = 18;

    public OperatingSystem(String s) {
        writeFileToFloppy(s, true, 0, 1);
    }

    private void writeFileToFloppy(String filename, boolean bootable, int cylinder, int beginSec) {
        File file = new File(filename);
        InputStream in = null;

        try {
            in = new FileInputStream(file);
            byte[] buf = new byte[512];

            //要将内核加载器加载到内存，然后加载器再将内核加载到内存，所以要让机器将软盘的头512字节加载到内存，头512字节的最后两个字节依旧必须是55，aa
            //这样可以实现系统内核超越之前设置的512字节
            if (bootable) {
                buf[510] = 0x55;
                buf[511] = (byte) 0xaa;
            }

            //OS_Kernal往往是大于512字节的，所以采用while不断读入写到磁盘上
            while (in.read(buf) > 0) {
                floppyDisk.writeFloppy(Floppy.MAGNETIC_HEAD.MAGNETIC_HEAD_0, cylinder, beginSec, buf);
                beginSec++;

                if (beginSec > MAX_SECTOR_NUM) {
                    cylinder++;
                    beginSec = 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void makeFloppy() {
        writeFileToFloppy("kernal.bat", false, 1, 2);
        floppyDisk.makeFloppy("system.img");
    }

    public static void main(String[] args) {
        OperatingSystem op = new OperatingSystem("boot.bat");
        op.makeFloppy();
    }
}
