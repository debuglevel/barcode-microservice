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

    fun generate(barcode: Barcode, outputPictureFormat: OutputPictureFormat): ByteArray {
        logger.debug { "Generating ${barcode.barcodeType} barcode in format '$outputPictureFormat' with content '${barcode.content}'..." }

        val symbol = getSymbol(barcode)
        symbol.content = barcode.content

        logger.debug { "Rendering ${barcode.barcodeType} barcode with content '${barcode.content}'..." }
        val outputStream = ByteArrayOutputStream()
        val renderer = getRenderer(outputPictureFormat, outputStream)
        renderer.render(symbol)

        logger.debug { "Generated ${barcode.barcodeType} barcode in format '$outputPictureFormat' with content '${barcode.content}'." }
        return outputStream.toByteArray()
    }

    private fun getSymbol(barcode: Barcode): Symbol {
        return when (barcode.barcodeType) {
            BarcodeType.Code128 -> Code128()
            BarcodeType.QrCode -> QrCode()
        }
    }

    private fun getRenderer(
        outputPictureFormat: OutputPictureFormat,
        outputStream: ByteArrayOutputStream
    ): SymbolRenderer {
        return when (outputPictureFormat) {
            OutputPictureFormat.SVG -> SvgRenderer(outputStream, 1.0, Color.WHITE, Color.BLACK)
            OutputPictureFormat.PNG -> PngRenderer(outputStream, 4.0, Color.WHITE, Color.BLACK)
        }
    }
}