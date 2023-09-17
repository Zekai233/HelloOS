import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class OperatingSystem {
    //设置一些初始化的数据，然后调用BIOS中断将某个缓冲区的字符打印到屏幕上去，然后进入一个死循环
    //将上述功能的代码由汇编实现，接着将汇编转换为16进制，再将其放到imgContent中
    private int[] imgContent = new int[] {
            0xeb,0x4e,0x90,0x48,0x45,0x4c,0x4c,0x4f,0x49,0x50,0x4c,0x00,0x02,0x01,0x01,0x00,0x02,0xe0,
            0x00,0x40,0x0b,0xf0,0x09,0x00,0x12,0x00,0x02,0x00,0x00,0x00,0x00,0x00,0x40,0x0b,0x00,0x00,0x00,0x00,0x29,
            0xff,0xff,0xff,0xff,0x48,0x45,0x4c,0x4c,0x4f,0x2d,0x4f,0x53,0x20,0x20,0x20,0x46,0x41,0x54,0x31,0x32,
            0x20,0x20,0x20,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0xb8,0x00,0x00,0x8e,
            0xd0,0xbc,0x00,0x7c,0x8e,0xd8,0x8e,0xc0,0xbe,0x74,0x7c,0x8a,
            0x04,0x83,0xc6,0x01,0x3c,0x00,0x74,0x09,0xb4,0x0e,0xbb,0x0f,0x00,0xcd,0x10,0xeb,0xee,0xf4,0xeb,0xfd
    };

    private ArrayList<Integer> imgByteToWrite = new ArrayList<Integer>();

    public OperatingSystem(String s) {
        for (int i = 0; i < imgContent.length; i++) {
            imgByteToWrite.add(imgContent[i]);
        }

        //加入换行符和传进函数的字符串参数
        imgByteToWrite.add(0x0a);
        imgByteToWrite.add(0x0a);
        for (int j = 0; j < s.length(); j++) {
            imgByteToWrite.add((int)s.charAt(j));
        }
        imgByteToWrite.add(0x0a);

        //0x1fe=510
        int len = 0x1fe;
        int curSize = imgByteToWrite.size();
        for (int k = 0; k < len - curSize; k++) {
            imgByteToWrite.add(0);
        }

        //要想让机器将软盘的头512字节当作操作系统的内核加载到内存，头512字节的最后两个字节必须是55，aa(Intel设计的)
        imgByteToWrite.add(0x55);
        imgByteToWrite.add(0xaa);
        imgByteToWrite.add(0xf0);
        imgByteToWrite.add(0xff);
        imgByteToWrite.add(0xff);

        //除了头部有内容，其他用0来补充
        len = 0x168000;
        curSize = imgByteToWrite.size();
        for (int l = 0; l < len - curSize; l++) {
            imgByteToWrite.add(0);
        }
    }

    public void makeFllopy() {
        try {
            //在磁盘上创建一个上述imgByteToWrite存储内容大小字节的二进制文件
            //将imgContent的内容放到该文件的头512字节，这个二进制文件将作为一个1.5M的虚拟软盘用于当作虚拟机的启动软盘
            DataOutputStream out  = new DataOutputStream(new FileOutputStream("system.img"));
            for (int i = 0; i < imgByteToWrite.size(); i++) {
                out.writeByte(imgByteToWrite.get(i).byteValue());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        OperatingSystem op = new OperatingSystem("hello, this is the first line of my operating system code");
        op.makeFllopy();
    }
}
