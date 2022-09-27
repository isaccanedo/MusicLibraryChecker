package ch.shaped.mp3.report;

import java.util.ArrayList;
import java.util.List;

import ch.shaped.mp3.library.MP3LibraryAlbum;
import ch.shaped.mp3.library.MP3LibraryItem;

public class CheckReport {
	private final String name;
	private final String description;

	private final MP3LibraryAlbum album;
	
	private List<MP3LibraryItem> success = new ArrayList<MP3LibraryItem>();
	private List<MP3LibraryItem> fail = new ArrayList<MP3LibraryItem>();
	
	public CheckReport(String name, String description, MP3LibraryAlbum album) {
		this.name = name;
		this.description = description;
		this.album = album;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public MP3LibraryAlbum getAlbum() {
		return album;
	}

	public List<MP3LibraryItem> getSuccess() {
		return success;
	}
	
	public List<MP3LibraryItem> getAll() {
		List<MP3LibraryItem> list = new ArrayList<MP3LibraryItem>(this.success);
		list.addAll(this.fail);
		
		return list;
	}

	public List<MP3LibraryItem> getFail() {
		return fail;
	}

	public void addFail(MP3LibraryItem item) {
		this.fail.add(item);
	}
	
	public void add(MP3LibraryItem item, boolean success) {
		if(success) {
			this.success.add(item);
		} else {
			this.fail.add(item);
		}
	}
	
	public void addSuccess(MP3LibraryItem item) {
		this.success.add(item);
	}
}
