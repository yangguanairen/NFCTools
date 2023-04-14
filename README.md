# TODO
- 1. 存储改为cache存储，每个卡片单独一个无后缀txt文件，内存数据为Gson，避免sp的全量更新
- 2. wifi信息的写入和解析
- 3. 


# NFCTools

TagData



~~~
文件结构
TagData
  ++ NdefData
  ++ NfcAData
       ++ M1ClassicData
       ++ M1UltralightData
~~~

~~~
读取流程
NfcUtils.read
  ++ NdefUtils.read
  ++ readNfcA
       ++ M1ClassicUtils.read
~~~

~~~
格式化流程
NfcUtils.format
  ++ M1ClassicUtils.format
~~~

~~~
写入流程
NfcUtils.write
  ++ NdefUtils.write
~~~

