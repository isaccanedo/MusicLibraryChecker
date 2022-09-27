package ch.shaped.mp3.check;
import ch.shaped.mp3.library.MP3LibraryAlbum;
import ch.shaped.mp3.report.LibraryReport;

public interface LibraryCheck {
	void run(MP3LibraryAlbum album);
	String getName();
	String getDescription();
	void setLibraryReport(LibraryReport libraryReport);
}
