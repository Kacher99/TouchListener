package info.application.touchlistener;

public class Info {
    private String name;
    private String note;
    private int screen;

    public Info(String name,String note,int screen){
        this.name = name;
        this.note = note;
        this.screen = screen;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public int getScreen() {
        return screen;
    }
}
