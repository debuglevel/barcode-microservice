package de.debuglevel.barcode.barcode

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Barcode(
    @Id
    @GeneratedValue
    var id: UUID?,
    val content: String,
    val barcodeType: BarcodeType
)