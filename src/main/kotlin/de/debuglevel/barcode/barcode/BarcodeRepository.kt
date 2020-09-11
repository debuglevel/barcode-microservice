package de.debuglevel.barcode.barcode

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface BarcodeRepository : CrudRepository<Barcode, UUID>