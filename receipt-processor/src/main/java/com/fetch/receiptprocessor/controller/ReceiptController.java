package com.fetch.receiptprocessor.controller;


import com.fetch.receiptprocessor.domain.Receipt;
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
        try {
            UUID uuid = UUID.fromString(id);
            Optional<Receipt> receipt = receiptService.findReceiptById(uuid);

            if (receipt.isEmpty()) {
                return new ResponseEntity<>("No receipt found for that ID.", HttpStatus.NOT_FOUND);
            }

            int points = receiptService.calculatePointsForReceipt(uuid);
            return ResponseEntity.ok().body(Map.of("points", points));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid UUID format.", HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException() {
        return ResponseEntity.badRequest().body("The receipt is invalid. Please verify input.");
    }
}
