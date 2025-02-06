package com.fetch.receiptprocessor.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "Receipt_details")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="receipt_id")
    private UUID id;

    @Version
    private Long version = 0L;

    @NotBlank(message = "Retailer name is required")
    @Pattern(regexp = "^[\\w\\s\\-&]+$", message = "Retailer name contains invalid characters")
    private String retailer;

    @NotBlank(message = "PurchaseDate is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Purchase date must be in YYYY-MM-DD format")
    private String purchaseDate;

    @NotBlank(message = "PurchaseTime is required")
    @Pattern(regexp = "^(?:[01]\\d|2[0-3]):[0-5]\\d$", message = "Purchase time must be in HH:mm format (24-hour)")
    private String purchaseTime;

    @NotBlank(message = "Total is required")
    @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "Total must be in decimal format with two decimal places (e.g., 35.35)")
    private String total;

    @NotEmpty(message = "Items list is required")
    @Size(min = 1, message = "At least one item is required")
    @OneToMany(mappedBy = "receipt",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;


    public Receipt() {}


    public Receipt(String retailer, String purchaseDate, String purchaseTime, String total, List<Item> items) {
        this.retailer = retailer;
        this.purchaseDate = purchaseDate;
        this.purchaseTime = purchaseTime;
        this.total = total;
        this.items = items;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }



    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(String purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
