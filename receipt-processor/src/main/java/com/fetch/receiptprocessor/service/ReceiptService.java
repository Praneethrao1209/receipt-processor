package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.domain.Item;
import com.fetch.receiptprocessor.domain.Receipt;
import com.fetch.receiptprocessor.repository.ReceiptRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;



@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Transactional
    public UUID saveReceipt(Receipt receipt){
        receipt.getItems().forEach(item -> item.setReceipt(receipt));
        Receipt savedReceipt = receiptRepository.save(receipt);
        return savedReceipt.getId();
    }

    public Optional<Receipt> findReceiptById(UUID id) {
        return receiptRepository.findById(id);
    }

    public int calculatePointsForReceipt(UUID id){
        Receipt receipt = receiptRepository.getReferenceById(id);
        int points = 0 ;

        int retailerLength = receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();
         points +=retailerLength;

        double total = Double.parseDouble(receipt.getTotal());
        if(total == Math.floor(total)){
            points +=50;
        }

        if(total % 0.25 == 0){
            points += 25;
        }

        int itemPairs  = (receipt.getItems().size()) / 2 ;
        points += itemPairs * 5;

        for(Item item : receipt.getItems()){
            int descriptionLength = item.getShortDescription().trim().length();
            if(descriptionLength % 3 == 0){
                double price = Double.parseDouble(item.getPrice());
                points += Math.ceil(price * 0.2);
            }
        }

        LocalDate currentDate = LocalDate.parse(receipt.getPurchaseDate());
        int day = currentDate.getDayOfMonth();
            if(day % 2 == 1){
            points += 6;
            }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime purchaseTime = LocalTime.parse(receipt.getPurchaseTime(), timeFormatter);
        LocalTime start = LocalTime.of(14, 0);
        LocalTime end = LocalTime.of(16, 0);

        if (purchaseTime.isAfter(start) && purchaseTime.isBefore(end)) {
            points += 10;
        }

        return points;
    }
}
