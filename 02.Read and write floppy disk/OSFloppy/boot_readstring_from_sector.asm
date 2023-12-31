org 0x7c00

jmp entry
db 0x90
DB "OSKERNAL"
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
	mov ax, 0
	mov ss, ax
	mov ds, ax
	mov es, ax
	mov si, msg

readFloppy:
	mov CH, 1	;CH存储柱面号
	mov DH, 0	;DH存储磁头号
	mov CL, 2	;CL存储扇区号

	mov BX, msg	;数据存储缓冲区

	mov AH, 0x02	;表示要做读盘操作	
	mov AL, 1	;表示要连续读取多少个扇区
	mov DL, 0	;驱动器号，一般我们只有一个软盘驱动器，所以写为0

	INT 0x13	;调用BIOS中断实现磁盘读取功能

	jc error
	
putloop:
	mov al, [si]
	add si, 1
	cmp al, 0
	je  fin
	mov ah, 0x0e
	mov bx, 15
	int 0x10
	jmp putloop

fin:
	HLT
	jmp fin

error:
	mov si, errmsg
	jmp putloop

msg:
	RESB 64

errmsg:
	DB "error"
