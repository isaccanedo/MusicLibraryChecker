package ch.shaped.mp3.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.shaped.mp3.library.MP3LibraryAlbum;
import ch.shaped.mp3.library.MP3LibraryItem;

import com.google.common.io.Files;

public class LibraryReport {
	private static final Logger logger = LogManager.getLogger(LibraryReport.class);
	private static final String FORMAT = "%20s %10s %s\n";
	
	private Map<CheckReport, List<MP3LibraryAlbum>> check2album = new HashMap<CheckReport, List<MP3LibraryAlbum>>();
	private Map<MP3LibraryAlbum, List<CheckReport>> album2check = new HashMap<MP3LibraryAlbum, List<CheckReport>>();
	
	public void addReport(MP3LibraryAlbum album, CheckReport report) {
		if(!album2check.containsKey(album)) {
			this.album2check.put(album, new ArrayList<CheckReport>());
		}
		
		this.album2check.get(album).add(report);
		
		if(!check2album.containsKey(report)) {
			this.check2album.put(report, new ArrayList<MP3LibraryAlbum>());
		}
		
		this.check2album.get(report).add(album);
	}
	
	
	public Map<CheckReport, List<MP3LibraryAlbum>> getReportsByCheck() {
		return this.check2album;
	}
	
	
	public Map<MP3LibraryAlbum, List<CheckReport>> getReportsByAlbum() {
		return this.album2check;
	}
	
	
	public List<CheckReport> getReportForAlbum(MP3LibraryAlbum album) {
		if(!this.album2check.containsKey(album)) {
			return new ArrayList<CheckReport>();
		}
		
		return this.album2check.get(album);
	}
	
	
	public List<MP3LibraryAlbum> getReportForCheck(CheckReport check) {
		if(!this.check2album.containsKey(check)) {
			return new ArrayList<MP3LibraryAlbum>();
		}
		
		return this.check2album.get(check);
	}
	
	public Set<MP3LibraryAlbum> getAlbums() {
		return album2check.keySet();
	}
	
	public Set<CheckReport> getChecks() {
		return check2album.keySet();
	}
	
	public String getReport(boolean failOnly) {
		StringBuffer sb = new StringBuffer();
		
		for (MP3LibraryAlbum album : this.getAlbums()) {
			boolean printHead = true;

			List<CheckReport> reports = this.getReportForAlbum(album);
			for (CheckReport checkReport : reports) {
				List<MP3LibraryItem> itemsFail = checkReport.getFail();
				
				if(printHead && (itemsFail.size() > 0 || !failOnly)) {
					sb.append("\n"+album.getName()+"\n------------------------------------------------------------------\n");
					sb.append(String.format(FORMAT, "Check", "State", "File"));
					printHead = false;
				}
			
				for (MP3LibraryItem mp3LibraryItem : itemsFail) {
					sb.append(String.format(FORMAT, checkReport.getName(), "FAIL", mp3LibraryItem.getItem().getName()));
				}

				if(!failOnly) {
					List<MP3LibraryItem> itemsSuccess = checkReport.getSuccess();
					for (MP3LibraryItem mp3LibraryItem : itemsSuccess) {
						sb.append(String.format(FORMAT, checkReport.getName(), "SUCCESS", mp3LibraryItem.getItem().getName()));
					}
				}
			}
		}
		
		return sb.toString();
	}
	
	public void writeReport(File f) {
		if(f == null) {
			return;
		}
		
		try {
			Files.write(this.getReport(true), f, Charset.forName("UTF-8"));
		} catch(IOException e) {
			logger.fatal("Could not write report to file: "+f+"");
		}
	}
}
