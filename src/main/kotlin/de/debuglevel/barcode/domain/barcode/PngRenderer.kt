package de.debuglevel.barcode.domain.barcode

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
 * @param out the output stream to render to
 * @param magnification the magnification factor to apply
 * @param paper the paper (background) color
 * @param ink the ink (foreground) color
 */(
        /** The output stream to render to.  */
        private val out: OutputStream,
        /** The magnification factor to apply.  */
        magnification: Double,
        /** The paper (background) color.  */
        paper: Color,
        /** The ink (foreground) color.  */
        ink: Color) : SymbolRenderer {

    private val logger = KotlinLogging.logger {}

    private val svgRenderer: SvgRenderer
    private val svgOutputStream = ByteArrayOutputStream()

    init {
        svgRenderer = SvgRenderer(svgOutputStream, magnification, paper, ink)
    }

    override fun render(symbol: Symbol) {
        logger.debug { "Rendering PNG barcode..." }
        logger.debug { "Rendering SVG barcode as first step..." }
        svgRenderer.render(symbol)

        logger.debug { "Converting SVG to PNG as second step..." }
        val pngTranscoder = PNGTranscoder()
        val transcoderInput = TranscoderInput(svgOutputStream.toByteArray().inputStream())
        val transcoderOutput = TranscoderOutput(out)
        pngTranscoder.transcode(transcoderInput, transcoderOutput)
        out.flush()
        out.close()

        logger.debug { "Rendering PNG barcode done." }
    }
}