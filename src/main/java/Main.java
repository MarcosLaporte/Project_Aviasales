import entities.Airport;
import entities.Entity;
import entities.Route;
import org.apache.logging.log4j.Level;
import services.IDao;
import services.MyBatis;
import services.RouteService;
import utils.*;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        RouteService routeService = new RouteService();
        routeService.start();
    }
}
