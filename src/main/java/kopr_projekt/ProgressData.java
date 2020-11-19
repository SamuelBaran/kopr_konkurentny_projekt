package kopr_projekt;

public class ProgressData {


    Progress byteAmountProgress;
    Progress fileAmountProgress;

    public ProgressData(long totalFilesAmount, long totalBytesAmount){
        fileAmountProgress = new Progress(0, totalFilesAmount);
        byteAmountProgress = new Progress(0, totalBytesAmount);
    }

    public ProgressData(long downloadedFilesAmount, long totalFilesAmount,
                        long downloadedBytesAmount, long totalBytesAmount){
        fileAmountProgress = new Progress(downloadedFilesAmount, totalFilesAmount);
        byteAmountProgress = new Progress(downloadedBytesAmount, totalBytesAmount);
    }

    private ProgressData(Progress byteAmountProgress, Progress fileAmountProgress){
        this.byteAmountProgress = byteAmountProgress;
        this.fileAmountProgress = fileAmountProgress;
    }


    public Progress getByteAmountProgress() {
        return byteAmountProgress;
    }

    public Progress getFileAmountProgress() {
        return fileAmountProgress;
    }

    public ProgressData copy(){
        return new ProgressData(byteAmountProgress, fileAmountProgress);
    }
}
