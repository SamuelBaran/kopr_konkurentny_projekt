package kopr_projekt;

public class FileInfo {
    private String filepath;
    private String fullDestinationPath;
    private long downloadedAmount;
    private long fileSize;

    public FileInfo(String filepath, String fullDestinationPath, long downloadedAmount, long fileSize) {
        this.filepath = filepath;
        this.fullDestinationPath = fullDestinationPath;
        this.downloadedAmount = downloadedAmount;
        this.fileSize = fileSize;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getFullDestinationPath() {
        return fullDestinationPath;
    }

    public long getDownloadedAmount() {
        return downloadedAmount;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setDownloadedAmount(long downloadedAmount) {
        this.downloadedAmount = downloadedAmount;
    }

    public void increaseDownloadedAmount(long amountToAdd) {
        this.downloadedAmount += amountToAdd;
    }

    public boolean downloadingDone(){
        return downloadedAmount == fileSize;
    }

    @Override
    public String toString() {
        return " -->  '" + fullDestinationPath+"'\n\t(" + downloadedAmount + "/" + fileSize + ")\n";
    }
}
