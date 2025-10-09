/**
 * Copyright (c) 2025 SaltifyDev
 *
 * This file is part of [qrcode-kotlin](https://github.com/g0dkar/qrcode-kotlin/)
 * and is licensed under the MIT License.
 * See the MIT-LICENSE file in the module root (/yogurt-qrcode) for license information.
 */

package org.ntqqrev.yogurt.qrcode

import org.ntqqrev.yogurt.qrcode.QRCodeSetup.applyMaskPattern
import org.ntqqrev.yogurt.qrcode.QRCodeSetup.setupBottomLeftPositionProbePattern
import org.ntqqrev.yogurt.qrcode.QRCodeSetup.setupPositionAdjustPattern
import org.ntqqrev.yogurt.qrcode.QRCodeSetup.setupTimingPattern
import org.ntqqrev.yogurt.qrcode.QRCodeSetup.setupTopLeftPositionProbePattern
import org.ntqqrev.yogurt.qrcode.QRCodeSetup.setupTopRightPositionProbePattern
import org.ntqqrev.yogurt.qrcode.QRCodeSetup.setupTypeInfo
import org.ntqqrev.yogurt.qrcode.QRCodeSetup.setupTypeNumber

typealias QRCodeRawData = Array<Array<QRCodeSquare>>

/**
 * A Class/Library that helps encode data as QR Code images without any external dependencies.
 *
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/QRCode.java).
 *
 * @param data String that will be encoded in the QR Code.
 * @param errorCorrectionLevel The level of Error Correction that should be applied to the QR Code. Defaults to [ErrorCorrectionLevel.MEDIUM].
 * @param dataType One of the available [QRCodeDataType]. By default, the code tries to guess which one is the best fitting one from your input data.
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 *
 * @see ErrorCorrectionLevel
 * @see QRUtil.getDataType
 */
@Suppress("NON_EXPORTABLE_TYPE", "MemberVisibilityCanBePrivate")
class QRCodeProcessor(
    private val data: String,
    private val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.MEDIUM,
    private val dataType: QRCodeDataType = QRUtil.getDataType(data),
) {
    private val qrCodeData: QRData = when (dataType) {
        QRCodeDataType.NUMBERS -> QRNumber(data)
        QRCodeDataType.UPPER_ALPHA_NUM -> QRAlphaNum(data)
        QRCodeDataType.DEFAULT -> QR8BitByte(data)
    }

    companion object {
        const val DEFAULT_CELL_SIZE = 25
        private const val PAD0 = 0xEC
        private const val PAD1 = 0x11
        const val MAXIMUM_INFO_DENSITY = 40

        /**
         * Infer what is the least amount of the informationDensity parameter to fit the specified [data]
         * at the given [errorCorrectionLevel].
         *
         * If it cannot determine the value, the maximum value for it will be returned: `40`.
         *
         * @see MAXIMUM_INFO_DENSITY
         */
        fun infoDensityForDataAndECL(
            data: String,
            errorCorrectionLevel: ErrorCorrectionLevel,
            dataType: QRCodeDataType = QRUtil.getDataType(data),
        ): Int {
            val qrCodeData = when (dataType) {
                QRCodeDataType.NUMBERS -> QRNumber(data)
                QRCodeDataType.UPPER_ALPHA_NUM -> QRAlphaNum(data)
                QRCodeDataType.DEFAULT -> QR8BitByte(data)
            }
            val dataLength = qrCodeData.length()

            for (typeNum in 1 until errorCorrectionLevel.maxTypeNum) {
                if (dataLength <= QRUtil.getMaxLength(typeNum, dataType, errorCorrectionLevel)) {
                    return typeNum
                }
            }

            return MAXIMUM_INFO_DENSITY
        }
    }

    /**
     * Computes and encodes the [data] of this object into a QR Code. This method returns the raw data of the QR Code.
     *
     * @param type `type` value for the QRCode computation. Between 0 and 40. Read more about it [here][ErrorCorrectionLevel].
     * Defaults to an [automatically calculated value][infoDensityForDataAndECL] based on [data] and the [errorCorrectionLevel].
     * @param maskPattern Mask Pattern to apply to the final QR Code. Basically changes how the QR Code looks at the end.
     * Read more about it [here][MaskPattern]. Defaults to [MaskPattern.PATTERN000].
     *
     * @return The byte matrix of the encoded QRCode.
     *
     * @see infoDensityForDataAndECL
     * @see ErrorCorrectionLevel
     * @see MaskPattern
     */
    fun encode(
        type: Int = infoDensityForDataAndECL(data, errorCorrectionLevel),
        maskPattern: MaskPattern = MaskPattern.PATTERN000,
    ): QRCodeRawData {
        val moduleCount = type * 4 + 17
        val modules: Array<Array<QRCodeSquare?>> =
            Array(moduleCount) { Array(moduleCount) { null } }

        setupTopLeftPositionProbePattern(modules = modules)
        setupTopRightPositionProbePattern(modules)
        setupBottomLeftPositionProbePattern(modules)

        setupPositionAdjustPattern(type, modules)
        setupTimingPattern(moduleCount, modules)
        setupTypeInfo(errorCorrectionLevel, maskPattern, moduleCount, modules)

        if (type >= 7) {
            setupTypeNumber(type, moduleCount, modules)
        }

        val data = createData(type)

        applyMaskPattern(data, maskPattern, moduleCount, modules)

        return Array(moduleCount) { row ->
            Array(moduleCount) { column ->
                modules[row][column] ?: QRCodeSquare(false, row, column, moduleCount)
            }
        }
    }

    private fun createData(type: Int): IntArray {
        val rsBlocks = RSBlock.getRSBlocks(type, errorCorrectionLevel)
        val buffer = BitBuffer()

        buffer.put(qrCodeData.dataType.value, 4)
        buffer.put(qrCodeData.length(), qrCodeData.getLengthInBits(type))
        qrCodeData.write(buffer)

        val totalDataCount = rsBlocks.sumOf { it.dataCount } * 8

        if (buffer.lengthInBits > totalDataCount) {
            val errorMessage =
                "Insufficient Information Density Parameter: $type [neededBits=${buffer.lengthInBits}, maximumBitsForDensityLevel=$totalDataCount] - Try increasing the Information Density parameter value or use 0 (zero) to automatically compute the least amount needed to fit the QRCode data being encoded."
            throw IllegalArgumentException(errorMessage)
        }

        if (buffer.lengthInBits + 4 <= totalDataCount) {
            buffer.put(0, 4)
        }

        while (buffer.lengthInBits % 8 != 0) {
            buffer.put(false)
        }

        while (true) {
            if (buffer.lengthInBits >= totalDataCount) {
                break
            }

            buffer.put(PAD0, 8)

            if (buffer.lengthInBits >= totalDataCount) {
                break
            }

            buffer.put(PAD1, 8)
        }

        return createBytes(buffer, rsBlocks)
    }

    private fun createBytes(buffer: BitBuffer, rsBlocks: Array<RSBlock>): IntArray {
        var offset = 0
        var maxDcCount = 0
        var maxEcCount = 0
        var totalCodeCount = 0
        val dcData = Array(rsBlocks.size) { IntArray(0) }
        val ecData = Array(rsBlocks.size) { IntArray(0) }

        rsBlocks.forEachIndexed { i, it ->
            val dcCount = it.dataCount
            val ecCount = it.totalCount - dcCount

            totalCodeCount += it.totalCount
            maxDcCount = maxDcCount.coerceAtLeast(dcCount)
            maxEcCount = maxEcCount.coerceAtLeast(ecCount)

            // Init dcData[i]
            dcData[i] = IntArray(dcCount) { idx -> 0xff and buffer.buffer[idx + offset] }
            offset += dcCount

            // Init ecData[i]
            val rsPoly = QRUtil.getErrorCorrectPolynomial(ecCount)
            val rawPoly = Polynomial(dcData[i], rsPoly.len() - 1)
            val modPoly = rawPoly.mod(rsPoly)
            val ecDataSize = rsPoly.len() - 1

            ecData[i] = IntArray(ecDataSize) { idx ->
                val modIndex = idx + modPoly.len() - ecDataSize
                if ((modIndex >= 0)) modPoly[modIndex] else 0
            }
        }

        var index = 0
        val data = IntArray(totalCodeCount)

        for (i in 0 until maxDcCount) {
            for (r in rsBlocks.indices) {
                if (i < dcData[r].size) {
                    data[index++] = dcData[r][i]
                }
            }
        }

        for (i in 0 until maxEcCount) {
            for (r in rsBlocks.indices) {
                if (i < ecData[r].size) {
                    data[index++] = ecData[r][i]
                }
            }
        }

        return data
    }

    override fun toString(): String =
        "QRCode(data=$data" +
                ", errorCorrectionLevel=$errorCorrectionLevel" +
                ", dataType=$dataType" +
                ", qrCodeData=${qrCodeData::class.simpleName}" +
                ")"
}