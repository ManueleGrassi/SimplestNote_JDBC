package it.univaq.lbd.views;

public class NoteView {

    private Integer id;
    private String title;
    private String text;
    private String tags;

    public NoteView(Integer id, String title, String text, String tags) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text != null ? text : "NESSUN PARAGRAFO IN QUESTA NOTA";
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTags() {
        return tags != null ? tags : "NESSUN TAG IN QUESTA NOTA";
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
