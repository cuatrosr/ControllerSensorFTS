package ui;

import config.GetPropertyValues;
import model.ClientSensor;
import model.ConvertAndAnalyze;
import model.Platforms;
import rest.CommunicationServer;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {

    private static final Logger logger = LoggerFactory.getLogger(Test.class);
    
    private static Date date1;
    private static ClientSensor clientSensor;
    private static boolean connectionSuccess;
    private static boolean firstConnection;
    
    public static void main(String[] args) throws Exception {
        logger.info("Controlador de Sensor iniciado");
        GetPropertyValues properties = new GetPropertyValues();
        properties.setPropValues();

        int ammountPlatformsIn = GetPropertyValues.getAmmountPlatforms();

        String HOST = GetPropertyValues.getHOST();
        int PUERTO = GetPropertyValues.getPUERTO();

        int refreshPlatformsTime = properties.getRefreshPlatformsTime();

        String stringURL = properties.getURL();
        CommunicationServer.setURL(stringURL);

        ConvertAndAnalyze.setRefreshPlatformsTime(refreshPlatformsTime);

        Platforms app = new Platforms(ammountPlatformsIn);
        app.firstFillPlatforms();

        firstConnection = true;
        clientSensor = new ClientSensor();
        connectController(HOST, PUERTO);
    }

    public static void connectController(String HOST, int PUERTO) {
        try {
            clientSensor.connectController(HOST, PUERTO);
            clientSensor.setUp();
            connectionSuccess = true;
            logger.info("Conexion al controlador exitoso. HOST:{}, PUERTO:{}", HOST, PUERTO);
            if (firstConnection) {
                firstConnection = false;
                clientSensor.start();
            }
        } catch (Exception ConnectException) {
            connectionSuccess = false;
            logger.error("Error... No me puedo conectar al controlador. HOST:{}, PUERTO:{}", HOST, PUERTO);
            date1 = new Date();
            if (ConvertAndAnalyze.compareControllerDates(date1)) {
                logger.info("Reconectando al controlador. HOST:{}, PUERTO:{}", HOST, PUERTO);
                connectController(HOST, PUERTO);
            }
        }
    }

    public static boolean getConnectionSuccess() {
        return connectionSuccess;
    }

    public static void setConnectionSuccess(boolean status) {
        connectionSuccess = status;
    }

    public static ClientSensor getClientSensor() {
        return clientSensor;
    }
}
