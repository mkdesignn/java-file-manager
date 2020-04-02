package filehandler.project.Unit

import filehandler.project.controller.MailController
import net.minidev.json.parser.JSONParser
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class MailSpec extends Specification {

    MockMvc mockMvc

    @Autowired
    MailController mailController

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mailController).build()
    }

    def "sending mail should throw an error if one of receivers are invalid email address"() {

        given:
        MockMultipartFile attachmentFile = new MockMultipartFile("attachment", "test.json", "application/json", "{\"key1\": \"value1\"}".getBytes())

        when:
        def requestBuilder = MockMvcRequestBuilders
                .multipart("/mail")
                .file(attachmentFile)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("subject", "test email subject")
                .param("text", "test email body")
                .param("receivers", "imany")

        mockMvc.perform(requestBuilder)

        then:
        def exception = thrown(Exception)
        assert exception.getMessage().indexOf("Invalid Addresses") >= 0

    }

    def "sending mail should return success without file"() {

        when:
        def requestBuilder = MockMvcRequestBuilders
                .multipart("/mail")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("subject", "test email subject")
                .param("text", "test email body")
                .param("receivers", "imany.apk@gmail.com")

        def response = mockMvc.perform(requestBuilder)

        then:
        def content = response.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
        JSONParser parser = new JSONParser()
        JSONObject json = (JSONObject) parser.parse(content)

        assert json.status == 200
        assert json.data.message == "Email successfully sent"

    }

    def "sending mail should return success with file"() {

        given:
        MockMultipartFile attachmentFile = new MockMultipartFile("attachment", "test.json", "application/json", "{\"key1\": \"value1\"}".getBytes())

        when:
        def requestBuilder = MockMvcRequestBuilders
                .multipart("/mail")
                .file(attachmentFile)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("subject", "test email subject")
                .param("text", "test email body")
                .param("receivers", "imany.apk@gmail.com")

        def response = mockMvc.perform(requestBuilder)

        then:
        def content = response.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
        JSONParser parser = new JSONParser()
        JSONObject json = (JSONObject) parser.parse(content)

        assert json.status == 200
        assert json.data.message == "Email successfully sent"

    }

}
