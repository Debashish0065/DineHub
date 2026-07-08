package com.dinehub.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.dinehub.entity.Order;
import com.dinehub.entity.User;
import com.dinehub.repository.OrderRepository;
import com.dinehub.repository.UserRepository;
import com.dinehub.service.OrderService;
import com.dinehub.service.PdfInvoiceService;
import com.dinehub.service.EmailService;
import com.dinehub.service.impl.CartServiceImpl;

@Controller
public class OrderController {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PdfInvoiceService pdfInvoiceService;
    
    @Autowired
    private EmailService emailService;


    // ======================================
    // CONFIRM ORDER
    // ======================================
    @PostMapping("/confirm-order")
    public String confirmOrder(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String paymentMethod,
            Principal principal,
            Model model) {

        BigDecimal totalAmount = cartService.getCartTotal(principal.getName());

        // COD PAYMENT
        if ("COD".equalsIgnoreCase(paymentMethod)) {

        	Order order = orderService.placeOrder(
        	        principal.getName(),
        	        address,
        	        "COD",
        	        "PENDING",
        	        totalAmount
        	);


        	emailService.sendOrderConfirmation(
        	        order.getUser().getEmail(),
        	        order.getOrderNumber(),
        	        order.getTotalAmount().toString()
        	);


        	return "redirect:/order-success";
        }

        // UPI PAYMENT
        model.addAttribute("name", name);
        model.addAttribute("phone", phone);
        model.addAttribute("address", address);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("totalAmount", totalAmount);

        return "upi-payment";
    }

    // ======================================
    // LOAD UPI PAGE
    // ======================================
    @GetMapping("/upi-payment")
    public String upiPayment(Model model, Principal principal) {

        model.addAttribute(
                "totalAmount",
                cartService.getCartTotal(principal.getName()));

        return "upi-payment";
    }

    // ======================================
    // PAYMENT SUCCESS
    // ======================================
    @PostMapping("/payment-success")
    public String paymentSuccess(Principal principal) {

        BigDecimal totalAmount = cartService.getCartTotal(principal.getName());

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));


        Order order = orderService.placeOrder(
                principal.getName(),
                user.getAddress(),
                "UPI",
                "PAID",
                totalAmount
        );


        emailService.sendOrderConfirmation(
                order.getUser().getEmail(),
                order.getOrderNumber(),
                order.getTotalAmount().toString()
        );


        return "redirect:/order-success";
    }

    // ======================================
    // ORDER SUCCESS PAGE
    // ======================================
    @GetMapping("/order-success")
    public String orderSuccess() {
        return "order-success";
    }

    // ======================================
    // MY ORDERS
    // ======================================
    @GetMapping("/orders")
    public String myOrders(Principal principal,
                           Model model) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        model.addAttribute(
                "orders",
                orderRepository.findByUser(user));

        return "orders";
    }

    // ======================================
    // ORDER DETAILS
    // ======================================
    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id,
                               Principal principal,
                               Model model) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        // Security Check
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access Denied");
        }

        model.addAttribute("order", order);

        return "order-details";
    }
 // ======================================
 // DOWNLOAD PDF INVOICE
 // ======================================
 @GetMapping("/orders/{id}/invoice")
 public ResponseEntity<InputStreamResource> downloadInvoice(
         @PathVariable Long id,
         Principal principal) {

     User user = userRepository.findByEmail(principal.getName())
             .orElseThrow(() ->
                     new RuntimeException("User not found"));

     Order order = orderRepository.findById(id)
             .orElseThrow(() ->
                     new RuntimeException("Order not found"));

     // Security Check
     if (!order.getUser().getId().equals(user.getId())) {
         throw new RuntimeException("Access Denied");
     }

     ByteArrayInputStream pdf =
             pdfInvoiceService.generateInvoice(id);

     HttpHeaders headers = new HttpHeaders();

     headers.add(
             HttpHeaders.CONTENT_DISPOSITION,
             "inline; filename=invoice-" + order.getInvoiceNumber() + ".pdf");

     return ResponseEntity
             .ok()
             .headers(headers)
             .contentType(MediaType.APPLICATION_PDF)
             .body(new InputStreamResource(pdf));
 }

}