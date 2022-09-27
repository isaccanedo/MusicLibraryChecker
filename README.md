MusicLibraryChecker
===================
[![Build Status](https://travis-ci.org/christofluethi/MusicLibraryChecker.svg?branch=master)](https://travis-ci.org/christofluethi/MusicLibraryChecker)

Simple command line tool to check your music library

## Music Library Format
The LibraryChecker needs the following specific directory structure:

```
%{artist} - %{album}/%{track} %{artist} - %{title}
```

there is no artist folder (note for iTunes compatibility).

## Configuration
use a yaml configuration file to configure the checks to be run. 

A sample configuration looks like this:

```yaml
version: 1.0
logLevel: info
reportFile: ~/library-report.txt
checks:
    - ArtworkCheck
    - ID3v2TagCheck
    - TrackNumberCheck
```

You have to list the LibraryChecks which should be run in the checks section.

## Reporting

If you add the reportFile config a full report containing all failed items will be written.

The report looks like this:

 ```
Gspraechstoff - Hoebeler EP
------------------------------------------------------------------
 	      Check      State File
       ArtworkCheck       FAIL CD.m3u
      ID3v2TagCheck       FAIL CD.m3u
   TrackNumberCheck       FAIL 01 Intro1.mp3
   TrackNumberCheck       FAIL 02 Intro2.mp3

 Billboard - Hot 100 Songs 2013
------------------------------------------------------------------
              Check      State File

...
 ```

## How to use it
Start the utility from commandline using the following command

 ```
 java -jar MusicLibraryChecker.jar albumart.yml ~/MusicLibrary/
 Your MusicLibrary contains 87 albums
 Progress [=============                                     ]   24.05%     320 tracks found in 21 albums    
```
  
## Available Checks
* ArtworkCheck - Check if every file has the id3 cover tag set
* TrackNumberCheck - Check if every file has the tracknumbers set
* ID3v2Check - Check if every file has the id3v2 tags set
