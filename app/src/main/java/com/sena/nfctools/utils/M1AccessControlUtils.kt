package com.sena.nfctools.utils


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
    fun canRewriteDataBlock(blockIndex: Int, accessControlData: ByteArray): Boolean {
        val byte7 = accessControlData[0].toInt()
        val byte8 = accessControlData[1].toInt()
        val byte9 = accessControlData[2].toInt()

        val block_bit_0 = (byte7 shr (4 + blockIndex)) and 0x01
        val block_bit_1 = (byte8 shr (4 + blockIndex)) and 0x01
        val block_bit_2 = (byte9 shr (4 + blockIndex)) and 0x01
        val block_access_code = (block_bit_0 shl 2) or (block_bit_1 shl 1) or block_bit_2

        return 0B000 == block_access_code || 0B100 == block_access_code ||
                0B110 == block_access_code || 0B011 == block_access_code
    }

    fun canReadWriteDataBlock(blockIndex: Int, byte7: Byte, byte6: Byte, byte5: Byte): Boolean = canRewriteDataBlock(
        blockIndex, byteArrayOf(byte7, byte6, byte5)
    )

    fun canRewriteAccessCode(accessControlData: ByteArray): Boolean {
        val byte7 = accessControlData[0].toInt()
        val byte8 = accessControlData[1].toInt()
        val byte9 = accessControlData[2].toInt()

        val block_bit_0 = (byte7 shr 7) and 0x01
        val block_bit_1 = (byte8 shr 7) and 0x01
        val block_bit_2 = (byte9 shr 7) and 0x01
        val block_access_code = (block_bit_0 shl 2) or (block_bit_1 shl 1) or block_bit_2

        return 0B001 == block_access_code || 0B011 == block_access_code || 0B101 == block_access_code
    }

    /**
     * 在写入操作上，KeyA和KeyB是一致的
     */
    fun canRewriteKey(accessControlData: ByteArray): Boolean {
        return canRewriteKeyA(accessControlData)
    }

    fun canRewriteKeyA(accessControlData: ByteArray): Boolean {
        val byte7 = accessControlData[0].toInt()
        val byte8 = accessControlData[1].toInt()
        val byte9 = accessControlData[2].toInt()

        val block_bit_0 = (byte7 shr 7) and 0x01
        val block_bit_1 = (byte8 shr 7) and 0x01
        val block_bit_2 = (byte9 shr 7) and 0x01
        val block_access_code = (block_bit_0 shl 2) or (block_bit_1 shl 1) or block_bit_2

        return 0B000 == block_access_code || 0B100 == block_access_code ||
                0B001 == block_access_code || 0B011 == block_access_code
    }

    fun canRewriteKeyB(accessControlData: ByteArray): Boolean {
        val byte7 = accessControlData[0].toInt()
        val byte8 = accessControlData[1].toInt()
        val byte9 = accessControlData[2].toInt()

        val block_bit_0 = (byte7 shr 7) and 0x01
        val block_bit_1 = (byte8 shr 7) and 0x01
        val block_bit_2 = (byte9 shr 7) and 0x01
        val block_access_code = (block_bit_0 shl 2) or (block_bit_1 shl 1) or block_bit_2

        return 0B000 == block_access_code || 0B100 == block_access_code ||
                0B001 == block_access_code || 0B011 == block_access_code
    }


    fun parseAccessControl(bIndex: Int, controlData: ByteArray) {
        val byte6 = controlData[0].toInt()
        val byte7 = controlData[1].toInt()
        val byte8 = controlData[2].toInt()

        val block3_c10 = if ((byte6 shr 7) and 0x01 == 0) 1 else 0
        val block3_c20 = if ((byte7 shr 7) and 0x01 == 0) 1 else 0
        val block3_c30 = if ((byte8 shr 7) and 0x01 == 0) 1 else 0
        val block3_access_control = (block3_c10 shl 2) or (block3_c20 shl 1) or block3_c30

        val block2_c10 = (byte6 shr 6) and 0x01
        val block2_c20 = (byte7 shr 6) and 0x01
        val block2_c30 = (byte8 shr 6) and 0x01
        val block2_access_control = (block2_c10 shl 2) or (block2_c20 shl 1) or block3_c30

        val block1_c10 = (byte6 shr 5) and 0x01
        val block1_c20 = (byte7 shr 5) and 0x01
        val block1_c30 = (byte8 shr 5) and 0x01
        val block1_access_control = (block1_c10 shl 2) or (block1_c20 shl 1) or block1_c30

        val block0_c10 = (byte6 shr 4) and 0x01
        val block0_c20 = (byte7 shr 4) and 0x01
        val block0_c30 = (byte8 shr 4) and 0x01
        val block0_access_control = (block0_c10 shl 2) or (block0_c20 shl 1) or block0_c30

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

