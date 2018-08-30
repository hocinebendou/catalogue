package bio.tech.ystr.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Component
public class PdfGeneratorUtil extends PdfPageEventHelper {

    private String pdfName;
    private int insertedRows;
    private int updatedRows;
    private int notInsertedRows;
    private Set<String> studiesNotFound;
    private PdfTemplate t;
    private Image total;

    public PdfGeneratorUtil() {
        this.pdfName = "";
        this.insertedRows = 0;
        this.updatedRows = 0;
        this.notInsertedRows = 0;
        this.studiesNotFound = new HashSet<>();
    }

    public void onOpenDocument(PdfWriter writer, Document document) {
        t = writer.getDirectContent().createTemplate(30, 16);
        try {
            total = Image.getInstance(t);
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        addHeader(writer);
        addFooter(writer);
    }

    private void addHeader(PdfWriter writer) {
        PdfPTable header = new PdfPTable(2);
        try {
            header.setWidths(new int[]{2, 24});
            header.setTotalWidth(527);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(40);
            header.getDefaultCell().setBorder(Rectangle.BOTTOM);
            header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

            // add image
            Image logo = Image.getInstance(new DefaultResourceLoader()
                    .getResource("classpath:/static/img/login.png").getURL());
            header.addCell(logo);

            // add text
            PdfPCell text = new PdfPCell();
            text.setPaddingBottom(15);
            text.setPaddingLeft(10);
            text.setBorder(Rectangle.BOTTOM);
            text.setBorderColor(BaseColor.LIGHT_GRAY);
            text.addElement(new Phrase("H3Africa Studies catalogue",
                    new Font(Font.FontFamily.HELVETICA, 12)));
            text.addElement(new Phrase("http://h3acatalog.sanbi.ac.za", new Font(Font.FontFamily.HELVETICA, 8)));
            header.addCell(text);

            // write content
            header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
        }catch (DocumentException | IOException de) {
            throw new ExceptionConverter(de);
        }
    }

    private void addFooter(PdfWriter writer) {
        PdfPTable footer = new PdfPTable(3);
        try {
            // set defaults
            footer.setWidths(new int[]{24, 2, 1});
            footer.setTotalWidth(527);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setFixedHeight(40);
            footer.getDefaultCell().setBorder(Rectangle.TOP);
            footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

            // add copyright
            footer.addCell(new Phrase("\u00A9 sanbi.ac.za",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

            // add current page count
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()),
                    new Font(Font.FontFamily.HELVETICA, 8)));

            // add placeholder for total page count
            PdfPCell totalPageCount = new PdfPCell(total);
            totalPageCount.setBorder(Rectangle.TOP);
            totalPageCount.setBorderColor(BaseColor.LIGHT_GRAY);
            footer.addCell(totalPageCount);

            // write page
            PdfContentByte canvas = writer.getDirectContent();
            canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
            footer.writeSelectedRows(0, -1, 34, 50, canvas);
            canvas.endMarkedContentSequence();
        }catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    private String constructStudiesString () {
        StringBuilder sb = new StringBuilder("");
        Iterator<String> iterator = this.studiesNotFound.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext())
                sb.append(", ");
        }

        return sb.toString();
    }

    public String createPdf() throws Exception {
        FileOutputStream os = null;
        String pdfFilePath = "";
        try {
            final File outputFile = File.createTempFile(this.pdfName, ".pdf");
            pdfFilePath = outputFile.getPath();
            os = new FileOutputStream(outputFile);
            Document document = new Document(PageSize.A4, 36, 36, 90, 36);
            PdfWriter writer = PdfWriter.getInstance(document, os);

            PdfGeneratorUtil event = new PdfGeneratorUtil();
            writer.setPageEvent(event);

            document.open();
            document.add(new Paragraph(String.format("Number of rows inserted in the database: %d",
                    this.insertedRows)));
            document.add(new Paragraph(String.format("Number of rows updated in the database: %d",
                    this.updatedRows)));
            Paragraph paragraph = new Paragraph(String.format("Number of rows not inserted in the database: %d",
                    this.notInsertedRows));
            paragraph.setSpacingAfter(10f);
            document.add(paragraph);

            PdfPTable table = new PdfPTable(1);
            table.setTotalWidth(527);
            table.setLockedWidth(true);
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(1);
            header.setPhrase(new Phrase("Studies Not Found"));
            header.setPadding(5f);
            header.setPaddingLeft(10f);
            table.addCell(header);
            table.addCell(constructStudiesString());
            document.add(table);
            document.close();
        }finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {/*ignore*/}
            }
        }
        return pdfFilePath;
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
                new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)),
                totalWidth, 6, 0);
    }

    public void setPdfName (String pdfName) { this.pdfName = pdfName; }

    public void incInsertedRows () {
        this.insertedRows ++;
    }

    public void incUpdatedRows () { this.updatedRows ++; }

    public void incNotInsertedRows () {
        this.notInsertedRows ++;
    }

    public void addNotFoundStudy (String study) { this.studiesNotFound.add(study); }
}
