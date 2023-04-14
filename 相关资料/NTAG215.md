页数: 0x00, data: 04 12 E3 7D
页数: 0x01, data: 01 80 4B 03
页数: 0x02, data: C9 48 00 00
页数: 0x03, data: E1 10 3E 00
页数: 0x04, data: 03 00 FE 00


I/System.out: 页数: 0x81, data: 00 00 00 00
I/System.out: 页数: 0x82, data: 00 00 00 BD
I/System.out: 页数: 0x83, data: 04 00 00 FF
I/System.out: 页数: 0x84, data: 00 05 00 00
I/System.out: 页数: 0x85, data: 00 00 00 00
I/System.out: 页数: 0x86, data: 00 00 00 00


| 页数    | 数据1 | 数据2 | 数据3 | 数据4 |
|---------|-------|-------|-------|-------|
| 0x00    | 04    | 12    | E3    | 7D    |
| 0x01    | 01    | 80    | 4B    | 03    |
| 0x02    | C9    | 48    | 00    | 00    |
| 0x03    | E1    | 10    | 3E    | 00    |

<img src = ../相关资料/img/NTAG215_MemoryOrganization.png>


### 1.
540字节, 135页，每页4个字节   

序列号
~~~
SN = SN0:SN1:SN2:SN4:SN5:SN6:SN7
   = 04:12:E3:01:80:4B:03
~~~

校验位1
~~~
CheckByte1 = Page0-Byte3
  = 0x88⊕SN1⊕SN2⊕0xSN3
  = 0x88⊕0x04⊕0x12⊕0xE3
  = 0x7D
~~~

校验位2 
~~~
CheckByte1 = Page2-Byte0
  = SN4⊕SN5⊕SN6⊕0xSN7
  = 0x01⊕0x80⊕0x4B⊕0x03
  = 0xC9
~~~

静态锁字节
~~~
lockBytes = Page2-Byte2 + Page2-Byte3
~~~
LOTP表示锁定Page3为只读  
L4-L15的某一位设置为1，表示对应的页面锁定为只读   
BLx表示对锁定位本身进行锁定
注意，这16个锁定位一旦锁死无法修改为0

动态锁定字节
NTAG215动态锁定字节为Page82H的Byte0,Byte1,Byte2




