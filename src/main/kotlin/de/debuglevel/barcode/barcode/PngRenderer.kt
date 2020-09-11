package de.debuglevel.barcode.barcode

import mu.KotlinLogging
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import uk.org.okapibarcode.backend.Symbol
import uk.org.okapibarcode.output.SvgRenderer
import uk.org.okapibarcode.output.SymbolRenderer
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.OutputStream


/**
 * Renders Symbols to SVG first and then converts them to PNG
 */
class PngRenderer
/**
 * Creates a new PNG renderer.
 *
 * @param outputStream the output stream to render to
 * @param magnification the magnification factor to apply
 * @param paper the paper (background) color
 * @param ink the ink (foreground) color
 */(
    /** The output stream to write the PNG picture into. */
    private val outputStream: OutputStream,
    /** The magnification factor to apply. */
    private val magnification: Double,
    /** The paper (background) color. */
    private val paper: Color,
    /** The ink (foreground) color. */
    private val ink: Color
) : SymbolRenderer {
    private val logger = KotlinLogging.logger {}

    override fun render(symbol: Symbol) {
        logger.debug { "Rendering PNG barcode..." }

        logger.debug { "Rendering intermediate SVG barcode..." }
        ByteArrayOutputStream().use { svgOutputStream ->
            val svgRenderer = SvgRenderer(svgOutputStream, magnification, paper, ink)
            svgRenderer.render(symbol)

            logger.debug { "Converting SVG to PNG..." }
            val pngTranscoder = PNGTranscoder()
            val pngTranscoderInput = TranscoderInput(svgOutputStream.toByteArray().inputStream())
            val pngTranscoderOutput = TranscoderOutput(outputStream)
            pngTranscoder.transcode(pngTranscoderInput, pngTranscoderOutput)
            outputStream.flush()
            outputStream.close()
        }

        logger.debug { "Rendered PNG barcode" }
    }
}