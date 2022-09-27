package ch.shaped.mp3.check.impl;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import ch.shaped.mp3.check.LibraryCheck;
import ch.shaped.mp3.library.MP3LibraryAlbum;
import ch.shaped.mp3.library.MP3LibraryItem;
import ch.shaped.mp3.report.CheckReport;
import ch.shaped.mp3.report.LibraryReport;

public class GarbageFileCheck implements LibraryCheck {
	public static final String NAME = "GarbageFileCheck";
	public static final String DESCRIPTION = "Only MP3 files are allowed in the album directories";

	private LibraryReport libraryReport;
	
	public void setLibraryReport(LibraryReport lr) {
		this.libraryReport = lr;
	}
	
	@Override
	public void run(MP3LibraryAlbum album) {
		CheckReport cr = new CheckReport(NAME, DESCRIPTION, album);
		
		if(album != null) {
			for (MP3LibraryItem item : album.getChilds()) {
				boolean success = false;
				
				File f = item.getItem();
				if(FilenameUtils.isExtension(f.getName(), "mp3")) {
					success = true;
				}
				cr.add(item, success);
			}
		}
		
		libraryReport.addReport(album, cr);
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
}
