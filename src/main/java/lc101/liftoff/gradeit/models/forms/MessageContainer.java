package lc101.liftoff.gradeit.models.forms;

import sun.security.jca.GetInstance;

public class MessageContainer {
    public String message;
    public boolean isError = false; //no error code
    public int id = 0; //any int meaningful value

    public MessageContainer(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    public MessageContainer() {
    }
}


/*
public class MessageContainer {
    private static MessageContainer instance = null;
    private String message;

    private MessageContainer(String message) {
        this.message = message;
    }

    public static MessageContainer GetInstance(String message) {
        if(instance == null) instance = new MessageContainer(message);
        else instance.message = message;
        return instance;
    }
}
*/
