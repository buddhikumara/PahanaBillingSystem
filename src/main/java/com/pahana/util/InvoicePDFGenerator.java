//public class InvoicePDFGenerator {
//    public static String generateInvoice(Bill bill) {
//        String fileName = "invoice_" + bill.getBillId() + ".pdf";
//        String filePath = "/path/to/your/project/invoices/" + fileName;
//
//        try {
//            Document document = new Document();
//            PdfWriter.getInstance(document, new FileOutputStream(filePath));
//            document.open();
//            document.add(new Paragraph("Invoice #: " + bill.getBillId()));
//            document.add(new Paragraph("Date: " + bill.getBillDate()));
//            document.add(new Paragraph("Payment: " + bill.getPaymentMethod()));
//            document.add(new Paragraph(" "));
//
//            PdfPTable table = new PdfPTable(5);
//            table.addCell("Item");
//            table.addCell("Description");
//            table.addCell("Qty");
//            table.addCell("Price");
//            table.addCell("Total");
//
//            for (BillItem item : bill.getItems()) {
//                table.addCell(item.getItemName());
//                table.addCell(item.getDescription());
//                table.addCell(String.valueOf(item.getQuantity()));
//                table.addCell(String.format("%.2f", item.getPrice()));
//                table.addCell(String.format("%.2f", item.getTotal()));
//            }
//
//            document.add(table);
//            document.add(new Paragraph(" "));
//            document.add(new Paragraph("Grand Total: Rs. " + bill.getGrandTotal()));
//            document.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "invoices/" + fileName; // relative path
//    }
//}
