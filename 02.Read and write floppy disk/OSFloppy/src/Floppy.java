import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Floppy {
    enum MAGNETIC_HEAD {
        MAGNETIC_HEAD_0,
        MAGNETIC_HEAD_1
    };

    //SECTOR_SIZE:一个扇区的大小
    //SECTOR_COUNT:一个磁道/柱面扇区的数量
    //CYLINDER_COUNT:盘片磁道/柱面的数量
    public int SECTOR_SIZE = 512;
    private  int CYLINDER_COUNT = 80;
    private  int SECTOR_COUNT = 18;

    private MAGNETIC_HEAD magneticHead = MAGNETIC_HEAD.MAGNETIC_HEAD_0;
    private int current_cylinder = 0;
    private  int current_sector = 0;

    private HashMap<Integer, ArrayList<ArrayList<byte[]>>> floppy = new HashMap<Integer, ArrayList<ArrayList<byte[]>>>();

    //构造方法
    public Floppy() {
        initFloppy();
    }

    //磁盘的构造
    private void initFloppy() {
        //一个磁盘有两个盘面，对其进行初始化
        floppy.put(MAGNETIC_HEAD.MAGNETIC_HEAD_0.ordinal(), initFloppyDisk());
        floppy.put(MAGNETIC_HEAD.MAGNETIC_HEAD_1.ordinal(), initFloppyDisk());
    }

    //盘片的构造
    private ArrayList<ArrayList<byte[]>> initFloppyDisk() {
        ArrayList<ArrayList<byte[]>> floppyDisk = new ArrayList<ArrayList<byte[]>>();
        for (int i = 0; i < CYLINDER_COUNT; i++) {
            floppyDisk.add(initCylinder());
        }

        return floppyDisk;
    }

    //磁道(柱面)的构造
    private ArrayList<byte[]> initCylinder() {
        ArrayList<byte[]> cylinder = new ArrayList<byte[]>();
        for (int i = 0; i < SECTOR_COUNT; i++) {
            byte[] sector = new byte[SECTOR_SIZE];
            cylinder.add(sector);
        }

        return cylinder;
    }

    public void setMagneticHead(MAGNETIC_HEAD magneticHead) {
        this.magneticHead = magneticHead;
    }

    public void setCylinder(int cylinder) {
        //cylinder编号为0～79(与数组下标对应)
        if (cylinder < 0) {
            this.current_cylinder = 0;
        }
        else if (cylinder >= 80) {
            this.current_cylinder = 79;
        }
        else {
            this.current_cylinder = cylinder;
        }
    }

    public void setSector(int sector) {
        //sector编号为1～18(与数组下标不对应)
        if (sector < 0) {
            this.current_sector = 0;
        }
        else if (sector > 18) {
            this.current_sector = 18 - 1;
        }
        else {
            this.current_sector = sector - 1;
        }
    }

    //读盘
    public byte[] readFloppy(MAGNETIC_HEAD head, int cylinder_num, int sector_num) {
        setMagneticHead(head);
        setCylinder(cylinder_num);
        setSector(sector_num);

        ArrayList<ArrayList<byte[]>> disk = floppy.get(this.magneticHead.ordinal());
        ArrayList<byte[]> cylinder = disk.get(this.current_cylinder);
        byte[] sector = cylinder.get(this.current_sector);

        return sector;
    }

    //写盘
    public void writeFloppy(MAGNETIC_HEAD head, int cylinder_num, int sector_num, byte[] buf) {
        setMagneticHead(head);
        setCylinder(cylinder_num);
        setSector(sector_num);

        ArrayList<ArrayList<byte[]>> disk = floppy.get(this.magneticHead.ordinal());
        ArrayList<byte[]> cylinder = disk.get(this.current_cylinder);
        byte[] buffer = cylinder.get(this.current_sector);
        System.arraycopy(buf, 0, buffer, 0, buf.length);
    }

    //形成系统镜像文件system.img
    public void makeFloppy(String filename) {
        try {
            /*
             * 虚拟软盘是存粹的二进制文件，它的逻辑结构如下：
             * 前512*18字节的内容对应盘面0，柱面0的所有扇区内容
             * 接着的512*18字节的内容对应盘面1，柱面0的所有扇区内容
             * 再接着的512*18字节的内容对应盘面0，柱面1所有扇区内容
             * 再接着512*18字节的内容对应盘面1，柱面1所有扇区内容
             * 以此类推
             */
            DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
            for (int cylinder = 0; cylinder < CYLINDER_COUNT; cylinder++) {
                for (int head = 0; head <= MAGNETIC_HEAD.MAGNETIC_HEAD_1.ordinal(); head++) {
                    for (int sector = 1; sector <= SECTOR_COUNT; sector++) {
                        byte[] buf = readFloppy(MAGNETIC_HEAD.values()[head], cylinder, sector);

                        out.write(buf);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}


