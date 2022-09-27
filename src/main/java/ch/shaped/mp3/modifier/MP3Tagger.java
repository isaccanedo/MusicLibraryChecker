package ch.shaped.mp3.modifier;
import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mpatric.mp3agic.BaseException;
import com.mpatric.mp3agic.BufferTools;
import com.mpatric.mp3agic.EncodedText;
import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v23Tag;
import com.mpatric.mp3agic.Mp3File;

public class MP3Tagger {
	private static final String RETAG_EXTENSION = ".retag";
	private static final String BACKUP_EXTENSION = ".bak";
	private static final int CUSTOM_TAG_WARNING_THRESHOLD = 1024;
	
	public static final String TRACK = "track";
	public static final String ARTIST = "artist";
	public static final String TITLE = "title";
	public static final String ALBUM = "album";
	public static final String YEAR = "year";
	public static final String GENRE = "genre";
	public static final String COMMENT = "comment";
	public static final String COMPOSER = "composer";
	public static final String ORIGINALARTISTS = "originalArtists";
	public static final String COPYRIGHT = "copyright";
	public static final String URL = "url";
	public static final String ENCODER = "encoder";
	public static final String ALBUMIMAGE = "albumImage";

	private static final Logger logger = LogManager.getLogger(MP3Tagger.class);
	
	private boolean keepCustomTag = true;
		
	/**
	 * Retag a given MP3 with given values from tags Map
	 * Only the following tags are supported for retagging: Track, Title, Artist, Album, Year, Comment, Genre, Composer, OrigianlArtists, Encoder, Url, Copyright
	 * Tags not in the tags map will be kept from the old tag
	 * 
	 * Deleting of tags is not supported.
	 * 
	 * @param source The MP3 file
	 * @param tags The key value map containing the new tags. Key is one of the supported fields and value is the value to be set.
	 */
	public void retag(File source, Map<String, String> tags) {
		if(tags == null || tags.size() == 0) {
			return;
		}
		
		// this might stay null. 
		String customTag = null;
		String filename = source.getAbsolutePath();
		logger.debug("Tagging MP3File '"+source.getName()+"'");
		
		Mp3File mp3file = null;
		try {
			mp3file = new Mp3File(source);
			boolean hasId3v1Tag = mp3file.hasId3v1Tag();
			boolean hasId3v2Tag = mp3file.hasId3v2Tag();
			
			if (! hasId3v1Tag && ! hasId3v2Tag) {
				logger.error("ERROR processing '" + source.getName() + "' - no ID3 tags found");
			} else {
				boolean hasCustomTag = mp3file.hasCustomTag();
				if (hasCustomTag && mp3file.getCustomTag().length > CUSTOM_TAG_WARNING_THRESHOLD) {
					logger.warn("WARNING processing '" + source.getName() + "' - custom tag is " + mp3file.getCustomTag().length + " bytes, potential corrupt file");
				}
				
				updateId3Tags(mp3file, tags);
				updateCustomTag(mp3file, customTag);
				mp3file.save(mp3file.getFilename() + RETAG_EXTENSION);
				renameFiles(filename);
			}
			
		} catch (BaseException e) {
			logger.error("ERROR processing '" + source.getName() + "' - " + e.getDetailedMessage(), e);
			if (mp3file != null) {
				new File(mp3file.getFilename() + RETAG_EXTENSION).delete();
			}
		} catch (Exception e) {
			logger.error("ERROR processing '" + source.getName() + "' - " + e.getMessage(), e);
			if (mp3file != null) {
				new File(mp3file.getFilename() + RETAG_EXTENSION).delete();
			}
		}
	}
	
	private void updateId3Tags(Mp3File mp3file, Map<String, String> tags) {
		ID3Wrapper oldId3Wrapper = new ID3Wrapper(mp3file.getId3v1Tag(), mp3file.getId3v2Tag());
		ID3Wrapper newId3Wrapper = new ID3Wrapper(new ID3v1Tag(), new ID3v23Tag());
		StringBuffer sb = new StringBuffer();
		
		/* track */
		sb.append("Track '"+null2EmptyString(cleanTrack(oldId3Wrapper.getTrack()))+"'");
		if(tags.containsKey(TRACK)) {
			sb.append(" => '"+tags.get(TRACK)+"'");
			newId3Wrapper.setTrack(tags.get(TRACK));
		} else {
			newId3Wrapper.setTrack(cleanTrack(oldId3Wrapper.getTrack()));
		}
		logger.debug(sb.toString());
		sb.setLength(0);
		
		/* Title */
		sb.append("Title '"+null2EmptyString(trimField(oldId3Wrapper.getTitle()))+"'");
		if(tags.containsKey(TITLE)) {
			sb.append(" => '"+tags.get(TITLE)+"'");
			newId3Wrapper.setTitle(tags.get(TITLE));
		} else {
			newId3Wrapper.setTitle(trimField(oldId3Wrapper.getTitle()));
		}
		logger.debug(sb.toString());
		sb.setLength(0);
		
		/* Artist */
		sb.append("Artist '"+null2EmptyString(trimField(oldId3Wrapper.getArtist()))+"'");
		if(tags.containsKey(ARTIST)) {
			sb.append(" => '"+tags.get(ARTIST)+"'");
			newId3Wrapper.setArtist(tags.get(ARTIST));
		} else {
			newId3Wrapper.setArtist(trimField(oldId3Wrapper.getArtist()));
		}
		logger.debug(sb.toString());
		sb.setLength(0);
		
		/* Album */
		sb.append("Album '"+null2EmptyString(trimField(oldId3Wrapper.getAlbum()))+"'");
		if(tags.containsKey(ALBUM)) {
			sb.append(" => '"+tags.get(ALBUM)+"'");
			newId3Wrapper.setAlbum(tags.get(ALBUM));
		} else {
			newId3Wrapper.setAlbum(trimField(oldId3Wrapper.getAlbum()));
		}
		logger.debug(sb.toString());
		sb.setLength(0);
		
		
		/* Year */
		sb.append("Year '"+null2EmptyString(trimField(oldId3Wrapper.getYear()))+"'");
		if(tags.containsKey(YEAR)) {
			sb.append(" => '"+tags.get(YEAR)+"'");
			newId3Wrapper.setYear(tags.get(YEAR));
		} else {
			newId3Wrapper.setYear(trimField(oldId3Wrapper.getYear()));
		}
		logger.debug(sb.toString());
		sb.setLength(0);
		
		
		/* Genre */
		sb.append("Genre '"+oldId3Wrapper.getGenre()+"'");
		if(tags.containsKey(GENRE)) {
			try {
				sb.append(" => '"+tags.get(GENRE)+"'");
				int gInt = Integer.parseInt(tags.get(GENRE));
				newId3Wrapper.setGenre(gInt);
			} catch(NumberFormatException e) {
				newId3Wrapper.setGenre(oldId3Wrapper.getGenre());
			}
		} else {
			newId3Wrapper.setGenre(oldId3Wrapper.getGenre());
		}
		logger.debug(sb.toString());
		sb.setLength(0);
		
		
		/* Comment */
		sb.append("Comment '"+null2EmptyString(trimField(oldId3Wrapper.getComment()))+"'");
		if(tags.containsKey(COMMENT)) {
			sb.append(" => '"+tags.get(COMMENT)+"'");
			newId3Wrapper.setComment(tags.get(COMMENT));
		} else {
			newId3Wrapper.setComment(trimField(oldId3Wrapper.getComment()));
		}
		logger.debug(sb.toString());
		sb.setLength(0);
		
		
		/* Composer */
		if(tags.containsKey(COMPOSER)) {
			newId3Wrapper.setComposer(tags.get(COMPOSER));
		} else {
			newId3Wrapper.setComposer(trimField(oldId3Wrapper.getComposer()));
		}
		
		
		/* Original Artists */
		if(tags.containsKey(ORIGINALARTISTS)) {
			newId3Wrapper.setOriginalArtist(tags.get(ORIGINALARTISTS));
		} else {
			newId3Wrapper.setOriginalArtist(trimField(oldId3Wrapper.getOriginalArtist()));
		}

		
		/* Copyright */
		if(tags.containsKey(COPYRIGHT)) {
			newId3Wrapper.setCopyright(tags.get(COPYRIGHT));
		} else {
			newId3Wrapper.setCopyright(trimField(oldId3Wrapper.getCopyright()));
		}
		
		
		/* Url */
		if(tags.containsKey(URL)) {
			newId3Wrapper.setUrl(tags.get(URL));
		} else {
			newId3Wrapper.setUrl(trimField(oldId3Wrapper.getUrl()));
		}
	

		/* Encoder */
		if(tags.containsKey(ENCODER)) {
			newId3Wrapper.setEncoder(tags.get(ENCODER));
		} else {
			newId3Wrapper.setEncoder(trimField(oldId3Wrapper.getEncoder()));
		}
		
		
		newId3Wrapper.setAlbumImage(oldId3Wrapper.getAlbumImage(), cleanImageMimeType(oldId3Wrapper.getAlbumImageMimeType()));
		newId3Wrapper.getId3v2Tag().setPadding(true);
		
		mp3file.setId3v1Tag(newId3Wrapper.getId3v1Tag());
		mp3file.setId3v2Tag(newId3Wrapper.getId3v2Tag());
	}
	
	private String trimField(String field) {
		if(field == null) {
			return null;
		}
		
		return field.trim();
	}
	
	private String null2EmptyString(String in) {
		if(in == null) {
			return "";
		}
		
		return in;
	}

	private void updateCustomTag(Mp3File mp3file, String customTag) {
		byte[] existingCustomTag = mp3file.getCustomTag();
		byte[] newCustomTag = null;
		
		if (keepCustomTag && existingCustomTag != null && existingCustomTag.length > 0) {
			if (customTag != null && customTag.length() > 0) {
				EncodedText customTagEncodedText = new EncodedText(customTag);
				byte bytes[] = customTagEncodedText.toBytes(true);
				int newLength = existingCustomTag.length + bytes.length;
				newCustomTag = new byte[newLength];
				BufferTools.copyIntoByteBuffer(existingCustomTag, 0, existingCustomTag.length, newCustomTag, 0);
				BufferTools.copyIntoByteBuffer(bytes, 0, bytes.length, newCustomTag, existingCustomTag.length);
			} else {
				newCustomTag = mp3file.getCustomTag();
			}
		} else if (customTag != null && customTag.length() > 0) {
			EncodedText customTagEncodedText = new EncodedText(customTag);
			newCustomTag = customTagEncodedText.toBytes(true);
		}
		mp3file.setCustomTag(newCustomTag);
	}
	
	protected void renameFiles(String filename) {
		File originalFile = new File(filename);
		File backupFile = new File(filename + BACKUP_EXTENSION);
		File retaggedFile = new File(filename + RETAG_EXTENSION);
		if (backupFile.exists()) {
			backupFile.delete();
		}
		originalFile.renameTo(backupFile);
		retaggedFile.renameTo(originalFile);
	}
	
	private String cleanTrack(String track) {
		if (track == null) {
			return track;
		}
		
		int slashIndex = track.indexOf('/');
		
		if (slashIndex < 0) {
			return trimField(track.replaceAll("^0+", ""));
		}
		
		return trimField(track.substring(0, slashIndex).replaceAll("^0+", ""));
	}
	
	private String cleanImageMimeType(String mimeType) {
		if (mimeType == null) {
			return mimeType;
		}
		
		if (mimeType.indexOf('/') >= 0) {
			return mimeType;
		}
		
		return "image/" + mimeType.toLowerCase();
	}
}