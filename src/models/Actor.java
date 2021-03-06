package models;

/**
 * Created by 23878410v on 01/02/17.
 */
public class Actor {
    private int actor_id;
    private String first_name;
    private String last_name;

    public Actor() {
    }

    public Actor(int actor_id, String first_name, String last_name) {
        this.actor_id = actor_id;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public int getActor_id() {
        return actor_id;
    }

    public void setActor_id(int actor_id) {
        this.actor_id = actor_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getInsertQuery(){
        return "INSERT INTO actor (actor_id, first_name, last_name) VALUES ({0}, {1}, {2})"
                .replace("{0}", ""+actor_id)
                .replace("{1}", "'"+first_name+"'")
                .replace("{2}", "'"+last_name+"'");
    }

    public String getUpdateQuery(){
        return "UPDATE actor SET first_name={0}, LAST_NAME={1} WHERE actor_id={2}"
                .replace("{0}", "'"+first_name+"'")
                .replace("{1}", "'"+last_name+"'")
                .replace("{2}", ""+actor_id);
    }

}
