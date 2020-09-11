package de.debuglevel.barcode.barcode

import mu.KotlinLogging
import java.util.*
import javax.inject.Singleton

@Singleton
class BarcodeService(
    private val barcodeRepository: BarcodeRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun get(id: UUID): Barcode {
        logger.debug { "Getting barcode with ID '$id'..." }

        val barcode: Barcode = barcodeRepository.findById(id).orElseThrow {
            logger.debug { "Getting barcode with ID '$id' failed" }
            EntityNotFoundException(id)
        }

        logger.debug { "Got barcode with ID '$id': $barcode" }
        return barcode
    }

    fun add(barcode: Barcode): Barcode {
        logger.debug { "Adding barcode '$barcode'..." }

        val savedBarcode = barcodeRepository.save(barcode)

        logger.debug { "Added barcode: $savedBarcode" }
        return savedBarcode
    }

//    fun update(id: UUID, barcode: Barcode): Barcode {
//        logger.debug { "Updating barcode '$barcode' with ID '$id'..." }
//
//        // an object must be known to Hibernate (i.e. retrieved first) to get updated;
//        // it would be a "detached entity" otherwise.
//        val updateBarcode = this.get(id).apply {
//            name = barcode.name
//        }
//
//        val updatedBarcode = barcodeRepository.update(updateBarcode)
//
//        logger.debug { "Updated barcode: $updatedBarcode with ID '$id'" }
//        return updatedBarcode
//    }

    fun list(): Set<Barcode> {
        logger.debug { "Getting all barcodes ..." }

        val barcodes = barcodeRepository.findAll().toSet()

        logger.debug { "Got all barcodes" }
        return barcodes
    }

    fun delete(id: UUID) {
        logger.debug { "Deleting barcode with ID '$id'..." }

        if (barcodeRepository.existsById(id)) {
            barcodeRepository.deleteById(id)
        } else {
            throw EntityNotFoundException(id)
        }

        logger.debug { "Deleted barcode with ID '$id'" }
    }

    fun deleteAll() {
        logger.debug { "Deleting all merges..." }

        val countBefore = barcodeRepository.count()
        barcodeRepository.deleteAll()
        val countAfter = barcodeRepository.count()

        logger.debug { "Deleted ${countBefore - countAfter} of $countBefore barcodes, $countAfter remaining" }
    }

    class EntityNotFoundException(criteria: Any) : Exception("Entity '$criteria' does not exist.")
}