package de.debuglevel.barcode.barcode

import java.util.*

data class AddBarcodeResponse(
    val id: UUID,
    val content: String,
    val codeType: CodeType,
    val selfUrl: String,
) {
    constructor(barcode: Barcode, url: String) : this(
        id = barcode.id!!,
        content = barcode.content,
        codeType = barcode.codeType,
        selfUrl = url
    )
}