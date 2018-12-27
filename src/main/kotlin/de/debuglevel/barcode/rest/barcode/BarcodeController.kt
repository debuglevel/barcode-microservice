package de.debuglevel.barcode.rest.barcode

import com.google.gson.Gson
import de.debuglevel.barcode.domain.barcode.BarcodeGenerator
import de.debuglevel.barcode.domain.barcode.OutputFormat
import de.debuglevel.barcode.rest.responsetransformer.JsonTransformer
import mu.KotlinLogging
import spark.kotlin.RouteHandler
import java.util.*

object BarcodeController {
    private val logger = KotlinLogging.logger {}

    private val barcodes = hashMapOf<String, BarcodeDTO>()

    fun getOneSvg(): RouteHandler.() -> Any {
        return {
            val uuid = params(":uuid")
            val barcode = barcodes.getOrElse(uuid) { throw BarcodeNotFoundException(uuid) }

            try {
                val barcodeByteArray = BarcodeGenerator.generate(barcode, OutputFormat.SVG)

                type(contentType = "image/svg+xml")
                barcodeByteArray
            } catch (e: Exception) {
                logger.info("Barcode could not be generated: ", e.message)
                response.type("application/json")
                response.status(400)
                "{\"message\":\"barcode '$uuid' could not be generated: ${e.message}\"}"
            }
        }
    }

    fun getOnePng(): RouteHandler.() -> Any {
        return {
            val uuid = params(":uuid")
            val barcode = barcodes.getOrElse(uuid) { throw BarcodeNotFoundException(uuid) }

            try {
                val barcodeByteArray = BarcodeGenerator.generate(barcode, OutputFormat.PNG)

                type(contentType = "image/png")
                barcodeByteArray
            } catch (e: Exception) {
                logger.info("Barcode could not be generated: ", e.message)
                response.type("application/json")
                response.status(400)
                "{\"message\":\"barcode '$uuid' could not be generated: ${e.message}\"}"
            }
        }
    }

    fun getList(): RouteHandler.() -> String {
        return {
            type(contentType = "application/json")
            JsonTransformer.render(barcodes.values)
        }
    }

    fun postOne(): RouteHandler.() -> String {
        return {
            // TODO: Gson can create objects with null, even if it's nullable in Kotlin. Reflection magic.
            // TODO: this can furthermore create a null codeType if the upper/lower-case of the enum does not fit.
            val barcode = Gson().fromJson(request.body(), BarcodeDTO::class.java)
            val uuid = UUID.randomUUID().toString()
            barcode.uuid = uuid

            logger.debug { "Storing barcode description '$barcode'..." }
            barcodes[uuid] = barcode

            type(contentType = "application/json")
            JsonTransformer.render(barcode)
        }
    }

    class BarcodeNotFoundException(uuid: String) : Exception("Barcode with UUID '$uuid' not found.")
}