package filehandler.project.Unit

import filehandler.project.controller.FileController
import filehandler.project.service.S3Service
import net.minidev.json.parser.JSONParser
import org.json.JSONObject
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class FileSpec extends Specification {

    MockMvc mockMvc

    @Autowired
    FileController fileController

    @SpringBean
    private S3Service s3Service = Stub()

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build()
    }


    def "upload should throw an error if file field was empty"() {

        given:
        MockMultipartFile jsonFile = new MockMultipartFile("test", "test.json", "application/json", "{\"key1\": \"value1\"}".getBytes())

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file(jsonFile))

        then:
        response.andExpect(status().isBadRequest())

    }

    def "upload should return success if all goes well"() {
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

    def "download should return exception if UUID not found"() {
        given:
        String uuid = "some-chert-uuid"

        when:
        mockMvc.perform(MockMvcRequestBuilders.get("/download/" + uuid))

        then:
        def exception = thrown(Exception)
        assert exception.getMessage().indexOf(uuid) >= 0
    }

//    def "download should return success if all goes well"() {
//        given:
//        String filename = "test.json"
//        MockMultipartFile jsonFile = new MockMultipartFile("file", filename, "application/json", "{\"key1\": \"value1\"}".getBytes())
//        def uploadResponse = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file(jsonFile))
//        def uploadContent = uploadResponse.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
//        JSONParser parser = new JSONParser()
//        JSONObject json = (JSONObject) parser.parse(uploadContent)
//        String uuid = json.data.uuid
//
//        when:
//        def response = mockMvc.perform(MockMvcRequestBuilders.get("/download/" + uuid))
//
//        then:
//        def content = response.andExpect(status().isOk()).andReturn().getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION) as String
//
//        assert content.endsWith(filename)
//    }
}
