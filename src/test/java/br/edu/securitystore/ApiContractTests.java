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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@SpringBootTest @AutoConfigureMockMvc
class ApiContractTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test void publicCatalogIsReadable() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").exists());
    }

    @Test void anonymousCannotCreateProduct() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Invasor\",\"price\":1,\"stock\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test void customerCannotCreateProduct() throws Exception {
        mvc.perform(post("/api/products").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("aluno@lab.local", "Aluno123!"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Teste\",\"price\":1,\"stock\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test void registrationValidationUsesProblemDetails() throws Exception {
        mvc.perform(post("/api/iam/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Teste\",\"email\":\"teste@lab.local\",\"password\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Dados da requisição inválidos"));
    }

    @Test void orderResponseDoesNotExposePasswordHash() throws Exception {
        mvc.perform(post("/api/orders").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic("aluno@lab.local", "Aluno123!"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"productId\":1,\"quantity\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.total").value(149.90));
    }

    @Test void orderLifecycleAndOwnership() throws Exception {
        var customer = SecurityMockMvcRequestPostProcessors.httpBasic("aluno@lab.local", "Aluno123!");
        String orderJson = mvc.perform(post("/api/orders").with(customer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":2,\"quantity\":1}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andReturn().getResponse().getContentAsString();
        long orderId = json.readTree(orderJson).get("id").asLong();
        mvc.perform(post("/api/orders/" + orderId + "/pay").with(customer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"method\":\"MOCK\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.paymentStatus").value("PAID"));
        mvc.perform(get("/api/orders").with(customer)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customer.passwordHash").doesNotExist());
        mvc.perform(patch("/api/orders/" + orderId + "/delivery").with(customer).contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isForbidden());
    }
}
