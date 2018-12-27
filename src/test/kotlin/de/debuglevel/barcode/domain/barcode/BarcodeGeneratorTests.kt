package de.debuglevel.barcode.domain.barcode

import de.debuglevel.barcode.rest.greeting.BarcodeDTO
import mu.KotlinLogging
import org.apache.tika.config.TikaConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.toList


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BarcodeGeneratorTests {
    private val logger = KotlinLogging.logger {}

    @ParameterizedTest
    @MethodSource("validContentAndCodetypeAndOutputformatCombinationsProvider")
    fun `generate valid barcodes (permutate content, codetype and outputformat)`(testData: ContentAndCodetypeAndOutputformatTestData) {
        logger.debug { "Test data: $testData" }
        // Arrange

        // Act
        val barcodeByteArray = BarcodeGenerator.generate(BarcodeDTO(null, testData.content, testData.codeType), testData.outputformatTestdata.outputFormat)

        //Assert
        val mimetype = TikaConfig().detector.detect(barcodeByteArray.inputStream(), org.apache.tika.metadata.Metadata())
        assertThat(mimetype.toString()).isEqualTo(testData.outputformatTestdata.mimetype)
    }

    fun validContentProvider() = Stream.of(
            ContentTestData(value = "Mozart"),
            ContentTestData(value = "Amadeus"),
            ContentTestData(value = "Hänschen"),
            ContentTestData(value = "http://www.google.de"),
            ContentTestData(value = "Awesome code!?"),
            ContentTestData(value = "Max Mustermann")
    )

    fun validOutputformatProvider() = Stream.of(
            OutputformatTestData(outputFormat = OutputFormat.SVG, mimetype = "image/svg+xml"),
            OutputformatTestData(outputFormat = OutputFormat.PNG, mimetype = "image/png")
    )

    fun validContentAndCodetypeAndOutputformatCombinationsProvider(): Stream<ContentAndCodetypeAndOutputformatTestData> {
        val contents = validContentProvider().toList()
        val codeTypes = CodeType.values()
        val outputformatTestdata = validOutputformatProvider().toList()

        val permutations: MutableSet<ContentAndCodetypeAndOutputformatTestData> = mutableSetOf()
        contents.forEach { contentTestData ->
            codeTypes.forEach { codetype ->
                outputformatTestdata.forEach { outputFormat ->
                    permutations.add(ContentAndCodetypeAndOutputformatTestData(contentTestData.value, codetype, outputFormat))
                }
            }
        }

        return permutations.stream()
    }

    data class ContentTestData(
            val value: String
    )

    data class OutputformatTestData(
            val outputFormat: OutputFormat,
            val mimetype: String
    )

    data class ContentAndCodetypeAndOutputformatTestData(
            val content: String,
            val codeType: CodeType,
            val outputformatTestdata: OutputformatTestData
    )
}