%include "pm.inc"

org 0x9000

jmp LABEL_BEGIN

[SECTION .gdt]
LABEL_GDT:		Descriptor	0,		0,			0
LABEL_DESC_CODE32:	Descriptor	0,		SegCode32Len - 1,	DA_C + DA_32
LABEL_DESC_VIDEO:	Descriptor	0B8000h,        0ffffh,            	DA_DRW

GdtLen equ $ - LABEL_GDT
GdtPtr dw  GdtLen - 1
       dd  0

SelectorCode32 equ LABEL_DESC_CODE32 - LABEL_GDT
SelectorVideo  equ LABEL_DESC_VIDEO - LABEL_GDT

[SECTION .s16]
[BITS 16]
LABEL_BEGIN:
    mov  ax, cs
    mov  ds, ax
    mov  es, ax
    mov  ss, ax
    mov  sp, 0100h

    ;初始化全局描述符表（GDT）中的一个段描述符
    xor  eax, eax
    mov  ax,  cs
    shl  eax, 4
    add  eax, LABEL_SEG_CODE32
    mov  word [LABEL_DESC_CODE32 + 2], ax
    shr  eax, 16
    mov  byte [LABEL_DESC_CODE32 + 4], al
    mov  byte [LABEL_DESC_CODE32 + 7], ah

    ;初始化全局描述符表（GDT）以及加载 GDT 地址到 GDTR 寄存器
    xor  eax, eax
    mov  ax, ds
    shl  eax, 4
    add  eax, LABEL_GDT
    mov  dword [GdtPtr + 2], eax

    lgdt [GdtPtr]

    ;关中断
    cli

    ;执行了一些与控制计算机硬件相关的操作，主要涉及到两个端口的输入输出以及对控制寄存器 CR0 的修改
    in   al, 92h
    or   al, 00000010b
    out  92h, al

    mov  eax, cr0
    or   eax, 1
    mov  cr0, eax

    jmp  dword SelectorCode32:0

[SECTION .s32]
[BITS 32]
LABEL_SEG_CODE32:
    mov  ax, SelectorVideo
    mov  gs, ax		;将信息写入gs指向的内存后,信息会显示到屏幕上
    mov  si, msg
    mov  ebx, 10
    mov  ecx, 2

;将msg指向的内容写到指定的显存的位置,并设置显示字符的一些属性
ShowChar:
    mov  edi, (80*11)
    add  edi, ebx
    mov  eax, edi
    mul  ecx
    mov  edi, eax
    mov  ah, 0ch
    mov  al, [si]
    cmp  al, 0
    je   end
    add  ebx, 1
    add  si, 1
    mov  [gs:edi], ax
    jmp  ShowChar

end:
    jmp  $
    msg:
    DB  "Protected Mode", 0

SegCode32Len equ $ - LABEL_SEG_CODE32
