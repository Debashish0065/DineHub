package com.dinehub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {


    @Autowired
    private JavaMailSender mailSender;


    public void sendOrderConfirmation(
            String email,
            String orderNumber,
            String totalAmount) {


        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "DineHub Order Confirmation");


        message.setText(
                "Hello,\n\n"
                + "Your order has been placed successfully.\n\n"
                + "Order Number: " + orderNumber + "\n"
                + "Total Amount: ₹" + totalAmount + "\n\n"
                + "Thank you for ordering from DineHub.\n\n"
                + "Enjoy your food!"
        );


        try {
            mailSender.send(message);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}