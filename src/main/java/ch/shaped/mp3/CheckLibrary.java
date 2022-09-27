package ch.shaped.mp3;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.yaml.snakeyaml.Yaml;

import ch.shaped.mp3.check.LibraryCheck;
import ch.shaped.mp3.config.CheckConfiguration;
import ch.shaped.mp3.library.MP3LibraryAlbum;
import ch.shaped.mp3.library.MP3LibraryItem;
import ch.shaped.mp3.report.LibraryReport;

/**
 * mp3 checker
 */
public class CheckLibrary {
	private static final Logger logger = LogManager.getLogger(CheckLibrary.class);
	
	private List<LibraryCheck> checks = new ArrayList<LibraryCheck>();
	private File source;
	private LibraryReport libraryReport;
	
	FileFilter directoryFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};
	
	public CheckLibrary(File source) {
		this.source = source;
	}
	
	public void setLibraryReport(LibraryReport lr) {
		this.libraryReport = lr;
	}
	
	public LibraryReport getLibraryReport() {
		return this.libraryReport;
	}
	
	public void run() {
		File[] albums = this.source.listFiles(directoryFilter);
		int processed = 0;
		int files = 0;
		if(albums != null) {
			System.out.println("Your MusicLibrary contains "+albums.length+" albums");
			logger.info("Your MusicLibrary contains "+albums.length+" albums");

			for (File album : albums) {
				if(processed % 10 == 0) {
					double percentage = round(((double)100/(double)albums.length * (double)processed), 2);
					printProgBar(percentage, processed, files);
				}
				
				if(album.isFile()) {
					logger.warn("Unexpected MusicLibrary format: "+album.getName()+" is a file. should be a directory.");
				} else {	
					File[] tracks = album.listFiles();
					MP3LibraryAlbum libraryAlbum = new MP3LibraryAlbum(album.getName(), album);
					if(tracks != null) {
						for (File track : tracks) {
							if(track.isDirectory()) {
								logger.warn("Unexpected MusicLibrary format: "+album+"/"+track.getName()+" is a directory. should be a file.");
							} else {
								MP3LibraryItem albumItem = new MP3LibraryItem(track.getName(), track, libraryAlbum);
								libraryAlbum.addChild(albumItem);
								files++;
							}
						}
					}
					
					for (LibraryCheck check : this.checks) {
						check.run(libraryAlbum);
					}
				}
				processed++;
			}
			printProgBar(100, processed, files);
		}
	}
	
    public static void main( String[] args ) {
        logger.info("CheckLibrary started...");
        
        if(args.length > 1 && args[0].length() > 0 && args[1].length() > 0) {
        	File library = new File(args[1]);
        	File yamlConfigFile = new File(args[0]);
        	if(library.exists() && yamlConfigFile.exists()) {
        		CheckLibrary checker = new CheckLibrary(library);
        		LibraryReport lr = new LibraryReport();
        		checker.setLibraryReport(lr);
        		String reportFile = null;
        		
        		Yaml yaml = new Yaml();  
            	try {
            		logger.info("Reading config file: "+args[0]);
            		InputStream in = Files.newInputStream(Paths.get(args[0]));

            		CheckConfiguration configuration = yaml.loadAs(in, CheckConfiguration.class);
            		String level = configuration.getLogLevel().toUpperCase();
            		reportFile = configuration.getReportFile();
            		if(reportFile != null && !reportFile.trim().isEmpty()) {
            			logger.info("Will write report to: "+reportFile);
            		} else {
            			logger.info("No report file given. Writing to stdout.");
            			reportFile = null;
            		}
            		
            		if(level != null && !level.isEmpty()) {
            			logger.warn("Resetting LogLevel to: "+level);
            			
            			LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
                		Configuration config = ctx.getConfiguration();
                		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
                		loggerConfig.setLevel(Level.getLevel(level));
                		ctx.updateLoggers(); 
            		}
            		
            		List<LibraryCheck> checks = configuration.getLibraryCheck();
            		
            		for (LibraryCheck libraryCheck : checks) {
            			libraryCheck.setLibraryReport(lr);
            			logger.info("Adding LibraryCheck: "+libraryCheck.getName());
						checker.addCheck(libraryCheck);
					}
            	} catch(IOException e) {
            		logger.error("Cannot read YAML config file. File '"+ args[1] +"'");
            	}
        		checker.run();
        		
        		if(reportFile != null) {
        			logger.info("Writing report to file: "+reportFile+"");
        			lr.writeReport(new File(reportFile));
        		} else {
        			System.out.println(lr.getReport(true));
        		}
        	} else {
        		logger.error("Specified MusicLibrary path or YAML config file do not exist");
        	}
        } else {
        	 	logger.info("Need YAML config file parameter and MusicLibrary path to check");
        }
    }
    
    public void addCheck(LibraryCheck lc) {
    	this.checks.add(lc);
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static void printProgBar(double percent, int albums, int tracks) {
        StringBuilder bar = new StringBuilder("Progress [");
        
        for(int i = 0; i < 50; i++) {
            if( i < (percent/2)) {
                bar.append("=");
            } else if( i == (int)(percent/2)) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }

        bar.append("]   " + percent + "%     "+tracks+" tracks found in "+albums+" albums       ");
        System.out.print("\r" + bar.toString());
        
        if(percent == 100) {
        	System.out.println();
        }
    }
}
