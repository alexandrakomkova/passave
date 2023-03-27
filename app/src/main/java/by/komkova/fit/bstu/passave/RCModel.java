package by.komkova.fit.bstu.passave;

public class RCModel {

    Integer id;
    String title;
    String lastUpdateDate;

//    public RCModel(Integer id, String title, String lastUpdateDate) {
//        this.id = id;
//        this.title = title;
//        this.lastUpdateDate = lastUpdateDate;
//    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
