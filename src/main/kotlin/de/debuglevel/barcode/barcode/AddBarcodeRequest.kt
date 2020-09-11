package de.debuglevel.barcode.barcode

data class AddBarcodeRequest(
    val content: String,
    val codeType: BarcodeType,
) {
    fun toBarcode(): Barcode {
        return Barcode(
            id = null,
            content = this.content,
            barcodeType = this.codeType,
        )
    }
}