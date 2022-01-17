package escape.game;

import escape.required.GameObserver;

public class GO implements GameObserver {

    //vars
    String message = null;

    /**
     * Create an observer
     */
    public GO(){
        this.message = "Observer Created";
    }
}
