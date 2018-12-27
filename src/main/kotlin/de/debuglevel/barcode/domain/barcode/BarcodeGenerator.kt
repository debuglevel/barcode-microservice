package de.debuglevel.barcode.domain.barcode

import de.debuglevel.barcode.rest.greeting.BarcodeDTO
import mu.KotlinLogging
import uk.org.okapibarcode.backend.*
import uk.org.okapibarcode.output.SvgRenderer
import uk.org.okapibarcode.output.SymbolRenderer
import java.awt.Color
import java.io.ByteArrayOutputStream

object BarcodeGenerator {
    private val logger = KotlinLogging.logger {}

    /**
     * @param content String which should be encoded in a barcode
     * @param codeType Type of code to generate
     */
    fun generate(barcode: BarcodeDTO, outputFormat: OutputFormat): ByteArray {
        logger.debug { "Generating ${barcode.codeType} barcode with content '${barcode.content}'..." }

        val symbol: Symbol = when (barcode.codeType) {
            CodeType.Code128 -> Code128()
            CodeType.Code32 -> Code32()
            CodeType.Code39 -> Code3Of9()
            CodeType.QrCode -> QrCode()

            else -> throw UnsupportedCodeTypeException(barcode.codeType)
        }
        symbol.content = barcode.content

        logger.debug { "Rendering ${barcode.codeType} barcode with content '${barcode.content}'..." }
        val outputStream = ByteArrayOutputStream()
        val renderer: SymbolRenderer = when (outputFormat) {
            OutputFormat.SVG -> SvgRenderer(outputStream, 1.0, Color.WHITE, Color.BLACK)
            else -> throw UnsupportedOutputFormat(outputFormat)
        }
        renderer.render(symbol)

        logger.debug { "Generated ${barcode.codeType} barcode with content '${barcode.content}'." }
        return outputStream.toByteArray()
    }

    class UnsupportedOutputFormat(outputFormat: OutputFormat) : Exception("Output format '$outputFormat' is not supported.")

    class UnsupportedCodeTypeException(codeType: CodeType) : Exception("Code of type '$codeType' is not supported.")
}