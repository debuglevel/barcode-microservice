package de.debuglevel.barcode.barcode

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import java.util.*

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/barcodes")
@Tag(name = "barcodes")
class BarcodeController(
    private val barcodeService: BarcodeService,
    private val barcodeGenerator: BarcodeGenerator,
) {
    private val logger = KotlinLogging.logger {}

    @Get("/{id}")
    fun getOneJson(id: UUID): HttpResponse<*> {
        logger.debug("Called getOne($id)")
        return try {
            val barcode = barcodeService.get(id)

            val getBarcodeResponse = GetBarcodeResponse(barcode)
            HttpResponse.ok(getBarcodeResponse)
        } catch (e: BarcodeService.EntityNotFoundException) {
            logger.debug { "Getting barcode $id failed: ${e.message}" }
            HttpResponse.notFound("Barcode $id does not exist.")
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError("Unhandled exception: ${e.message}")
        }
    }

    @Get("/{id}")
    @Produces("image/svg+xml")
    fun getOneSvg(id: UUID): HttpResponse<*> {
        logger.debug("Called getOne($id)")
        return try {
            val barcode = barcodeService.get(id)
            val barcodeByteArray = barcodeGenerator.generate(barcode, OutputFormat.SVG)

            HttpResponse.ok(barcodeByteArray)
        } catch (e: BarcodeService.EntityNotFoundException) {
            logger.debug { "Getting barcode $id failed: ${e.message}" }
            HttpResponse.notFound("Barcode $id does not exist.")
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError("Unhandled exception: ${e.message}")
        }
    }

    @Get("/{id}")
    @Produces("image/png")
    fun getOnePng(id: UUID): HttpResponse<*> {
        logger.debug("Called getOne($id)")
        return try {
            val barcode = barcodeService.get(id)
            val barcodeByteArray = barcodeGenerator.generate(barcode, OutputFormat.PNG)

            HttpResponse.ok(barcodeByteArray)
        } catch (e: BarcodeService.EntityNotFoundException) {
            logger.debug { "Getting barcode $id failed: ${e.message}" }
            HttpResponse.notFound("Barcode $id does not exist.")
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError("Unhandled exception: ${e.message}")
        }
    }

    @Post("/")
    fun postOne(addBarcodeRequest: AddBarcodeRequest): HttpResponse<*> {
        logger.debug("Called postOne($addBarcodeRequest)")

        return try {
            val barcode = addBarcodeRequest.toBarcode()
            val addedBarcode = barcodeService.add(barcode)

            val addBarcodeResponse = AddBarcodeResponse(addedBarcode)
            HttpResponse.created(addBarcodeResponse)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError("Unhandled exception: ${e.message}")
        }
    }

    @Delete("/{id}")
    fun deleteOne(id: UUID): HttpResponse<*> {
        logger.debug("Called deleteOne($id)")
        return try {
            barcodeService.delete(id)

            HttpResponse.noContent<Any>()
        } catch (e: BarcodeService.EntityNotFoundException) {
            HttpResponse.notFound("Barcode $id not found.")
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError("Unhandled exception: ${e.message}")
        }
    }

    @Delete("/")
    fun deleteAll(): HttpResponse<*> {
        logger.debug("Called deleteAll()")
        return try {
            barcodeService.deleteAll()

            HttpResponse.noContent<Any>()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError("Unhandled exception: ${e.message}")
        }
    }
}