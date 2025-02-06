package com.fetch.receiptprocessor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fetch.receiptprocessor.domain.Item;
import com.fetch.receiptprocessor.domain.Receipt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.fetch.receiptprocessor.repository.ReceiptRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ReceiptServiceTest {
    @InjectMocks
    private ReceiptService receiptService;

    @Mock
    private ReceiptRepository receiptRepository;

    @Test
    void shouldSaveReceiptAndReturnUUID() {

        UUID expectedId = UUID.randomUUID();
        Receipt receipt = new Receipt();
        receipt.setRetailer("Test Store");
        receipt.setPurchaseDate("2024-02-02");
        receipt.setPurchaseTime("12:30");
        receipt.setTotal("25.00");

        Item item = new Item();
        item.setShortDescription("Milk");
        item.setPrice("3.50");

        List<Item> items = List.of(item);
        receipt.setItems(items);

        when(receiptRepository.save(any(Receipt.class))).thenAnswer(invocation -> {
            Receipt savedReceipt = invocation.getArgument(0);
            savedReceipt.setId(expectedId);
            return savedReceipt;
        });

        UUID result = receiptService.saveReceipt(receipt);

        assertNotNull(result);
        assertEquals(expectedId, result);
        assertEquals(receipt, item.getReceipt()); // Ensure item is linked to the receipt
        verify(receiptRepository, times(1)).save(receipt);
    }

    @Test
    void shouldThrowExceptionWhenSavingNullReceipt() {
        assertThrows(NullPointerException.class, () -> receiptService.saveReceipt(null));
    }

    @Test
    void shouldFindReceiptByIdWhenExists() {

        UUID receiptId = UUID.randomUUID();
        Receipt receipt = new Receipt();
        receipt.setId(receiptId);

        when(receiptRepository.findById(receiptId)).thenReturn(Optional.of(receipt));


        Optional<Receipt> result = receiptService.findReceiptById(receiptId);


        assertTrue(result.isPresent());
        assertEquals(receiptId, result.get().getId());
        verify(receiptRepository, times(1)).findById(receiptId);
    }

    @Test
    void shouldReturnEmptyOptionalWhenReceiptDoesNotExist() {

        UUID receiptId = UUID.randomUUID();
        when(receiptRepository.findById(receiptId)).thenReturn(Optional.empty());


        Optional<Receipt> result = receiptService.findReceiptById(receiptId);


        assertFalse(result.isPresent());
        verify(receiptRepository, times(1)).findById(receiptId);
    }

    @Test
    void shouldCalculatePointsForValidReceipt() {

        UUID receiptId = UUID.randomUUID();
        Receipt receipt = new Receipt();
        receipt.setId(receiptId);
        receipt.setRetailer("M&M Corner Market");
        receipt.setTotal("50.00");
        receipt.setPurchaseDate("2024-02-02");
        receipt.setPurchaseTime("15:00");

        Item item1 = new Item();
        item1.setShortDescription("Chocolate");
        item1.setPrice("5.00");

        Item item2 = new Item();
        item2.setShortDescription("Milk");
        item2.setPrice("3.50");

        receipt.setItems(List.of(item1, item2));

        when(receiptRepository.getReferenceById(receiptId)).thenReturn(receipt);


        int points = receiptService.calculatePointsForReceipt(receiptId);


        assertTrue(points > 0); // Ensure points are awarded
        verify(receiptRepository, times(1)).getReferenceById(receiptId);
    }

    @Test
    void shouldThrowExceptionWhenReceiptNotFoundInCalculatePoints() {

        UUID receiptId = UUID.randomUUID();
        when(receiptRepository.getReferenceById(receiptId)).thenThrow(new IllegalArgumentException("Receipt not found"));


        assertThrows(IllegalArgumentException.class, () -> receiptService.calculatePointsForReceipt(receiptId));
        verify(receiptRepository, times(1)).getReferenceById(receiptId);
    }
}
