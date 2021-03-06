package kr.bistroad.userservice.user

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import kr.bistroad.userservice.user.domain.User
import kr.bistroad.userservice.user.domain.UserCredential
import kr.bistroad.userservice.user.domain.UserRole
import kr.bistroad.userservice.user.infrastructure.UserRepository
import kr.bistroad.userservice.user.presentation.UserRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
internal class UserIntegrationTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    private val objectMapper: ObjectMapper = ObjectMapper()

    @AfterEach
    fun clear() = userRepository.deleteAll()

    @Test
    fun `Gets a user`() {
        val user = User(
            credential = UserCredential(password = "example"),
            username = "john1234",
            fullName = "John",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )
        userRepository.save(user)

        mockMvc.perform(
            get("/users/${user.id}")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.id").value(user.id.toString()))
            .andExpect(jsonPath("\$.username").value(user.username))
            .andExpect(jsonPath("\$.fullName").value(user.fullName))
            .andExpect(jsonPath("\$.phone").value(user.phone))
            .andExpect(jsonPath("\$.role").value(user.role.name))
    }

    @Test
    fun `Searches users`() {
        val userA = User(
            credential = UserCredential(password = "example"),
            username = "userA",
            fullName = "John",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )

        val userB = User(
            credential = UserCredential(password = "example"),
            username = "userB",
            fullName = "Bob",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )

        val userC = User(
            credential = UserCredential(password = "example"),
            username = "userC",
            fullName = "Chris",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )

        userRepository.save(userA)
        userRepository.save(userB)
        userRepository.save(userC)

        mockMvc.perform(
            get("/users?sort=fullName,desc")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].username").value(userA.username))
            .andExpect(jsonPath("\$.[1].username").value(userC.username))
            .andExpect(jsonPath("\$.[2].username").value(userB.username))
    }

    @Test
    fun `Posts a user`() {
        val body = UserRequest.PostBody(
            username = "john",
            password = "VPlB3aWzlI",
            fullName = "John",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )

        mockMvc.perform(
            post("/users")
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.id").exists())
            .andExpect(jsonPath("\$.username").value(body.username))
            .andExpect(jsonPath("\$.fullName").value(body.fullName))
            .andExpect(jsonPath("\$.phone").value(body.phone))
            .andExpect(jsonPath("\$.role").value(body.role.name))

        val users = userRepository.findAll()
        users.shouldBeSingleton()
        users.first().username.shouldBe(body.username)
        users.first().fullName.shouldBe(body.fullName)
        users.first().phone.shouldBe(body.phone)
        users.first().role.shouldBe(body.role)
    }

    @Test
    fun `Patches a user`() {
        val user = User(
            credential = UserCredential(password = "VPlB3aWzlI"),
            username = "john",
            fullName = "John",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )
        userRepository.save(user)

        val body = UserRequest.PatchBody(
            username = null,
            password = "VPlB3aWzlI",
            fullName = "John David",
            phone = null,
            role = null
        )

        mockMvc.perform(
            patch("/users/${user.id}")
                .header("Authorization-User-Id", user.id.toString())
                .header("Authorization-Role", "ROLE_USER")
                .content(objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.id").value(user.id.toString()))
            .andExpect(jsonPath("\$.username").value(user.username))
            .andExpect(jsonPath("\$.fullName").value(body.fullName!!))
            .andExpect(jsonPath("\$.phone").value(user.phone))
            .andExpect(jsonPath("\$.role").value(user.role.name))

        val users = userRepository.findAll()
        users.shouldBeSingleton()
        users.first().username.shouldBe(user.username)
        users.first().fullName.shouldBe(body.fullName)
        users.first().phone.shouldBe(user.phone)
        users.first().role.shouldBe(user.role)
    }

    @Test
    fun `Deletes a user`() {
        val userA = User(
            credential = UserCredential(password = "example"),
            username = "example1",
            fullName = "example",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )
        val userB = User(
            credential = UserCredential(password = "example"),
            username = "example2",
            fullName = "example",
            phone = "010-0000-0000",
            role = UserRole.ROLE_USER
        )

        userRepository.save(userA)
        userRepository.save(userB)

        mockMvc.perform(
            delete("/users/${userA.id}")
                .header("Authorization-User-Id", userA.id.toString())
                .header("Authorization-Role", "ROLE_USER")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)
            .andExpect(content().string(""))

        val users = userRepository.findAll()
        users.shouldBeSingleton()
        users.first().shouldBe(userB)
    }
}