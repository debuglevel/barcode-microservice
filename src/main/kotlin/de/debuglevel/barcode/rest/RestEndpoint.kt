package de.debuglevel.barcode.rest

import de.debuglevel.barcode.rest.greeting.BarcodeController
import de.debuglevel.microservices.utils.apiversion.apiVersion
import de.debuglevel.microservices.utils.logging.buildRequestLog
import de.debuglevel.microservices.utils.logging.buildResponseLog
import de.debuglevel.microservices.utils.spark.configuredPort
import de.debuglevel.microservices.utils.status.status
import mu.KotlinLogging
import spark.Spark.path
import spark.kotlin.after
import spark.kotlin.before
import spark.kotlin.get
import spark.kotlin.post


/**
 * REST endpoint
 */
class RestEndpoint {
    private val logger = KotlinLogging.logger {}

    /**
     * Starts the REST endpoint to enter a listening state
     *
     * @param args parameters to be passed from main() command line
     */
    fun start(args: Array<String>) {
        logger.info("Starting...")
        configuredPort()
        status(this::class.java)

        // publish following paths on e.g. /v2/greetings/ and /greetings/ (as "default" is true)
        apiVersion("1", true)
        {
            path("/barcodes") {
                get("/", "application/json", BarcodeController.getList())
                post("/", function = BarcodeController.postOne())

                path("/:uuid") {
                    get("", "image/svg+xml", BarcodeController.getOneSvg())
                    //get("", "image/png", BarcodeController.getOnePng())
                    //get("", "application/json", BarcodeController.getOneJson())
                }
            }
        }

        // add loggers
        before { logger.debug(buildRequestLog(request)) }
        after { logger.debug(buildResponseLog(request, response)) }
    }
}
