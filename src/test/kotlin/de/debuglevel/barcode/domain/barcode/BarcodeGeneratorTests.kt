package de.debuglevel.barcode.domain.barcode

import de.debuglevel.barcode.rest.greeting.BarcodeDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BarcodeGeneratorTests {
    @ParameterizedTest
    @MethodSource("validContentProvider")
    fun `generate valid barcodes`(testData: ContentTestData) {
        // Arrange

        // Act
        val barcodeByteArray = BarcodeGenerator.generate(BarcodeDTO(null, testData.value, CodeType.Code128), OutputFormat.SVG)

        //Assert
        assertThat(barcodeByteArray).isNotEmpty()
        assertThat(barcodeByteArray).isNotNull()
    }

    fun validContentProvider() = Stream.of(
            ContentTestData(value = "Mozart"),
            ContentTestData(value = "Amadeus"),
            ContentTestData(value = "Hänschen"),
            ContentTestData(value = "Max Mustermann")
    )

//    @ParameterizedTest
//    @MethodSource("invalidNameProvider")
//    fun `format invalid names`(testData: NameTestData) {
//        // Arrange
//
//        // Act
//
//        // Assert
//        assertThatExceptionOfType(BarcodeGenerator.GreetingException::class.java).isThrownBy({ BarcodeGenerator.greet(testData.value) })
//    }

//    fun invalidNameProvider() = Stream.of(
//            NameTestData(value = ""),
//            NameTestData(value = " ")
//    )

    data class ContentTestData(
            val value: String
    )
}