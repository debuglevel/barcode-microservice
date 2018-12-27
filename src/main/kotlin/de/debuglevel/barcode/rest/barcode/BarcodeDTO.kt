package de.debuglevel.barcode.rest.barcode

import de.debuglevel.barcode.domain.barcode.CodeType

data class BarcodeDTO(var uuid: String?,
                      val content: String,
                      val codeType: CodeType)