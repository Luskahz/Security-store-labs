package br.edu.securitystore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest @AutoConfigureMockMvc
class ApiContractTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String token(String email, String password) throws Exception {
        String body = json.writeValueAsString(java.util.Map.of("email", email, "password", password));
        String response = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(response).get("accessToken").asText();
    }

    @Test void publicCatalogIsReadable() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").exists());
    }

    @Test void anonymousCannotCreateProduct() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Invasor\",\"price\":1,\"stock\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test void customerCannotCreateProduct() throws Exception {
        mvc.perform(post("/api/products").header("Authorization", token("aluno@lab.local", "Aluno123!"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Teste\",\"price\":1,\"stock\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test void registrationValidationUsesProblemDetails() throws Exception {
        mvc.perform(post("/admin/users").header("Authorization", token("admin@lab.local", "Admin123!")).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Teste\",\"email\":\"teste@lab.local\",\"password\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Dados da requisição inválidos"));
    }

    @Test void orderResponseDoesNotExposePasswordHash() throws Exception {
        mvc.perform(post("/api/orders").header("Authorization", token("aluno@lab.local", "Aluno123!"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"productId\":1,\"quantity\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.total").value(149.90));
    }

    @Test void orderLifecycleAndOwnership() throws Exception {
        String customer = token("aluno@lab.local", "Aluno123!");
        String orderJson = mvc.perform(post("/api/orders").header("Authorization", customer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":2,\"quantity\":1}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andReturn().getResponse().getContentAsString();
        long orderId = json.readTree(orderJson).get("id").asLong();
        mvc.perform(post("/api/orders/" + orderId + "/pay").header("Authorization", customer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"method\":\"MOCK\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.paymentStatus").value("PAID"));
        mvc.perform(get("/api/orders").header("Authorization", customer)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customer.passwordHash").doesNotExist());
        mvc.perform(patch("/api/orders/" + orderId + "/delivery").header("Authorization", customer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isForbidden());
    }
}
