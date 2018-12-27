package de.debuglevel.barcode.rest.greeting

import de.debuglevel.barcode.domain.barcode.CodeType

data class BarcodeDTO(var uuid: String?,
                      val content: String,
                      val codeType: CodeType)