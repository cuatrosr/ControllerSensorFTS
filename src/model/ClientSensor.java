package model;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import config.GetPropertyValues;
import ui.Test;

public class ClientSensor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ClientSensor.class);

    private Socket socket;
    private DataInputStream inputTcp;
    private BufferedWriter bw;
    private byte[] bfrs;
    private int nbytes = 0;
    private static Date date1;
    private Date date2;
    
    public static void setDate1(Date newDate) {
        date1 = newDate;
    }

    //Se conecta al controlador mediante el host y el puerto
    public void connectController(String HOST, int PUERTO) throws Exception {
        socket = new Socket(HOST, PUERTO);
    }

    //Crea los flujos para recibir y enviar datos
    public void setUp() throws Exception {
        inputTcp = new DataInputStream(socket.getInputStream());

        OutputStream out = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(out);
        bw = new BufferedWriter(osw);
    }

    //Recibe la respuesta convirtiendola de hexadecimal a binario
    public String getBinTramaAnswer() {
        bfrs = new byte[512];
        try {
            nbytes = inputTcp.read(bfrs);
            bfrs[nbytes] = 0x00;
            String message = ConvertAndAnalyze.hexToBin(bfrs, nbytes);
            return message;
        } catch (Exception e) {
        }
        return "";
    }

    //Envia el pedido al controlador con la trama de error
    public void requestErrorTrama() {
        try {
            char[] datos = {0x01, 0x03, 0x00, 0x10, 0x00, 0x1F, 0x05, 0xC7};
            bw.write(datos);
            bw.flush();
        } catch (Exception SocketException) {
            logger.error("Error... Se ha perdido conexion con el controlador");
            Test.setConnectionSuccess(false);
            Test.connectController(GetPropertyValues.getHOST(), GetPropertyValues.getPUERTO());
            ConvertAndAnalyze.stopHilo();
        }
    }

    //Analiza la respuesta del controlador de la trama de error y actualiza las plataformas si es necesario
    public void setPlatformsError() {
        String binAnswer = getBinTramaAnswer();
        String[] intBin = ConvertAndAnalyze.organizeBinAnswer(binAnswer);
        int[] arrayErrorSensors = ConvertAndAnalyze.createArrayError(intBin);
        Sensor[] updatedPlatforms = Platforms.validatePlatformsError(arrayErrorSensors);
        Platforms.setPlatforms(updatedPlatforms);
    }

    //Envia el pedido al controlador con la trama de status
    public void requestStatusTrama() {
        try {
            char[] datos = {0x01, 0x03, 0x00, 0x00, 0x00, 0x0F, 0x05, 0xCE};
            bw.write(datos);
            bw.flush();
        } catch (Exception SocketException) {
            logger.error("Error... Se ha perdido conexion con el controlador");
            Test.setConnectionSuccess(false);
            Test.connectController(GetPropertyValues.getHOST(), GetPropertyValues.getPUERTO());
            ConvertAndAnalyze.stopHilo();
        }
    }

    //Analiza la respuesta del controlador de la trama de status y actualiza las plataformas si es necesario
    public void setPlatformsStatus() {
        String binAnswer = getBinTramaAnswer();
        String[] intBin = ConvertAndAnalyze.organizeBinAnswer(binAnswer);
        int[] arrayStatusSensors = ConvertAndAnalyze.createArrayStatus(intBin);
        Sensor[] updatedPlatforms = Platforms.validatePlatformsStatus(arrayStatusSensors);
        Platforms.setPlatforms(updatedPlatforms);
    }

    @Override
    public void run() {
        date1 = new Date();
        try {
            for (int infinite = 0; infinite > -1; infinite++) {
                requestErrorTrama();
                setPlatformsError();
                requestStatusTrama();
                setPlatformsStatus();
                Platforms.platformsSyncTrue();
                date2 = new Date();
                ConvertAndAnalyze.comparePlatformDates(date1, date2);
            }
        } catch (Exception e) {
        }
    }
}
