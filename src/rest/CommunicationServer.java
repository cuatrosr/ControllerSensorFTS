package rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;
import config.GetPropertyValues;
import model.ConvertAndAnalyze;
import model.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunicationServer {

    private static URL url;
    private static String stringURL;
    private static HttpURLConnection huc;
    private static final Logger logger = LoggerFactory.getLogger(CommunicationServer.class);

    public static void setURL(String stringURL) throws Exception {
        url = new URL(stringURL);
    }

    public static void connectUrl() throws Exception {
        huc = (HttpURLConnection) url.openConnection();
    }

    public static void setUpHttpURLConnection() throws Exception {
        huc.setRequestMethod("POST");
        huc.setRequestProperty("Content-Type", "application/json");
        huc.setDoOutput(true);
    }

    public static void sendInfoServer(byte[] platformBytes) throws Exception {
        huc.getOutputStream().write(platformBytes);
    }

    public static void getServerAnswer() throws Exception {
        Reader in = new BufferedReader(new InputStreamReader(huc.getInputStream(), "UTF-8"));
    }

    public static void sendPlatformsSyncTrue(Sensor platform) throws Exception {
        JSONObject platformJson = ConvertAndAnalyze.platformToJson(platform);
        byte[] platformBytes = ConvertAndAnalyze.jsonObjToArrayByte(platformJson);
        connectUrl();
        setUpHttpURLConnection();
        sendInfoServer(platformBytes);
        logger.info("Plataforma enviada. platform:{}", platformJson);
        getServerAnswer();
    }

    public static void sendAllPlatforms() throws Exception {
        try {
            for (int id = 0; id < GetPropertyValues.getAmmountPlatforms(); id++) {
                JSONObject platformJson = ConvertAndAnalyze.allPlatformsToJson(id);
                byte[] platformBytes = ConvertAndAnalyze.jsonObjToArrayByte(platformJson);
                connectUrl();
                setUpHttpURLConnection();
                sendInfoServer(platformBytes);
                logger.info("Plataforma enviada. platform:{}", platformJson);
                getServerAnswer();
            }
        } catch (Exception ConnectException) {
            logger.error("Error... Se perdio conexion con el servidor. URL:{}", stringURL);
        }
    }
}
