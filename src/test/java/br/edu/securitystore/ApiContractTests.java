package br.edu.securitystore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest @AutoConfigureMockMvc
class ApiContractTests {
    @Autowired MockMvc mvc;

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
}
