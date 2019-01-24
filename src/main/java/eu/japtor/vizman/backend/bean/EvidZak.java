package eu.japtor.vizman.backend.bean;

public class EvidZak implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    Long kontId;
    Integer czak;
    String text;
    String folder;
    String kontFolder;

    public EvidZak(Long kontId, Integer czak, String text, String folder, String kontFolder) {
        this.kontId = kontId;
        this.czak = czak;
        this.text = text;
        this.folder = folder;
        this.kontFolder = kontFolder;
    }

    public Long getKontId() {
        return kontId;
    }

    public void setKontId(Long kontId) {
        this.kontId = kontId;
    }

    public Integer getCzak() {
        return czak;
    }

    public void setCzak(Integer czak) {
        this.czak = czak;
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

    public String getKontFolder() {
        return kontFolder;
    }

    public void setKontFolder(String kontFolder) {
        this.kontFolder = kontFolder;
    }

}
