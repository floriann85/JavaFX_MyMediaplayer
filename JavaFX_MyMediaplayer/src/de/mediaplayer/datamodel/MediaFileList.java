package de.mediaplayer.datamodel;

// mit dieser Klasse wird der Mediendateipfad,
// Anzeigename, Titel sowie Jahr der MediaFile gespeichert
public class MediaFileList {
	String displayName;

	String filePath;

	public MediaFileList(String filePath) {
		this.filePath = filePath;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}