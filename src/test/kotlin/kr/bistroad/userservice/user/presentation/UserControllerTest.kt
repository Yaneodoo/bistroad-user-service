package kr.bistroad.userservice.user.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kr.bistroad.userservice.user.application.UserDto
import kr.bistroad.userservice.user.application.UserService
import kr.bistroad.userservice.user.domain.UserRole
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(controllers = [UserController::class])
internal class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    private val objectMapper: ObjectMapper = ObjectMapper()

    @Test
    fun `Returns bad request when getting a user without a valid token`() {
        mockMvc.perform(
            get("/users/me")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `Returns forbidden when posting an admin user without permission`() {
        val body = UserRequest.PostBody(
            username = "example",
            password = "example",
            fullName = "example",
            phone = "010-0000-0000",
            role = UserRole.ROLE_ADMIN
        )

        mockMvc.perform(
            post("/users")
                .header("Authorization-User-Id", UUID.randomUUID().toString())
                .header("Authorization-Role", "ROLE_USER")
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `Returns forbidden when patching a user to admin without permission`() {
        val userId = UUID.randomUUID()
        val body = UserRequest.PatchBody(
            username = "example",
            password = "example",
            fullName = "example",
            phone = "010-0000-0000",
            role = UserRole.ROLE_ADMIN
        )

        mockMvc.perform(
            patch("/users/${userId}")
                .header("Authorization-User-Id", UUID.randomUUID().toString())
                .header("Authorization-Role", "ROLE_USER")
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `Returns forbidden when deleting a user without permission`() {
        val userId = UUID.randomUUID()
        every { userService.deleteUser(UserDto.Delete(userId)) } returns true

        mockMvc.perform(
            delete("/users/${userId}")
                .header("Authorization-User-Id", UUID.randomUUID().toString())
                .header("Authorization-Role", "ROLE_USER")
        ).andExpect(status().isForbidden)

        mockMvc.perform(
            delete("/users/${userId}")
                .header("Authorization-User-Id", UUID.randomUUID().toString())
                .header("Authorization-Role", "ROLE_ADMIN")
        ).andExpect(status().is2xxSuccessful)
    }
}