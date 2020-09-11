package de.debuglevel.barcode.barcode

import mu.KotlinLogging
import uk.org.okapibarcode.backend.Code128
import uk.org.okapibarcode.backend.QrCode
import uk.org.okapibarcode.backend.Symbol
import uk.org.okapibarcode.output.SvgRenderer
import uk.org.okapibarcode.output.SymbolRenderer
import java.awt.Color
import java.io.ByteArrayOutputStream
import javax.inject.Singleton

@Singleton
class BarcodeGenerator {
    private val logger = KotlinLogging.logger {}

    fun generate(barcode: Barcode, outputFormat: OutputFormat): ByteArray {
        logger.debug { "Generating ${barcode.codeType} barcode in format '$outputFormat' with content '${barcode.content}'..." }

        val symbol = getSymbol(barcode)
        symbol.content = barcode.content

        logger.debug { "Rendering ${barcode.codeType} barcode with content '${barcode.content}'..." }
        val outputStream = ByteArrayOutputStream()
        val renderer = getRenderer(outputFormat, outputStream)
        renderer.render(symbol)

        logger.debug { "Generated ${barcode.codeType} barcode in format '$outputFormat' with content '${barcode.content}'." }
        return outputStream.toByteArray()
    }

    private fun getSymbol(barcode: Barcode): Symbol {
        return when (barcode.codeType) {
            CodeType.Code128 -> Code128()
            CodeType.QrCode -> QrCode()
        }
    }

    private fun getRenderer(
        outputFormat: OutputFormat,
        outputStream: ByteArrayOutputStream
    ): SymbolRenderer {
        return when (outputFormat) {
            OutputFormat.SVG -> SvgRenderer(outputStream, 1.0, Color.WHITE, Color.BLACK)
            OutputFormat.PNG -> PngRenderer(outputStream, 4.0, Color.WHITE, Color.BLACK)
        }
    }
}