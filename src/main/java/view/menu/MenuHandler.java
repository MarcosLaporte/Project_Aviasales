package view.menu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Abstract Menu Handler Class
public abstract class MenuHandler {
    protected static final Logger logger = LogManager.getLogger(MenuHandler.class);

    public MenuHandler() {
    }

    public abstract void processMenuOption();

}