package com.mathbank.examsheet.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.mathbank.examsheet.dto.ExamSheetDetailDto;
import com.mathbank.examsheet.dto.ExamSheetProblemDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final ExamSheetService examSheetService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private BaseFont baseFont;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource fontResource = new ClassPathResource("fonts/NanumGothic.ttf");
            byte[] fontBytes = fontResource.getInputStream().readAllBytes();
            baseFont = BaseFont.createFont(
                    "NanumGothic.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    true, fontBytes, null
            );
        } catch (Exception e) {
            throw new RuntimeException("PDF 폰트 초기화 실패", e);
        }
    }

    public void generateExamPdf(Long examSheetId, HttpServletResponse response, boolean includeAnswer) throws Exception {
        ExamSheetDetailDto examSheet = examSheetService.getExamSheetDetail(examSheetId);
        List<ExamSheetProblemDto> problems = examSheet.getProblems();

        Font titleFont  = new Font(baseFont, 16, Font.BOLD);
        Font headerFont = new Font(baseFont, 12, Font.BOLD);
        Font bodyFont   = new Font(baseFont, 11);
        Font answerFont = new Font(baseFont, 11, Font.NORMAL, new Color(60, 60, 60));

        String fileName = includeAnswer
                ? "examsheet_" + examSheetId + "_answer.pdf"
                : "examsheet_" + examSheetId + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

        Document document = new Document(PageSize.A4, 60f, 60f, 70f, 50f);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new PageNumberEvent(baseFont));
        document.open();

        if (includeAnswer) {
            buildAnswerSheet(document, examSheet, problems, titleFont, headerFont, bodyFont, answerFont);
        } else {
            buildQuestionSheet(document, examSheet, problems, titleFont, headerFont, bodyFont);
        }

        document.close();
    }

    // ── 문제지 ────────────────────────────────────────────────────────────────

    private void buildQuestionSheet(Document document, ExamSheetDetailDto examSheet,
                                    List<ExamSheetProblemDto> problems,
                                    Font titleFont, Font headerFont, Font bodyFont) throws DocumentException {

        // 머리말 테이블
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100f);
        header.setSpacingAfter(6f);

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        header.addCell(noBorderCell(new Phrase(examSheet.getName(), titleFont), Element.ALIGN_LEFT));
        header.addCell(noBorderCell(new Phrase("날짜: " + dateStr, bodyFont), Element.ALIGN_RIGHT));
        header.addCell(noBorderCell(new Phrase("학년/반: _______________", bodyFont), Element.ALIGN_LEFT));
        header.addCell(noBorderCell(new Phrase("이름: _______________", bodyFont), Element.ALIGN_RIGHT));

        document.add(header);
        document.add(thickSeparator());

        // 문제 목록
        for (ExamSheetProblemDto p : problems) {
            Paragraph numTitle = buildParagraph(p.getSortOrder() + ".  " + p.getTitle(), headerFont);
            numTitle.setSpacingBefore(15f);
            numTitle.setSpacingAfter(6f);
            document.add(numTitle);

            if (p.getContent() != null && !p.getContent().isBlank()) {
                Paragraph content = buildParagraph(p.getContent(), bodyFont);
                content.setIndentationLeft(20f);
                content.setSpacingAfter(4f);
                document.add(content);
            }

            if (p.getImagePath() != null && !p.getImagePath().isBlank()) {
                addProblemImage(document, p.getImagePath());
            }
        }
    }

    private void addProblemImage(Document document, String imagePath) {
        try {
            String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
            java.io.File file = Path.of(uploadDir, filename).toFile();
            if (!file.exists()) return;

            Image image = Image.getInstance(file.getAbsolutePath());
            image.scaleToFit(360f, 260f);
            image.setIndentationLeft(20f);
            image.setSpacingBefore(4f);
            image.setSpacingAfter(8f);
            document.add(image);
        } catch (Exception e) {
            // 이미지를 읽을 수 없어도 나머지 PDF 생성은 계속 진행
        }
    }

    // ── 답안지 ────────────────────────────────────────────────────────────────

    private void buildAnswerSheet(Document document, ExamSheetDetailDto examSheet,
                                  List<ExamSheetProblemDto> problems,
                                  Font titleFont, Font headerFont, Font bodyFont, Font answerFont) throws DocumentException {

        Paragraph title = new Paragraph(examSheet.getName() + "  정답 및 해설", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(8f);
        document.add(title);
        document.add(thickSeparator());

        for (ExamSheetProblemDto p : problems) {
            Paragraph answerPara = buildParagraph(
                    p.getSortOrder() + "번.  정답: " + latexToText(p.getAnswer()), headerFont);
            answerPara.setSpacingBefore(12f);
            answerPara.setSpacingAfter(4f);
            document.add(answerPara);

            if (p.getExplanation() != null && !p.getExplanation().isBlank()) {
                Paragraph explPara = buildParagraph("해설: " + p.getExplanation(), answerFont);
                explPara.setIndentationLeft(20f);
                explPara.setSpacingAfter(6f);
                document.add(explPara);
            }

            document.add(thinSeparator());
        }
    }

    // ── 헬퍼 ──────────────────────────────────────────────────────────────────

    // latexToText 를 적용한 뒤 \n 으로 줄바꿈 처리
    private static Paragraph buildParagraph(String raw, Font font) {
        String text = latexToText(raw);
        Paragraph p = new Paragraph();
        p.setFont(font);
        String[] lines = text.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) p.add(Chunk.NEWLINE);
            p.add(new Chunk(lines[i], font));
        }
        return p;
    }

    private static String latexToText(String input) {
        if (input == null || input.isBlank()) return "";
        String s = input;

        // 블록 수식 $$...$$ → 내용만
        s = s.replaceAll("(?s)\\$\\$(.+?)\\$\\$", " $1 ");
        // 인라인 수식 $...$ → 내용만
        s = s.replaceAll("\\$([^$\\n]+)\\$", "$1");

        // LaTeX 줄바꿈 \\ → 개행
        s = s.replace("\\\\", "\n");

        // \left \right 제거
        s = s.replace("\\left(", "(").replace("\\left[", "[")
             .replace("\\left|", "|").replace("\\left.", "")
             .replace("\\right)", ")").replace("\\right]", "]")
             .replace("\\right|", "|").replace("\\right.", "");

        // 분수 \frac{a}{b}, \dfrac, \tfrac (중첩 처리 3회 반복)
        for (int i = 0; i < 3; i++) {
            s = s.replaceAll("\\\\[dt]?frac\\{([^{}]*)\\}\\{([^{}]*)\\}", "($1)/($2)");
        }

        // 루트 \sqrt[n]{x}, \sqrt{x}
        s = s.replaceAll("\\\\sqrt\\[([^\\]]+)\\]\\{([^{}]*)\\}", "$1√($2)");
        s = s.replaceAll("\\\\sqrt\\{([^{}]*)\\}", "√($1)");
        s = s.replace("\\sqrt", "√");

        // \text{...}
        s = s.replaceAll("\\\\text\\{([^{}]*)\\}", "$1");

        // 위첨자 ^{n} → ^n, 단순 숫자는 유니코드 변환
        s = s.replaceAll("\\^\\{([^{}]+)\\}", "^($1)");
        s = s.replace("^(2)", "²").replace("^(3)", "³").replace("^(4)", "⁴")
             .replace("^(5)", "⁵").replace("^(6)", "⁶").replace("^(7)", "⁷")
             .replace("^(8)", "⁸").replace("^(9)", "⁹").replace("^(0)", "⁰")
             .replace("^2", "²").replace("^3", "³").replace("^4", "⁴");

        // 아래첨자 _{n}
        s = s.replaceAll("_\\{([^{}]+)\\}", "_$1");

        // 그리스 문자
        s = s.replace("\\alpha", "α").replace("\\beta", "β")
             .replace("\\gamma", "γ").replace("\\Gamma", "Γ")
             .replace("\\delta", "δ").replace("\\Delta", "Δ")
             .replace("\\epsilon", "ε").replace("\\theta", "θ")
             .replace("\\lambda", "λ").replace("\\mu", "μ")
             .replace("\\pi", "π").replace("\\Pi", "Π")
             .replace("\\sigma", "σ").replace("\\Sigma", "Σ")
             .replace("\\omega", "ω").replace("\\phi", "φ");

        // 연산자·기호
        s = s.replace("\\leq", "≤").replace("\\geq", "≥")
             .replace("\\neq", "≠").replace("\\ne", "≠")
             .replace("\\times", "×").replace("\\div", "÷")
             .replace("\\pm", "±").replace("\\cdot", "·")
             .replace("\\cdots", "···").replace("\\infty", "∞")
             .replace("\\therefore", "∴").replace("\\because", "∵")
             .replace("\\cup", "∪").replace("\\cap", "∩")
             .replace("\\in", "∈").replace("\\subset", "⊂")
             .replace("\\circ", "∘");

        // 공백 명령
        s = s.replace("\\,", " ").replace("\\;", " ")
             .replace("\\:", " ").replace("\\ ", " ").replace("\\!", "");

        // 나머지 LaTeX 명령 일괄 제거 (\command)
        s = s.replaceAll("\\\\[a-zA-Z]+\\*?\\s?", "");

        // 중괄호 제거
        s = s.replace("{", "").replace("}", "");

        // 남은 $ 제거
        s = s.replace("$", "");

        // 다중 공백 정리
        s = s.replaceAll("[ \t]+", " ").trim();

        return s;
    }

    private static PdfPCell noBorderCell(Phrase phrase, int alignment) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4f);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private static Chunk thickSeparator() {
        LineSeparator sep = new LineSeparator(1f, 100f, new Color(80, 80, 80), Element.ALIGN_CENTER, -4f);
        return new Chunk(sep);
    }

    private static Chunk thinSeparator() {
        LineSeparator sep = new LineSeparator(0.5f, 100f, new Color(200, 200, 200), Element.ALIGN_CENTER, -2f);
        return new Chunk(sep);
    }

    // ── 페이지 번호 이벤트 ────────────────────────────────────────────────────

    private static class PageNumberEvent extends PdfPageEventHelper {

        private final BaseFont bf;
        private PdfTemplate totalTemplate;

        PageNumberEvent(BaseFont bf) {
            this.bf = bf;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalTemplate = writer.getDirectContent().createTemplate(30, 16);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb  = writer.getDirectContent();
            float midX         = document.getPageSize().getWidth() / 2;
            float bottomY      = document.getPageSize().getBottom() + 20f;
            float fontSize     = 9f;

            String current = writer.getPageNumber() + " / ";
            float currentW = bf.getWidthPoint(current, fontSize);

            cb.beginText();
            cb.setFontAndSize(bf, fontSize);
            cb.setTextMatrix(midX - currentW, bottomY);
            cb.showText(current);
            cb.endText();

            // 전체 페이지 수는 문서 닫힐 때 template에 채워짐
            cb.addTemplate(totalTemplate, midX, bottomY);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            totalTemplate.beginText();
            totalTemplate.setFontAndSize(bf, 9f);
            totalTemplate.setTextMatrix(0, 0);
            totalTemplate.showText(String.valueOf(writer.getPageNumber() - 1));
            totalTemplate.endText();
        }
    }
}
