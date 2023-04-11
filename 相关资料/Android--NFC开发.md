##1NFC开发

[AndroidNFC学习笔记](https://juejin.cn/post/7075257574131499038)

[AndroidNFC使用详解](https://blog.csdn.net/u011082160/article/details/89146192),包含M1卡的数据解析过程

[Android与RFID的点点滴滴（二）RFID通讯协议](https://blog.csdn.net/qq_32368129/article/details/114312929),详细解释了RFID协议

[AndroidNFC功能简单实现](https://www.jianshu.com/p/cf36c214f2a8)，标准的NFC实现

### 1. M1卡

<font color=red bold=true size = 13> 注意：0扇区0块不要轻易修改，会导致卡片无法读写，还有块4的控制码，不要锁死 </font>

#### 1.1 基本信息

#### 1.2 存取控制字节
https://blog.csdn.net/qq_39772670/article/details/119912468
https://blog.csdn.net/sierllen/article/details/83302009?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-0-83302009-blog-119912468.235^v28^pc_relevant_t0_download&spm=1001.2101.3001.4242.1&utm_relevant_index=3

KeyA在任何情况下都是不可读取的，只能暴力破解 
重置块时，需要校验写入权限   
个人格式化逻辑
~~~
1. 访问控制码是否可写入，不可抛出
2. 重置访问控制码，读出KeyB
3. 重置块0..3
~~~

07 8F 0F 69【冰冻，永远只可读不可写】  
00 F0 FF 69【自杀，永远不可读不可写】自杀模式还有很多，把控制字写的不认识也无法使用，在实际中新手经常干的事，所以一定要控制好。

#### 1.3 暴力破解M1
常见M1密钥，重放攻击
https://www.cnblogs.com/strengthen/p/16083846.html


####1.读取

####2.覆写

##### 2.1 写入WIFI信息
~~~kotlin
val mimeRecord = NdefRecord.createMime(
    NFC_TOKEN_MIME_TYPE, // value = application/vnd.wfa.wsc
    generatePayload(ssid, passwd, authType, ecyType) // value = {
        0x1045 to ssid // SSID_FIELD_ID, 名称
        0x1027 to passwd // NETWORK_KEY_ID, 密码
        0x1003 to authType // AUTH_TYPE_FIELD_ID, 安全性
        0x100f to ecyType // ENC_TYPE_FIELD_ID, 加密类型
     // }
)
~~~

~~~
I/System.out: UNKNOWN_ID: 4134
I/System.out: UNKNOWN_CONTENT: 01
I/System.out: SSID: 41 69 74 6D 65 64 2D 45 43 4F 53
I/System.out: SSID: Aitmed-ECOS
I/System.out: AUTH_TYPE: 34
I/System.out: UNKNOWN_ID: 4111 // 加密方式
I/System.out: UNKNOWN_CONTENT: 00 01
I/System.out: KEY: 61 69 74 6D 65 64 31 32 33
I/System.out: KEY: aitmed123
I/System.out: UNKNOWN_ID: 4128  // mac
I/System.out: UNKNOWN_CONTENT: FF FF FF FF FF FF
~~~


###2遇到的问题
####1.读到的action为空
~~~
//Q:intent.action==null
//A:https://blog.csdn.net/queal/article/details/126379781
//FLAG_IMMUTABLE表示intent不能除发送端以外的其他应用修改
~~~

####2.intent被其他应用响应
设定intentfilter和tech-list。如果两个都为null就代表优先接收任何形式的TAGaction。也就是说系统会主动发TAGintent。

####3.安装后桌面无图标
manifest.xml中添加action不能全部写在一个intentFilter中，一个action单独一个intentFilter
~~~xml
<intent-filter>
<actionandroid:name="android.nfc.action.NDEF_DISCOVERED"/>
<dataandroid:mimeType="*/*"/>

</intent-filter>
<intent-filter>
<actionandroid:name="android.nfc.action.TECH_DISCOVERED"/>
<dataandroid:mimeType="*/*"/>

</intent-filter>
<intent-filter>
<actionandroid:name="android.nfc.action.TAG_DISCOVERED"/>
<dataandroid:mimeType="*/*"/>

</intent-filter>
~~~


####4.java.io.IOException:Transceivefailed
检查blockIndex，这个在全局里是递增的，不是永远从0..3，而是sectorIndex*blockCount+i
具体到m1卡中，是sectorIndex*4+i,在读写时，sectorIndex要和blockIndex相匹配，<code>[sectorIndex*4,(sectorIndex+1)*4)</code>



###999ExtraContent
#####1.Android加密
-MD5不可逆加密
-AES对称加密
-RSA非对称加密

[浅谈android数据存储加密](https://blog.csdn.net/say_from_wen/article/details/77870849)

#####2.Android本地加密
可以使用设备<code>UUID</code>作为密钥，好处：即使算法被破解，没有对应的UUID也解不出正确内容

#####3.AndroidSP加密
对Key和Value都进行加密处理，文章中对Key进行AES加密，对Value进行RES加密，但公钥和私钥都存储在代码中，不可取
目前NoodlSdk用的是DataStorePreference，需要兼容以前的数据，或者，新版本直接放弃以前的数据

[AndroidSharedPreferences加密存储方法](https://blog.csdn.net/fitaotao/article/details/110649420)


