package kr.bistroad.userservice.user.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kr.bistroad.userservice.global.config.security.JwtAuthenticationProvider
import kr.bistroad.userservice.global.config.security.JwtSigner
import kr.bistroad.userservice.global.config.security.UserPrincipal
import kr.bistroad.userservice.user.application.UserDto
import kr.bistroad.userservice.user.application.UserService
import kr.bistroad.userservice.user.domain.UserRole
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(controllers = [UserController::class])
@Import(JwtAuthenticationProvider::class, JwtSigner::class)
internal class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    @MockK
    private lateinit var securityContext: SecurityContext

    private val objectMapper: ObjectMapper = ObjectMapper()

    @BeforeEach
    fun `Set security context`() {
        SecurityContextHolder.setContext(securityContext)
        every { securityContext.authentication = any() } just Runs
    }

    @Test
    fun `Returns forbidden when getting a user without a valid token`() {
        every { securityContext.authentication } returns null

        mockMvc.perform(get("/users/me"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `Returns forbidden when posting an admin user without permission`() {
        val dto = UserDto.CreateReq(
            username = "example",
            password = "example",
            fullName = "example",
            phone = "010-0000-0000",
            role = UserRole.ROLE_ADMIN
        )

        every { securityContext.authentication } returns
                tokenOf(UUID.randomUUID(), UserRole.ROLE_USER)

        val body = objectMapper.writeValueAsString(dto)
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `Returns forbidden when patching a user to admin without permission`() {
        val userId = UUID.randomUUID()
        val dto = UserDto.PatchReq(
            username = "example",
            password = "example",
            fullName = "example",
            phone = "010-0000-0000",
            role = UserRole.ROLE_ADMIN
        )

        every { securityContext.authentication } returns
                tokenOf(UUID.randomUUID(), UserRole.ROLE_USER)

        val body = objectMapper.writeValueAsString(dto)
        mockMvc.perform(patch("/users/${userId}").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `Returns forbidden when deleting a user without permission`() {
        val userId = UUID.randomUUID()
        every { userService.deleteUser(userId) } returns true

        every { securityContext.authentication } returns
                tokenOf(UUID.randomUUID(), UserRole.ROLE_USER)
        mockMvc.perform(delete("/users/${userId}"))
            .andExpect(status().isForbidden)

        every { securityContext.authentication } returns
                tokenOf(UUID.randomUUID(), UserRole.ROLE_ADMIN)
        mockMvc.perform(delete("/users/${userId}"))
            .andExpect(status().is2xxSuccessful)
    }

    private fun tokenOf(userId: UUID, role: UserRole) = UsernamePasswordAuthenticationToken(
        UserPrincipal(userId, role), null,
        mutableListOf(SimpleGrantedAuthority(role.name))
    )
}