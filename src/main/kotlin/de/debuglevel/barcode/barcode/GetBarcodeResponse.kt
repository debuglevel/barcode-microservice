package de.debuglevel.barcode.barcode

import java.util.*

data class GetBarcodeResponse(
    val id: UUID,
    val content: String,
    val codeType: CodeType
) {
    constructor(barcode: Barcode) : this(
        id = barcode.id!!,
        content = barcode.content,
        codeType = barcode.codeType,
    )
}