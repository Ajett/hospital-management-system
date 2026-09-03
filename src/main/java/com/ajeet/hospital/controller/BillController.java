package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.BillRequest;
import com.ajeet.hospital.dto.BillResponse;
import com.ajeet.hospital.dto.PaymentStatusRequest;
import com.ajeet.hospital.service.BillService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    // CREATE BILL
    @PostMapping
    public BillResponse createBill(
            @Valid @RequestBody BillRequest request) {

        return billService.createBill(request);
    }

    // GET ALL BILLS
    @GetMapping
    public List<BillResponse> getAllBills() {

        return billService.getAllBills();
    }

    // GET BILL BY ID
    @GetMapping("/{id}")
    public BillResponse getBillById(
            @PathVariable Long id) {

        return billService.getBillById(id);
    }

    @PatchMapping("/{id}/status")
    public BillResponse updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody PaymentStatusRequest request) {

        return billService.updatePaymentStatus(
                id,
                request.getStatus()
        );
    }

    // UPDATE BILL
    @PutMapping("/{id}")
    public BillResponse updateBill(
            @PathVariable Long id,
            @Valid @RequestBody BillRequest request) {

        return billService.updateBill(id, request);
    }


    // DELETE BILL
    @DeleteMapping("/{id}")
    public String deleteBill(
            @PathVariable Long id) {

        billService.deleteBill(id);

        return "Bill deleted successfully";
    }


}
