package com.dinehub.service;

import java.io.ByteArrayInputStream;

public interface PdfInvoiceService {

    ByteArrayInputStream generateInvoice(Long orderId);

}