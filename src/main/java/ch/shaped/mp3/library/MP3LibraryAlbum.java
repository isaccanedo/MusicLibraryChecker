package ch.shaped.mp3.library;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MP3LibraryAlbum {
	protected File item;
	protected List<MP3LibraryItem> childs = new ArrayList<MP3LibraryItem>();
	protected String name;

	public MP3LibraryAlbum(String name, File item) {
		this.name = name;
		this.item = item;
	}
	
	public void addChild(MP3LibraryItem child) {
		this.childs.add(child);
	}
	
	public File getItem() {
		return item;
	}
	
	public void setItem(File item) {
		this.item = item;
	}
	
	public List<MP3LibraryItem> getChilds() {
		return childs;
	}
	
	public void setChilds(List<MP3LibraryItem> childs) {
		this.childs = childs;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
