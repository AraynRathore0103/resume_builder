package com.resume.builder.resume_builder.resume;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;

import java.io.FileNotFoundException;

public class ResumeGenerator {
    public static void generateResume(String resumeText, String filePath) {
        try {
            // Initialize PDF Writer
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Split the resume text into sections
            String[] sections = resumeText.split("\n\n");

            for (String section : sections) {
                if (section.trim().isEmpty()) continue; // Skip empty sections

                if (section.contains(":")) {  // Identify section titles
                    String[] parts = section.split(":", 2);
                    Text title = new Text(parts[0] + ": ").setBold().setFontSize(14);
                    Text content = new Text(parts[1]).setFontSize(12);
                    document.add(new Paragraph().add(title).add(content));
                } else {
                    document.add(new Paragraph(section).setFontSize(12));
                }

                // ✅ Add a line separator between sections
                document.add(new LineSeparator(new SolidLine()));
            }

            // Close document
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
