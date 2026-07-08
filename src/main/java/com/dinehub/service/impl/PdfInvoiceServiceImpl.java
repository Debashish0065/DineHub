package com.dinehub.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.dinehub.entity.Order;
import com.dinehub.entity.OrderItem;
import com.dinehub.repository.OrderRepository;
import com.dinehub.service.PdfInvoiceService;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfInvoiceServiceImpl implements PdfInvoiceService {

    private final OrderRepository orderRepository;

    @Override
    public ByteArrayInputStream generateInvoice(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font headingFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);

            document.add(new Paragraph("DineHub Invoice", titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Invoice No : " + order.getInvoiceNumber(),
                    normalFont));

            document.add(new Paragraph(
                    "Order No : " + order.getOrderNumber(),
                    normalFont));

            document.add(new Paragraph(
                    "Date : " +
                            order.getCreatedAt()
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                    normalFont));

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Customer Details", headingFont));

            document.add(new Paragraph(
                    order.getUser().getFirstName() + " " +
                    order.getUser().getLastName(),
                    normalFont));

            document.add(new Paragraph(
                    order.getUser().getEmail(),
                    normalFont));

            document.add(new Paragraph(
                    order.getDeliveryPhone(),
                    normalFont));

            document.add(new Paragraph(
                    order.getDeliveryAddress(),
                    normalFont));

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            table.addCell(new PdfPCell(new Phrase("Product")));
            table.addCell(new PdfPCell(new Phrase("Qty")));
            table.addCell(new PdfPCell(new Phrase("Price")));
            table.addCell(new PdfPCell(new Phrase("Total")));

            for (OrderItem item : order.getOrderItems()) {

                table.addCell(item.getProductName());

                table.addCell(item.getQuantity().toString());

                table.addCell("₹ " + item.getPrice());

                table.addCell(
                        "₹ " +
                        item.getPrice()
                                .multiply(
                                        java.math.BigDecimal.valueOf(
                                                item.getQuantity())));
            }

            document.add(table);

            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Payment Method : " + order.getPaymentMethod(),
                    normalFont));

            document.add(new Paragraph(
                    "Payment Status : " + order.getPaymentStatus(),
                    normalFont));

            document.add(new Paragraph(
                    "Order Status : " + order.getStatus(),
                    normalFont));

            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Grand Total : ₹ " + order.getTotalAmount(),
                    headingFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(
                    "Thank you for ordering with DineHub!",
                    headingFont));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Unable to generate invoice.");
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}