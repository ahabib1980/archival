/**
 * Copyright(C) 2020 Interactive Health Solutions, Pvt. Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
 * You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
 * Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 * Contributors: Tahira Niazi
 */
package org.openmrs.module.archival.web.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmrs.User;
import org.openmrs.module.archival.web.dto.PatientDto;
import org.springframework.core.io.ByteArrayResource;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author tahira.niazi@ihsinformatics.com
 */
public class PdfReport {
	
	public static ByteArrayInputStream generateReport(List<PatientDto> patientDtos, User user) {
		
		Document document = new Document(PageSize.A4, 80f, 80f, 50f, 0f);
		float lineSpacing = 12f;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			
			PdfPTable table = new PdfPTable(5);
			table.setWidthPercentage(80);
			table.setWidths(new int[] { 2, 3, 4, 4, 4 });
			
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11f);
			Font tableDataFont = FontFactory.getFont(FontFactory.HELVETICA, 11f);
			
			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("S No.", headerFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("Patient ID", headerFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("Name", headerFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("Gender", headerFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(hcell);
			
			hcell = new PdfPCell(new Phrase("DOB", headerFont));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(hcell);
			
			int serialNumber = 1;
			
			for (PatientDto patientDto : patientDtos) {
				
				PdfPCell cell;
				
				cell = new PdfPCell(new Phrase(String.valueOf(serialNumber), tableDataFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase(patientDto.getIdentifier(), tableDataFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase(patientDto.getPatientName(), tableDataFont));
				cell.setPaddingLeft(5);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase(patientDto.getGender(), tableDataFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPaddingRight(5);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase(patientDto.getDob(), tableDataFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPaddingRight(5);
				table.addCell(cell);
				
				serialNumber++;
			}
			
			PdfWriter.getInstance(document, out);
			document.open();
			String titleHeading = "Patient Archival Summary Report";
			Paragraph headingPara = new Paragraph(new Phrase(lineSpacing, titleHeading, FontFactory.getFont(
			    FontFactory.HELVETICA_BOLD, 18f)));
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
			String paraString = "Following were the patients that were archived in OpenMRS by "
			        + user.getPerson().getPersonName().getFullName() + " on " + dateFormat.format(date) + ".";
			Paragraph detailPara = new Paragraph(new Phrase(lineSpacing, paraString, FontFactory.getFont(
			    FontFactory.HELVETICA, 12f)));
			document.add(headingPara);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(detailPara);
			document.add(Chunk.NEWLINE);
			document.add(table);
			document.close();
			
		}
		catch (DocumentException ex) {
			
			Logger.getLogger(PdfReport.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
		//		InputStream targetStream = new ByteArrayInputStream(out.toByteArray());
		//		return resource;
		return new ByteArrayInputStream(out.toByteArray());
		
	}
	
}
