package de.debuglevel.barcode.rest

import com.google.gson.Gson
import de.debuglevel.barcode.domain.barcode.CodeType
import de.debuglevel.barcode.rest.greeting.BarcodeDTO
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import spark.Spark.awaitInitialization

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestEndpointTests {
    private val logger = KotlinLogging.logger {}

    init {
        val restEndpoint = RestEndpoint()
        restEndpoint.start(arrayOf())

        awaitInitialization()
    }

    @Test
    fun `server listens on default port`() {
        // Arrange

        // Act
        val response = ApiTestUtils.request("GET", "/barcodes/", null)

        // Assert
        // HTTP Codes begin from "100". So something from 100 and above was probably a response to a HTTP request
        assertThat(response?.status).isGreaterThanOrEqualTo(100)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class `valid requests on barcode` {
        @Test
        fun `server lists barcodes`() {
            // Arrange
            ApiTestUtils.request("POST", "/barcodes/", Gson().toJson(BarcodeDTO(null, "Content1", CodeType.Code128)))
            ApiTestUtils.request("POST", "/barcodes/", Gson().toJson(BarcodeDTO(null, "Content2", CodeType.QrCode)))

            // Act
            val response = ApiTestUtils.request("GET", "/barcodes/", null)

            // Assert
            assertThat(response?.body).contains(CodeType.Code128.toString())
            assertThat(response?.body).contains(CodeType.QrCode.toString())
            assertThat(response?.body).contains("Content1")
            assertThat(response?.body).contains("Content2")
        }

        @Test
        fun `server sends uuid for new barcode`() {
            // Arrange

            // Act
            val response = ApiTestUtils.request("POST", "/barcodes/", Gson().toJson(BarcodeDTO(null, "Content1", CodeType.Code128)))

            // Assert
            val barcode = Gson().fromJson(response?.body, BarcodeDTO::class.java)
            assertThat(barcode.uuid).isNotBlank()
        }

        @Test
        fun `server sends status code 200`() {
            // Arrange

            // Act
            val response = ApiTestUtils.request("POST", "/barcodes/", Gson().toJson(BarcodeDTO(null, "Content1", CodeType.Code128)))

            // Assert
            assertThat(response?.status).isEqualTo(200)
        }

        @Test
        fun `server sends json content type`() {
            // Arrange

            // Act
            val response = ApiTestUtils.request("POST", "/barcodes/", Gson().toJson(BarcodeDTO(null, "Content1", CodeType.Code128)))

            // Assert
            assertThat(response?.contentType).isEqualTo("application/json")
        }

        @Test
        fun `server sends json content`() {
            // Arrange

            // Act
            val response = ApiTestUtils.request("POST", "/barcodes/", Gson().toJson(BarcodeDTO(null, "Content1", CodeType.Code128)))

            // Assert
            val validJson = JsonUtils.isJSONValid(response?.body)
            assertThat(validJson).isTrue()
        }
    }

//    @Nested
//    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
//    inner class `invalid requests on greet` {
//        @ParameterizedTest
//        @MethodSource("invalidNameProvider")
//        fun `server does not send greeting in body`(testData: NameTestData) {
//            // Arrange
//
//            // Act
//            val response = ApiTestUtils.request("GET", "/greetings/${testData.value}", null)
//
//            // Assert
//            assertThat(response?.body).doesNotContain("Hello, ${testData.value}!")
//        }
//
//        @ParameterizedTest
//        @MethodSource("invalidNameProvider")
//        fun `server sends error message`(testData: NameTestData) {
//            // Arrange
//
//            // Act
//            val response = ApiTestUtils.request("GET", "/greetings/${testData.value}", null)
//
//            // Assert
//            assertThat(response?.body).contains("message")
//        }
//
//        @ParameterizedTest
//        @MethodSource("invalidNameProvider")
//        fun `server sends status code 400`(testData: NameTestData) {
//            // Arrange
//
//            // Act
//            val response = ApiTestUtils.request("GET", "/greetings/${testData.value}", null)
//
//            // Assert
//            assertThat(response?.status).isEqualTo(400)
//        }
//
//        @ParameterizedTest
//        @MethodSource("invalidNameProvider")
//        fun `server sends json content type`(testData: NameTestData) {
//            // Arrange
//
//            // Act
//            val response = ApiTestUtils.request("GET", "/greetings/${testData.value}", null)
//
//            // Assert
//            assertThat(response?.contentType).isEqualTo("application/json")
//        }
//
//        @ParameterizedTest
//        @MethodSource("invalidNameProvider")
//        fun `server sends json content`(testData: NameTestData) {
//            // Arrange
//
//            // Act
//            val response = ApiTestUtils.request("GET", "/greetings/${testData.value}", null)
//
//            // Assert
//            val validJson = JsonUtils.isJSONValid(response?.body)
//            assertThat(validJson).isTrue()
//        }
//
//        fun invalidNameProvider() = Stream.of(
//                //NameTestData(value = ""),
//                NameTestData(value = "%20")
//        )
//    }
//
//    data class NameTestData(
//            val value: String,
//            val expected: String? = null
//    )
}