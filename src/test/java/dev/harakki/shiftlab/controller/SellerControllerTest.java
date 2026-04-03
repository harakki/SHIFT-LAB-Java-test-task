package dev.harakki.shiftlab.controller;

import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import dev.harakki.shiftlab.service.SellerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import dev.harakki.shiftlab.dto.*;

import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SellerController.class)
class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SellerService sellerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllSellers_shouldReturnPage() throws Exception {
        var seller = new SellerSummaryResponseDto(1L, "John Doe");
        var page = new PageImpl<>(List.of(seller));

        Mockito.when(sellerService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/sellers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));
    }

    @Test
    void getSeller_shouldReturnSeller() throws Exception {
        var response = new SellerDetailResponseDto(
                1L,
                "John Doe",
                "john.doe@example.com",
                LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        Mockito.when(sellerService.get(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/sellers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.contactInfo").value("john.doe@example.com"));
    }

    @Test
    void createSeller_shouldReturnCreated() throws Exception {
        var request = new SellerCreateDto("John Doe", "contact");
        var response = new SellerDetailResponseDto(
                1L,
                "John Doe",
                "contact",
                LocalDateTime.now()
        );

        Mockito.when(sellerService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        Mockito.verify(sellerService).create(any());
    }

    @Test
    void createSeller_shouldFailValidation_whenNameBlank() throws Exception {
        var request = new SellerCreateDto("", "contact");

        mockMvc.perform(post("/api/v1/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSeller_shouldReturnUpdated() throws Exception {
        var request = new SellerUpdateDto("Updated", "new contact");
        var response = new SellerDetailResponseDto(
                1L,
                "Updated",
                "new contact",
                LocalDateTime.now()
        );

        Mockito.when(sellerService.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/sellers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));

        Mockito.verify(sellerService).update(eq(1L), any());
    }

    @Test
    void deleteSeller_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/sellers/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(sellerService).delete(1L);
    }

}
