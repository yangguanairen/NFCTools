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

