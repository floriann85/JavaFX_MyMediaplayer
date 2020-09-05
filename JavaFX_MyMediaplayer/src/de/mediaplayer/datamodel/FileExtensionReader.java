package de.mediaplayer.datamodel;

// mit dieser Klasse wird der Dateiname der MediaFile ausgelesen
public class FileExtensionReader {
	
	// die Methode ermittelt die Dateinamenerweiterung
	public static String getExtension(String filename) {
		if (filename == null) {
			return null;
		}

		int extensionPos = filename.lastIndexOf('.');
		int lastUnixPos = filename.lastIndexOf('/');
		int lastWindowsPos = filename.lastIndexOf('\\');
		int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);

		int index = lastSeparator > extensionPos ? -1 : extensionPos;
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}
}