package de.debuglevel.barcode.barcode

import de.debuglevel.barcode.Application
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.server.util.HttpHostResolver
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.web.router.RouteBuilder
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import java.util.*

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/barcodes")
@Tag(name = "barcodes")
class BarcodeController(
    private val barcodeService: BarcodeService,
    private val barcodeGenerator: BarcodeGenerator,
    private val httpHostResolver: HttpHostResolver,
    private val uriNamingStrategy: RouteBuilder.UriNamingStrategy,
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
            val barcodeByteArray = barcodeGenerator.generate(barcode, OutputPictureFormat.SVG)

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
            val barcodeByteArray = barcodeGenerator.generate(barcode, OutputPictureFormat.PNG)

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
    fun postOne(addBarcodeRequest: AddBarcodeRequest, httpRequest: HttpRequest<AddBarcodeRequest>): HttpResponse<*> {
        logger.debug("Called postOne($addBarcodeRequest)")

        return try {
            val barcode = addBarcodeRequest.toBarcode()
            val addedBarcode = barcodeService.add(barcode)

            val serverUrl = httpHostResolver.resolve(httpRequest)

            // get path of this Controller
            val applicationContext = Application.applicationContext
            val beanDefinition = applicationContext.getBeanDefinition(this::class.java)
            val annotationValue = beanDefinition.getAnnotation(Controller::class.java)!!
            val controllerPath = annotationValue.get("value", String::class.java).get()

            val selfUrl = "$serverUrl$controllerPath/${addedBarcode.id}"

            val addBarcodeResponse = AddBarcodeResponse(addedBarcode, selfUrl)
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