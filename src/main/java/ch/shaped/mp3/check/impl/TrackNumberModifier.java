package ch.shaped.mp3.check.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.shaped.mp3.CheckLibrary;
import ch.shaped.mp3.check.LibraryCheck;
import ch.shaped.mp3.library.MP3LibraryAlbum;
import ch.shaped.mp3.library.MP3LibraryItem;
import ch.shaped.mp3.modifier.MP3Tagger;
import ch.shaped.mp3.report.CheckReport;
import ch.shaped.mp3.report.LibraryReport;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class TrackNumberModifier implements LibraryCheck {
	public static final String NAME = "TrackNumberModifier";
	public static final String DESCRIPTION = "Get the track number from filename and store in ID3v2 tag";

	private LibraryReport libraryReport;
	
	private static final Logger logger = LogManager.getLogger(CheckLibrary.class);
	
	public void setLibraryReport(LibraryReport lr) {
		this.libraryReport = lr;
	}
	
	@Override
	public void run(MP3LibraryAlbum album) {
		CheckReport cr = new CheckReport(NAME, DESCRIPTION, album);
		logger.info("Entering Album '"+album.getName()+"'");
		
		if(album != null) {
			MP3Tagger tagger = new MP3Tagger();
			
			for (MP3LibraryItem item : album.getChilds()) {
				boolean success = false;
				
				File sourceFile = item.getItem();
				if(FilenameUtils.isExtension(sourceFile.getName(), "mp3")) {
					try {
						Mp3File mp3file = new Mp3File(item.getItem());

						if(mp3file.hasId3v2Tag()) {
							ID3v2 id3v2Tag = mp3file.getId3v2Tag();
							if(id3v2Tag != null) {
								String track = id3v2Tag.getTrack();
								if(track == null || track.isEmpty()) {
									String[] filename = sourceFile.getName().split(" ");
									String trackNr = filename[0].replaceAll("^0+", "");
									if(trackNr.matches("[0-9]+")) {
										Map<String, String> changeTags = new HashMap<String, String>();
										changeTags.put(MP3Tagger.TRACK, trackNr);
										tagger.retag(item.getItem(), changeTags);
									}
								}
							}
						}
					} catch (UnsupportedTagException|InvalidDataException|IOException e) {
						logger.debug("Error while running TrackNumberModifier. ", e);
					}
				}
				cr.add(item, success);
			}
		}
		logger.info("Leaving Album '"+album.getName()+"'");
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
