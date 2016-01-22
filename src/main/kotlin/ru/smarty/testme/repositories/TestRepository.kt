package ru.smarty.testme.repositories

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.springframework.stereotype.Service
import ru.smarty.testme.model.Test
import java.io.File
import java.util.*
import javax.annotation.PostConstruct

@Service
class TestRepository {
    var tests: Map<String, Test> = emptyMap()
        private set

    @PostConstruct
    fun updateTests() {
        val objectMapper = ObjectMapper(YAMLFactory())

        val tests = TreeMap<String, Test>()
        File("tests").listFiles().forEach { file ->
            tests[file.name] = objectMapper.readValue(file, Test::class.java)
        }

        this.tests = tests
    }

    fun allTests() = tests.values

    fun testByCode(code: String): Test? = tests[code]
}