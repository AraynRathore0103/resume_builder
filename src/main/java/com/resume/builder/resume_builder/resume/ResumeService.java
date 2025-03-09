package com.resume.builder.resume_builder.resume;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.resume.builder.resume_builder.model.ResumeRequest;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ResumeService {

    public ByteArrayOutputStream createResumePDF(ResumeRequest resumeRequest) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Name & Contact
        document.add(new Paragraph(resumeRequest.getFullName())
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("📞 " + resumeRequest.getPhone() + " | ✉️ " + resumeRequest.getEmail() +
                " | 🔗 " + resumeRequest.getLinkedIn()).setTextAlignment(TextAlignment.CENTER));

        document.add(new LineSeparator(new SolidLine()));

        // Sections
        addSection(document, "Professional Summary", resumeRequest.getSummary());
        addBulletList(document, "Technical Skills", resumeRequest.getTechnicalSkills());
        addBulletList(document, "Soft Skills", resumeRequest.getSoftSkills());
        addSection(document, "Education", resumeRequest.getEducation());

        for (ResumeRequest.WorkExperience experience : resumeRequest.getWorkExperience()) {
            addWorkExperience(document, experience.getJobTitle(), experience.getResponsibilities());
        }

        addBulletList(document, "Projects", resumeRequest.getProjects());
        addBulletList(document, "Certifications & Courses", resumeRequest.getCertifications());
        addBulletList(document, "Awards & Achievements", resumeRequest.getAwards());
        addBulletList(document, "Languages", resumeRequest.getLanguages());

        document.close();
        return outputStream;
    }

    private void addSection(Document document, String title, String content) {
        document.add(new Paragraph(title).setBold().setFontSize(14));
        document.add(new Paragraph(content).setFontSize(12));
        document.add(new LineSeparator(new SolidLine()));
    }

    private void addBulletList(Document document, String title, List<String> items) {
        document.add(new Paragraph(title).setBold().setFontSize(14));
        
        com.itextpdf.layout.element.List bulletList = new com.itextpdf.layout.element.List()
                .setSymbolIndent(12)
                .setListSymbol("• ");

        for (String item : items) {
            bulletList.add(new ListItem(item));
        }

        document.add(bulletList);
        document.add(new LineSeparator(new SolidLine()));
    }

    private void addWorkExperience(Document document, String jobTitle, List<String> responsibilities) {
        document.add(new Paragraph(jobTitle).setBold().setFontSize(12));

        com.itextpdf.layout.element.List experienceList = new com.itextpdf.layout.element.List()
                .setSymbolIndent(12)
                .setListSymbol("✓ ");

        for (String point : responsibilities) {
            experienceList.add(new ListItem(point));
        }

        document.add(experienceList);
        document.add(new LineSeparator(new SolidLine()));
    }

}
