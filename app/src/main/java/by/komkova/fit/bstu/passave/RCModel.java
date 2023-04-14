package by.komkova.fit.bstu.passave;

public class RCModel {

    Integer id;
    String title;
    String login;
    String lastUpdateDate;
    Integer favourite;

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

}
