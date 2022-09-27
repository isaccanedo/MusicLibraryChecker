package ch.shaped.mp3.library;

import java.io.File;

public class MP3LibraryItem {
	protected File item;
	protected MP3LibraryAlbum parent;
	protected String name;
	
	public MP3LibraryItem(String name, File item, MP3LibraryAlbum parent) {
		this.name = name;
		this.item = item;
		this.parent = parent;
	}
	
	public File getItem() {
		return item;
	}
	
	public void setItem(File item) {
		this.item = item;
	}
	
	public MP3LibraryAlbum getParent() {
		return parent;
	}
	
	public void setParent(MP3LibraryAlbum parent) {
		this.parent = parent;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
