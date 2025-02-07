package com.fetch.receiptprocessor.controller;


import com.fetch.receiptprocessor.domain.Receipt;
import com.fetch.receiptprocessor.exception.ReceiptNotFoundException;
import com.fetch.receiptprocessor.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> processReceipt(@Valid @RequestBody Receipt receipt){
        String receiptId = receiptService.saveReceipt(receipt).toString();
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", receiptId));
    };

    @GetMapping("/{id}/points")
    public ResponseEntity<?> getReceiptPoints(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        Optional<Receipt> receipt = receiptService.findReceiptById(uuid);

        if (receipt.isEmpty()) {
            throw new ReceiptNotFoundException("No receipt found for that ID.");
        }

        int points = receiptService.calculatePointsForReceipt(uuid);
        return ResponseEntity.ok().body(Map.of("points", points));
    }

}
