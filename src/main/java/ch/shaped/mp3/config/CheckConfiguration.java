package ch.shaped.mp3.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.shaped.mp3.check.LibraryCheck;

public class CheckConfiguration {
	private static final Logger logger = LogManager.getLogger(CheckConfiguration.class);
	
	private String version;
	private String logLevel;
	private List<String> checks;
	private String reportFile;
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String loglevel) {
		this.logLevel = loglevel;
	}
	
	public String getReportFile() {
		return reportFile;
	}

	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	public List<String> getChecks() {
		return checks;
	}
	
	public void setChecks(List<String> checks) {
		this.checks = checks;
	}
	
	public List<LibraryCheck> getLibraryCheck() {
		List<LibraryCheck> libchecks = new ArrayList<LibraryCheck>();
		if(this.checks == null) {
			return libchecks;
		}
		
		for (String s : this.checks) {
			try {
				String className = "ch.shaped.mp3.check.impl."+s;
				Class c = Class.forName(className);
				LibraryCheck lc = (LibraryCheck)c.newInstance();
				libchecks.add(lc);
			} catch(Exception e) {
				logger.error("Check "+s+" not found.");
			}
		}
		
		return libchecks;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
		  .append(String.format("Enabled checks: %s\n", checks))
		  .append(String.format("log level: %s\n", logLevel))
		  .append(String.format("report file: %s\n", reportFile))
		  .toString();
	}
}
