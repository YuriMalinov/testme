package ru.smarty.testme.repositories

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.smarty.testme.model.Test
import java.io.File
import java.util.*
import javax.annotation.PostConstruct

@Service
class TestRepository {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    var tests: Map<String, Test> = emptyMap()
        private set

    @PostConstruct
    fun updateTests() {
        val objectMapper = ObjectMapper(YAMLFactory())

        val tests = TreeMap<String, Test>()
        File("tests").listFiles().forEach { file ->
            tests[file.name] = try {
                objectMapper.readValue(file, Test::class.java)
            } catch (e: Exception) {
                val t = Test()
                t.title = file.name
                t.description = "Failed to read test: " + e.message
                t.time = "Unknown"
                logger.warn("Error while reading test file ${file.name}: ${e.message}", e);
                t
            }
        }

        this.tests = tests
    }

    fun allTests() = tests.values

    fun testByCode(code: String): Test? = tests[code]
}