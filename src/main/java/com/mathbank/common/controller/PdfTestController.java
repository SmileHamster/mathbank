package com.mathbank.common.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStream;

/**
 * PDF 출력 PoC — Week1 검증용. Week3 본구현 후 삭제 예정.
 */
@Controller
@RequestMapping("/pdf")
public class PdfTestController {

    @GetMapping("/test")
    public void pdfTest(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"test.pdf\"");

        // 한글 폰트 로드 (classpath → byte[] → BaseFont)
        ClassPathResource fontResource = new ClassPathResource("fonts/NanumGothic.ttf");
        byte[] fontBytes;
        try (InputStream is = fontResource.getInputStream()) {
            fontBytes = is.readAllBytes();
        }

        BaseFont baseFont = BaseFont.createFont("NanumGothic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
        Font titleFont  = new Font(baseFont, 18, Font.BOLD);
        Font bodyFont   = new Font(baseFont, 12);
        Font answerFont = new Font(baseFont, 12, Font.NORMAL, new java.awt.Color(0, 100, 0));

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // 제목
        Paragraph title = new Paragraph("수학 시험지 PoC", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // 구분선
        LineSeparator line = new LineSeparator();
        document.add(new Chunk(line));

        // 문제
        Paragraph q = new Paragraph();
        q.setSpacingBefore(16f);
        q.setSpacingAfter(8f);
        q.add(new Chunk("1.  다음 이차방정식을 풀어라.", bodyFont));
        document.add(q);

        Paragraph formula = new Paragraph("[수식]  x² + 2x + 1 = 0", bodyFont);
        formula.setIndentationLeft(20f);
        formula.setSpacingAfter(16f);
        document.add(formula);

        // 정답
        Paragraph answer = new Paragraph("정답:  x = -1  (중근)", answerFont);
        answer.setIndentationLeft(20f);
        document.add(answer);

        // 페이지 번호 (하단 중앙)
        PdfContentByte cb = writer.getDirectContent();
        float pageWidth  = document.getPageSize().getWidth();
        float pageBottom = document.getPageSize().getBottom() + 20f;
        cb.beginText();
        cb.setFontAndSize(baseFont, 10);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "1 / 1", pageWidth / 2, pageBottom, 0);
        cb.endText();

        document.close();
    }
}
