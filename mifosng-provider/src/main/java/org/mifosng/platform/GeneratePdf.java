package org.mifosng.platform;

import java.io.FileOutputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class GeneratePdf {

public static void main(String[] args) throws Exception {

Class.forName("com.mysql.jdbc.Driver");
Connection conn = (Connection) DriverManager.getConnection(
"jdbc:mysql://localhost:3306/mifostenant-default", "root",
"mysql");
Statement stmt = (Statement) conn.createStatement();
ResultSet rs = stmt
.executeQuery("SELECT b.*,c.*,mc.*,ca.*  FROM bill_master b,company_address_detail ca,m_client mc LEFT JOIN client_address c ON c.client_id = mc.id   WHERE b.client_id = mc.id");
// ResultSet rs1 = stmt.executeQuery("select * from bill_details");

for(int i=0;rs.next();i++)
{
Document document = new Document();

PdfWriter writer = PdfWriter.getInstance(document,
new FileOutputStream("tableTilePDF.pdf"));
document.open();
PdfContentByte pdfContentByte = writer.getDirectContent();
Font b = new Font(Font.BOLD + Font.BOLD,8);
Font b1 = new Font(Font.BOLD + Font.UNDERLINE + Font.BOLDITALIC+Font.TIMES_ROMAN,8);


pdfContentByte.beginText();

PdfPTable table = new PdfPTable(11);
table.setWidthPercentage(100);

PdfPCell cell1 = new PdfPCell(
(new Paragraph("Bill Invoice", FontFactory.getFont(FontFactory.HELVETICA,12, Font.BOLD))));
cell1.setColspan(11);
cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
cell1.setPadding(10.0f);
table.addCell(cell1);
PdfPCell cell = new PdfPCell();
cell.setColspan(2);
Paragraph para = new Paragraph("Name :", b1);
Paragraph addr = new Paragraph("Address :", b);
Paragraph branch = new Paragraph("Branch :", b);
branch.setSpacingBefore(10);

cell.addElement(para);
cell.addElement(addr);
cell.addElement(branch);
cell.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell);
PdfPCell cell0 = new PdfPCell();
Paragraph add0 = new Paragraph("", b);
Paragraph add1 = new Paragraph(""+rs.getString("country")+","+rs.getString("street"), b);
add1.setSpacingBefore(10);
Paragraph add2 = new Paragraph(","+rs.getString("state")+"-"+rs.getString("zip"), b);
cell0.setColspan(4);
cell0.disableBorderSide(PdfPCell.LEFT);
cell0.addElement(add0);
cell0.addElement(add1);
cell0.addElement(add2);
table.addCell(cell0);
//
 Image image = Image.getInstance("companyLogo.jpg");
 image.scaleAbsolute(90,90);
PdfPCell cell2 = new PdfPCell();
 cell2.addElement(image);
cell2.disableBorderSide(PdfPCell.TOP);
cell2.disableBorderSide(PdfPCell.BOTTOM);
cell2.disableBorderSide(PdfPCell.LEFT);
cell2.disableBorderSide(PdfPCell.RIGHT);
cell2.setColspan(2);
table.addCell(cell2);
PdfPCell cell02 = new PdfPCell();
Paragraph addr1 = new Paragraph("Hugo Technologies LLP",
FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD,
new CMYKColor(0, 255, 255,17)));
Paragraph addr2 = new Paragraph("# 501, Sai Balaji Cubicles,", b);
Paragraph addr3 = new Paragraph("Raghavendra Society, Kondapur,", b);
Paragraph addr4 = new Paragraph(" Hyderabad - 500 084, AP, India.",b);
Paragraph addr5 = new Paragraph(" Tel: +91-40-65141823",b);
Paragraph addr6 = new Paragraph("www.hugotechnologies.com",b);
cell02.addElement(addr1);
cell02.addElement(addr2);
cell02.addElement(addr3);
cell02.addElement(addr4);
cell02.addElement(addr5);
cell02.addElement(addr6);

cell02.disableBorderSide(PdfPCell.TOP);
cell02.disableBorderSide(PdfPCell.BOTTOM);
cell02.disableBorderSide(PdfPCell.LEFT);
cell2.disableBorderSide(PdfPCell.RIGHT);
cell02.setColspan(3);
table.addCell(cell02);
PdfPCell cell3 = new PdfPCell();
// cell3.setPadding (1.0f);
Paragraph BillId = new Paragraph("Client Id: " + rs.getInt("Client_id"), b);
cell3.setColspan(6);
cell3.addElement(BillId);
cell3.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell3);
PdfPCell cell12 = new PdfPCell();
Paragraph billNo = new Paragraph("BillNo:"
+ rs.getString("bill_no"), b);
// billNo.setIndentationLeft(280);
Paragraph billDate = new Paragraph("Bill Date:"
+ JdbcSupport.getLocalDate(rs, "bill_date"), b);
// billDate.setIndentationLeft(280);
Paragraph BillPeriod = new Paragraph("Bill Period:"
+ rs.getString("bill_Period"), b);
// BillPeriod.setIndentationLeft(280);
Paragraph dueDate = new Paragraph("Due Date:"
+ JdbcSupport.getLocalDate(rs, "due_date"), b);
// dueDate.setIndentationLeft(280);

// cell12.disableBorderSide(PdfPCell.TOP);
// cell12.disableBorderSide(PdfPCell.BOTTOM);
cell12.disableBorderSide(PdfPCell.LEFT);
// cell12.disableBorderSide(PdfPCell.RIGHT);
cell12.addElement(billNo);
cell12.addElement(billDate);
cell12.addElement(BillPeriod);
cell12.setColspan(5);
cell12.addElement(dueDate);
table.addCell(cell12);
PdfPCell cell4 = new PdfPCell();

Paragraph previousbal = new Paragraph("Previous Balance", b);
Paragraph previousamount = new Paragraph(""
+ rs.getDouble("previous_balance"), b);
cell4.setColspan(2);
cell4.addElement(previousbal);
cell4.addElement(previousamount);
cell4.disableBorderSide(PdfPCell.TOP);
// cell5.disableBorderSide(PdfPCell.BOTTOM);
// cell4.disableBorderSide(PdfPCell.LEFT);
//cell4.disableBorderSide(PdfPCell.RIGHT);

table.addCell(cell4);
pdfContentByte.setTextMatrix(390, 405);

PdfPCell cell5 = new PdfPCell();
Paragraph adjstment = new Paragraph("Adjustment Amount", b);
Paragraph adjstmentamount = new Paragraph(""
+ rs.getDouble("adjustment_amount"), b);
cell5.setColspan(2);
cell5.addElement(adjstment);
cell5.addElement(adjstmentamount);
cell5.disableBorderSide(PdfPCell.TOP);
// cell5.disableBorderSide(PdfPCell.BOTTOM);
cell5.disableBorderSide(PdfPCell.LEFT);
//cell5.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell5);

PdfPCell cell6 = new PdfPCell();
Paragraph paid_amount = new Paragraph("Payments", b);
Paragraph amount = new Paragraph("" + rs.getDouble("paid_amount"),
b);
cell6.setColspan(2);
cell6.addElement(paid_amount);
cell6.addElement(amount);
cell6.disableBorderSide(PdfPCell.TOP);
// cell5.disableBorderSide(PdfPCell.BOTTOM);
cell6.disableBorderSide(PdfPCell.LEFT);
//cell6.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell6);





PdfPCell cell7 = new PdfPCell();
Paragraph charge_amount = new Paragraph("Charge Amount", b);
Paragraph chargeamount = new Paragraph(""
+ rs.getDouble("charges_amount"), b);
cell7.setColspan(2);
cell7.addElement(charge_amount);
cell7.addElement(chargeamount);

cell7.disableBorderSide(PdfPCell.TOP);
// cell5.disableBorderSide(PdfPCell.BOTTOM);
cell7.disableBorderSide(PdfPCell.LEFT);
// cell7.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell7);

PdfPCell cell8 = new PdfPCell();
Paragraph due_amount = new Paragraph("Due Amount", b);
Paragraph dueamount = new Paragraph(
"" + rs.getDouble("due_amount"), b);
cell8.setColspan(3);
cell8.addElement(due_amount);
cell8.addElement(dueamount);

cell8.disableBorderSide(PdfPCell.TOP);
// cell5.disableBorderSide(PdfPCell.BOTTOM);
cell8.disableBorderSide(PdfPCell.LEFT);
// cell8.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell8);



PdfPCell cell9 = new PdfPCell();
cell9.setColspan(6);
Paragraph billDetails = new Paragraph("Current Bill Details", b);
cell9.setPadding(10.0f);
cell9.setPaddingLeft(100.0f);
cell9.addElement(billDetails);
cell9.disableBorderSide(PdfPCell.TOP);
cell9.disableBorderSide(PdfPCell.BOTTOM);
cell9.disableBorderSide(PdfPCell.LEFT);
cell9.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell9);

PdfPCell cell10 = new PdfPCell();
cell10.setColspan(5);
Paragraph message = new Paragraph("Promotional Message", b);
cell10.setPadding(10.0f);
cell10.setPaddingLeft(100.0f);
cell10.addElement(message);
cell10.disableBorderSide(PdfPCell.TOP);
cell10.disableBorderSide(PdfPCell.BOTTOM);
cell10.disableBorderSide(PdfPCell.LEFT);
cell10.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell10);


PdfPCell cell23 = new PdfPCell();
cell23.setColspan(1);
Paragraph ID = new Paragraph("id", b);


//cell10.disableBorderSide(PdfPCell.TOP);
//cell10.disableBorderSide(PdfPCell.BOTTOM);
//cell23.disableBorderSide(PdfPCell.LEFT);
cell23.disableBorderSide(PdfPCell.RIGHT);


PdfPCell cell26 = new PdfPCell();
cell26.setColspan(3);
Paragraph charge = new Paragraph("Transactions", b);

Paragraph charge2 = new Paragraph("PAYMENT - CREDIT CARD", b);

cell26.addElement(charge);
cell26.addElement(charge2);
cell26.addElement(charge2);
cell23.addElement(charge2);
//cell10.disableBorderSide(PdfPCell.TOP);
//cell10.disableBorderSide(PdfPCell.BOTTOM);
cell26.disableBorderSide(PdfPCell.LEFT);
cell26.disableBorderSide(PdfPCell.RIGHT);
//cell23.enableBorderSide(PdfPCell.BOTTOM);
cell23.addElement(ID);
table.addCell(cell23);
table.addCell(cell26);
PdfPCell cell27 = new PdfPCell();
cell27.setColspan(0);
Paragraph Date = new Paragraph("Date", b);

cell27.addElement(Date);
//cell10.disableBorderSide(PdfPCell.TOP);
//cell10.disableBorderSide(PdfPCell.BOTTOM);
cell27.disableBorderSide(PdfPCell.LEFT);
cell27.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell27);

PdfPCell cell28 = new PdfPCell();
cell28.setColspan(1);
Paragraph Amount = new Paragraph("Amount", b);

cell28.addElement(Amount);
//cell10.disableBorderSide(PdfPCell.TOP);
//cell10.disableBorderSide(PdfPCell.BOTTOM);
cell28.disableBorderSide(PdfPCell.LEFT);
cell28.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell28);


cell28.addElement(Amount);
//cell10.disableBorderSide(PdfPCell.TOP);
//cell10.disableBorderSide(PdfPCell.BOTTOM);
cell28.disableBorderSide(PdfPCell.LEFT);
cell28.disableBorderSide(PdfPCell.RIGHT);
table.addCell(cell28);


PdfPCell cell24 = new PdfPCell();
cell24.setColspan(0);
cell24.disableBorderSide(PdfPCell.TOP);
cell24.disableBorderSide(PdfPCell.BOTTOM);
table.addCell(cell24);
PdfPCell cell25 = new PdfPCell();
Paragraph charge1 = new Paragraph("transaction",b);

cell25.setColspan(6);
cell25.setPadding(70f);
cell25.addElement(charge1);
table.addCell(cell25);






pdfContentByte.endText();
document.add(table);
document.close();

Runtime.getRuntime().exec(
"rundll32 url.dll,FileProtocolHandler tableTilePDF.pdf");

}
rs.close();

// rs1.close();




}
}