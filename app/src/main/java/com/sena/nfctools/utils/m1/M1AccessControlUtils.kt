package com.sena.nfctools.utils.m1


/**
 * FileName: M1AccessControlUtils
 * Author: JiaoCan
 * Date: 2023/4/11 14:44
 */

object M1AccessControlUtils {

    /**
     * @param blockIndex 0..2
     * @param accessControlData size = 4
     * 默认是使用KeyB进行写入，KeyA能写入的地方，KeyB就可以，反之不行
     */
    fun canRewriteDataBlock(blockIndex: Int, byte6: Byte, byte7: Byte, byte8: Byte): Boolean {
        val code = getControlCode(blockIndex, byte6, byte7, byte8)

        return 0B000 == code || 0B100 == code ||
                0B110 == code || 0B011 == code
    }

    fun canRewriteControlCodeByKeyB(byte6: Byte, byte7: Byte, byte8: Byte): Boolean {
        val code = getControlCode(3, byte6, byte7, byte8)

        return 0B001 == code || 0B011 == code || 0B101 == code
    }

    fun canRewriteControlCodeByKeyA(byte6: Byte, byte7: Byte, byte8: Byte): Boolean {
        val code = getControlCode(3, byte6, byte7, byte8)

        return 0B001 == code
    }

    private fun getControlCode(blockIndex: Int, byte6: Byte, byte7: Byte, byte8: Byte): Int {
        val int6 = byte6.toInt()
        val int7 = byte7.toInt()
        val int8 = byte8.toInt()

        val bit_0 = ((int6 shr (4 + blockIndex)) and 0x01).inv() and 0x01  // 字节6的每一位取反
        val bit_1 = (int7 shr (4 + blockIndex)) and 0x01
        val bit_2 = (int8 shr (4 + blockIndex)) and 0x01

        return (bit_0 shl 2) or (bit_1 shl 1) or bit_2
    }

    fun canReadDataBlockByKeyA(blockIndex: Int, byte6: Byte, byte7: Byte, byte8: Byte): Boolean {
        val code = getControlCode(blockIndex, byte6, byte7, byte8)

        return 0B000 == code || 0B010 == code || 0B100 == code ||
                0B110 == code || 0B001 == code
    }

    fun canReadDataBlockByKeyB(blockIndex: Int, byte6: Byte, byte7: Byte, byte8: Byte): Boolean {
        val code = getControlCode(blockIndex, byte6, byte7, byte8)

        return 0B000 == code || 0B010 == code || 0B100 == code ||
                0B110 == code || 0B001 == code || 0B011 == code ||
                0B101 == code
    }

    fun canReadKeyBByKeyA(byte6: Byte, byte7: Byte, byte8: Byte): Boolean {
        val code = getControlCode(3, byte6, byte7, byte8)

        return 0B000 == code || 0B010 == code || 0B001 == code
    }


    fun parseAccessControl(bIndex: Int, byte6: Byte, byte7: Byte, byte8: Byte) {

        val block3_access_control = getControlCode(3, byte6, byte7, byte8)
        val block2_access_control = getControlCode(2, byte6, byte7, byte8)
        val block1_access_control = getControlCode(1, byte6, byte7, byte8)
        val block0_access_control = getControlCode(0, byte6, byte7, byte8)

        println("块号: $bIndex, ${printDataBlockAccessControlInfo(block0_access_control)}")
        println("块号: ${bIndex + 1}, ${printDataBlockAccessControlInfo(block1_access_control)}")
        println("块号: ${bIndex + 2}, ${printDataBlockAccessControlInfo(block2_access_control)}")
        println("块号: ${bIndex + 3}, ${printControlBlockAccessControlInfo(block3_access_control)}")
    }

    private fun printDataBlockAccessControlInfo(controlCode: Int): String {
        return when (controlCode) {
            0b000 -> {
                "Read: KeyA/KeyB\nWrite: KeyA/KeyB\nIncrement: KeyA/KeyB\nDecrement: KeyA/KeyB"
            }
            0b010 -> {
                "Read: KeyA/KeyB\nWrite: Never\nIncrement: Never\nDecrement: Never"
            }
            0b100 -> {
                "Read: KeyA/KeyB\nWrite: KeyB\nIncrement: Never\nDecrement: Never"
            }
            0b110 -> {
                "Read: KeyA/KeyB\nWrite: KeyA/KeyB\nIncrement: KeyB\nDecrement: KeyA/KeyB"
            }
            0b001 -> {
                "Read: KeyA/KeyB\nWrite: Never\nIncrement: Never\nDecrement: KeyA/KeyB"
            }
            0b011 -> {
                "Read: KeyB\nWrite: KeyB\nIncrement: Never\nDecrement: Never"
            }
            0b101 -> {
                "Read: KeyB\nWrite: Never\nIncrement: Never\nDecrement: Never"
            }
            0B111 -> {
                "Read: Never\nWrite: Never\nIncrement: Never\nDecrement: Never"
            }
            else -> ""
        }
    }

    fun printControlBlockAccessControlInfo(controlCode: Int): String {
        val format = "读取密码A: %8s, 写入密码A: %8s\n" +
                "读取控制块: %8s, 写入控制块: %8s\n" +
                "读取密码B: %8s, 写入密码B: %8s"

        return when (controlCode) {
            0B000 -> {
                String.format(
                    format,
                    "Never", "KeyA|B",
                    "KeyA|B", "Never",
                    "KeyA|B", "KeyA|B"
                )
            }
            0B010 -> {
                String.format(
                    format,
                    "Never", "Never",
                    "KeyA|B", "Never",
                    "KeyA|B", "Never"
                )
            }
            0B100 -> {
                String.format(
                    format,
                    "Never", "KeyB",
                    "KeyA|B", "Never",
                    "Never", "KeyB"
                )
            }
            0B110 -> {
                String.format(
                    format,
                    "Never", "Never",
                    "KeyA|B", "Never",
                    "Never", "Never"
                )
            }
            0B001 -> {
                String.format(
                    format,
                    "Never", "KeyA|B",
                    "KeyA|B", "KeyA|B",
                    "KeyA|B", "KeyA|B"
                )
            }
            0B011 -> {
                String.format(
                    format,
                    "Never", "KeyB",
                    "KeyA|B", "KeyB",
                    "Never", "KeyB"
                )
            }
            0B101 -> {
                String.format(
                    format,
                    "Never", "Never",
                    "KeyA|B", "KeyB",
                    "Never", "Never"
                )
            }
            0B111 -> {
                String.format(
                    format,
                    "Never", "Never",
                    "KeyA|B", "Never",
                    "Never", "Never"
                )
            }

            else -> ""
        }
    }


}

