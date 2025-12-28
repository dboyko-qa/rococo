package qa.dboyko.rococo.integrationtests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.dboyko.rococo.grpc.Userdata;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.service.UserClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestClientConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserClient userClient;

    // -----------------------
    // GET /api/user
    // -----------------------

    @Test
    @DisplayName("Should return current user when JWT is provided")
    void shouldReturnCurrentUserWhenJwtIsProvided() throws Exception {
        // arrange
        Userdata grpcUser = Userdata.newBuilder()
                .setUserId("1")
                .setUsername("johndoe")
                .setFirstname("John")
                .setLastname("Doe")
                .setAvatar("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA")
                .build();

        given(userClient.getUser("johndoe")).willReturn(grpcUser);

        // act & assert
        mockMvc.perform(get("/api/user")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "johndoe")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"));
    }

    // -----------------------
    // PATCH /api/user
    // -----------------------

    @Test
    @DisplayName("Should update current user when JWT is provided")
    void shouldUpdateUserWhenJwtIsProvided() throws Exception {
        // arrange
        Userdata updatedGrpcUser = Userdata.newBuilder()
                .setUserId("1")
                .setUsername("johndoe")
                .setFirstname("Johnny")
                .setLastname("Doe")
                .setAvatar("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA")
                .build();

        given(userClient.updateUser(any(UserdataJson.class)))
                .willReturn(updatedGrpcUser);

        String requestBody = """
            {
              "id": "1",
              "username": "johndoe",
              "firstname": "Johnny",
              "lastname": "Doe",
              "avatar": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA"
            }
            """;

        // act & assert
        mockMvc.perform(patch("/api/user")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "johndoe")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.firstname").value("Johnny"));
    }

    // -----------------------
    // Security
    // -----------------------

    @Test
    @DisplayName("Should return 401 when requesting current user without JWT")
    void shouldReturn401WhenJwtIsMissing() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
