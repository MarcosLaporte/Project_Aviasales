package view.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public abstract class View
{
    protected final Logger logger = LogManager.getLogger(View.class);

    protected HashMap<String, Object> displayData;

    public View(){displayData = new HashMap<>();}

    public abstract void display();

    public void setInputs(HashMap<String, Object> inputs){
        displayData = inputs;
    }
}
