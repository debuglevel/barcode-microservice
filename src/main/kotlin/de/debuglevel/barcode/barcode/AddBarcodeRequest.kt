package de.debuglevel.barcode.barcode

data class AddBarcodeRequest(
    val content: String,
    val codeType: CodeType,
) {
    fun toBarcode(): Barcode {
        return Barcode(
            id = null,
            content = this.content,
            codeType = this.codeType,
        )
    }
}