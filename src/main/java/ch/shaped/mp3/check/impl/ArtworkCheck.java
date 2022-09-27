package ch.shaped.mp3.check.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.shaped.mp3.check.AbstractLibraryCheck;
import ch.shaped.mp3.check.CheckState;
import ch.shaped.mp3.check.LibraryCheck;
import ch.shaped.mp3.library.MP3LibraryAlbum;
import ch.shaped.mp3.library.MP3LibraryItem;
import ch.shaped.mp3.report.CheckReport;
import ch.shaped.mp3.report.LibraryReport;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class ArtworkCheck implements LibraryCheck {
	public static final String NAME = "ArtworkCheck";
	public static final String DESCRIPTION = "Each item in the album must have the ID3v2 artwork tag set";
	
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
				if(FilenameUtils.isExtension(f.getName().toLowerCase(), "mp3")) {
					try {
						Mp3File mp3file = new Mp3File(item.getItem());

						if(mp3file.hasId3v2Tag()) {
							ID3v2 id3v2Tag = mp3file.getId3v2Tag();
							if(id3v2Tag != null) {
								byte[] albumImageData = id3v2Tag.getAlbumImage();
								if(albumImageData != null && albumImageData.length > 0) {
									success = true;
								}
							}
						}
					} catch (UnsupportedTagException|InvalidDataException|IOException e) {
						/* do not care */
					}
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
