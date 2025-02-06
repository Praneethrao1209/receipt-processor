package com.fetch.receiptprocessor.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fetch.receiptprocessor.domain.Receipt;
import com.fetch.receiptprocessor.service.ReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ReceiptControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ReceiptController receiptController;

    @Mock
    private ReceiptService receiptService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(receiptController).build();
    }

    @Test
    void shouldProcessReceiptSuccessfully() throws Exception {
        UUID receiptId = UUID.randomUUID();
        when(receiptService.saveReceipt(any(Receipt.class))).thenReturn(receiptId);

        mockMvc.perform(MockMvcRequestBuilders.post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"retailer\": \"M&M Market\", \"purchaseDate\": \"2024-02-02\", \"purchaseTime\": \"15:30\", \"total\": \"25.00\", \"items\": [{ \"shortDescription\": \"Milk\", \"price\": \"3.50\" }] }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(receiptId.toString()));

        verify(receiptService, times(1)).saveReceipt(any(Receipt.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidReceipt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Missing required fields
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The receipt is invalid. Please verify input."));
    }

    @Test
    void shouldReturnPointsForValidReceiptId() throws Exception {
        UUID receiptId = UUID.randomUUID();
        when(receiptService.findReceiptById(receiptId)).thenReturn(Optional.of(new Receipt()));
        when(receiptService.calculatePointsForReceipt(receiptId)).thenReturn(100);

        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/" + receiptId + "/points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(100));

        verify(receiptService, times(1)).calculatePointsForReceipt(receiptId);
    }

    @Test
    void shouldReturnNotFoundForNonExistingReceiptId() throws Exception {
        UUID receiptId = UUID.randomUUID();
        when(receiptService.findReceiptById(receiptId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/" + receiptId + "/points"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No receipt found for that ID."));
    }

    @Test
    void shouldReturnBadRequestForInvalidUUID() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/receipts/invalid-uuid/points"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid UUID format."));
    }
}
