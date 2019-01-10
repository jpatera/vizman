package eu.japtor.vizman.backend.bean;

public class EvidKont implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    String ckont;
    String text;
    String folder;

    public EvidKont(String ckont, String text, String folder) {
        this.ckont = ckont;
        this.text = text;
        this.folder = folder;
    }

    public String getCkont() {
        return ckont;
    }

    public void setCkont(String ckont) {
        this.ckont = ckont;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
