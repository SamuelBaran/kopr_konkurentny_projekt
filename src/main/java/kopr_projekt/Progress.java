package kopr_projekt;

public class Progress{
    long downloaded;
    long all;

    public Progress(long downloaded, long all){
        this.all = all;
        this.downloaded = downloaded;
    }

    public void addToDownloaded(long toAdd){
        downloaded += toAdd;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public double getProgressValue(){
        return ((double)downloaded) / all;
    }


    @Override
    public String toString() {
        return niceSeparatedLong(downloaded) + " / " + niceSeparatedLong(all);
    }

    private String niceSeparatedLong(long l){
//                if(l == 0)
//                    return "0";
        String s = "";
        int i = 1;
        while (l != 0) {
            s = (l % 10) + s;
            if (i % 3 == 0)
                s = " " + s;
            i++;
            l = l / 10;
        }
        return s;
    }
}
