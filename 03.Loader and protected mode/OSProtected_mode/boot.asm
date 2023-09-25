org 0x7c00;

LOAD_ADDR EQU 0X9000

jmp  entry
db   0x90
DB   "OSKERNEL"
DW   512
DB   1
DW   1
DB   2
DW   224
DW   2880
DB   0xf0
DW   9
DW   18
DW   2
DD   0
DD   2880
DB   0,0,0x29
DD   0xFFFFFFFF
DB   "MYFIRSTOS  "
DB   "FAT12   "
RESB  18

entry:
    mov  ax, 0
    mov  ss, ax
    mov  ds, ax
    mov  es, ax
    mov  si, ax

readFloppy:
    mov  CH, 1	;柱面号
    mov  DH, 0	;磁头号
    mov  CL, 2	;扇区号

    mov  BX, LOAD_ADDR	;ES:BX数据存储缓冲区,即将kernal读入内存的位置

    mov  AH, 0x02
    mov  AL, 1
    mov  DL, 0

    INT  0x13

    jc   fin

    jmp  LOAD_ADDR

fin:
    HLT
    jmp  fin
