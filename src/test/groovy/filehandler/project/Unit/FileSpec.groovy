package filehandler.project.Unit

import filehandler.project.controller.FileController
import net.minidev.json.parser.JSONParser
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class FileSpec extends Specification {

    MockMvc mockMvc

    @Autowired
    FileController fileController

    def setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build()
    }

    def "upload should throw an error if file field was empty"(){

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload"))

        then:
        response.andExpect(status().isBadRequest())

    }

    def "upload should return success if all goes well"(){
        given:
        MockMultipartFile jsonFile = new MockMultipartFile("file", "test.json", "application/json", "{\"key1\": \"value1\"}".getBytes())

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file(jsonFile))

        then:
        def content = response.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()

        JSONParser parser = new JSONParser()
        JSONObject json = (JSONObject) parser.parse(content)

        assert json.data.name == "test"
        assert json.data.type == "json"
    }
}
