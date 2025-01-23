package com.nsider.SlidesToNotes.controller;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nsider.SlidesToNotes.model.DocxRequest;
import com.nsider.SlidesToNotes.utils.files.DocumentHelper;

import java.io.ByteArrayOutputStream;

/**
 * Controller responsible for generating a DOCX file from the provided content.
 * Handles requests related to DOCX file generation.
 */
@RestController
@RequestMapping("/api/docx")
public class DocxController {

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    private final DocumentHelper documentHelper = new DocumentHelper();

    /**
     * Generates a DOCX file based on the provided DocxRequest.
     * The file is created with headers, paragraphs, and images as per the request.
     *
     * @param docxRequest The request payload containing the file details and content nodes. Must not be {@code null}.
     * @return A {@link ResponseEntity} containing the generated DOCX file as a byte array.
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateDocx(
            @RequestBody DocxRequest docxRequest,
            @RequestHeader("Origin") String origin) {

        if (!allowedOrigin.equals(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try (XWPFDocument document = new XWPFDocument()) {
            for (DocxRequest.Node node : docxRequest.getNodes()) {
                switch (node.getType()) {
                    case "H2":
                        documentHelper.addHeader2(document, node.getText());
                        break;

                    case "P":
                        documentHelper.addParagraph(document, node.getText());
                        break;

                    case "IMG":
                        documentHelper.addImage(document, node.getSrc(), docxRequest.getImageFormat());
                        break;
                        
                    case "TABLE":
                        documentHelper.addTable(document, node.getTableRows());
                        break;    

                    default:
                        break;
                }
            }

            return createDocxResponse(document, docxRequest.getFileName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Creates a {@link ResponseEntity} containing the generated DOCX file as a byte array.
     * The file is returned as an attachment with the specified file name.
     *
     * @param document  The generated DOCX document to be sent as the response body.
     * @param fileName  The name of the generated file. Must not be {@code null}.
     * @return A {@link ResponseEntity} containing the DOCX file as a byte array.
     * @throws Exception If an error occurs during document conversion to bytes.
     */
    private ResponseEntity<byte[]> createDocxResponse(
            XWPFDocument document, 
            String fileName) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        byte[] docxBytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName + ".docx");
        return new ResponseEntity<>(docxBytes, headers, HttpStatus.OK);
    }
}