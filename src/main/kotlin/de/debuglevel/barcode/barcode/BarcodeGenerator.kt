package de.debuglevel.barcode.barcode

import mu.KotlinLogging
import uk.org.okapibarcode.backend.*
import uk.org.okapibarcode.output.SvgRenderer
import java.awt.Color
import java.io.ByteArrayOutputStream
import javax.inject.Singleton

@Singleton
class BarcodeGenerator {
    private val logger = KotlinLogging.logger {}

    fun generate(barcode: Barcode, outputFormat: OutputFormat): ByteArray {
        logger.debug { "Generating ${barcode.codeType} barcode with content '${barcode.content}'..." }

        val symbol = getSymbol(barcode)
        symbol.content = barcode.content

        logger.debug { "Rendering ${barcode.codeType} barcode with content '${barcode.content}'..." }
        val outputStream = ByteArrayOutputStream()
        val renderer = getRenderer(outputFormat, outputStream)
        renderer.render(symbol)

        logger.debug { "Generated ${barcode.codeType} barcode with content '${barcode.content}'." }
        return outputStream.toByteArray()
    }

    private fun getSymbol(barcode: Barcode): Symbol {
        return when (barcode.codeType) {
            CodeType.Code128 -> Code128()
            CodeType.Code32 -> Code32()
            CodeType.Code39 -> Code3Of9()
            CodeType.QrCode -> QrCode()
            else -> throw UnsupportedCodeTypeException(barcode.codeType)
        }
    }

    private fun getRenderer(
        outputFormat: OutputFormat,
        outputStream: ByteArrayOutputStream
    ): SvgRenderer {
        return when (outputFormat) {
            OutputFormat.SVG -> SvgRenderer(outputStream, 1.0, Color.WHITE, Color.BLACK)
            else -> throw UnsupportedOutputFormat(outputFormat)
        }
    }

    class UnsupportedOutputFormat(outputFormat: OutputFormat) :
        Exception("Output format '$outputFormat' is not supported.")

    class UnsupportedCodeTypeException(codeType: CodeType) : Exception("Code of type '$codeType' is not supported.")
}