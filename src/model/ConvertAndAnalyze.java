package model;

import config.GetPropertyValues;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import rest.CommunicationServer;
import ui.Test;

public class ConvertAndAnalyze {

    private static String[] intBin;
    private static int refreshPlatformsTimeIn;

    //El tiempo para enviar todas las plataformas al servidor
    public static void setRefreshPlatformsTime(int refreshPlatformsTime) {
        refreshPlatformsTimeIn = refreshPlatformsTime;
    }

    //Convertir de hexadecimal a binario
    public static String hexToBin(byte[] bfrs, int nbytes) {
        String messageBin = "";
        for (int i = 0; i < nbytes; i++) {
            messageBin += String.format("%8s", Integer.toBinaryString(bfrs[i] & 0xFF)).replace(' ', '0');
        }
        return messageBin;
    }

    //Dividir la respuesta de string entero a partes de 8 digitos
    public static String[] organizeBinAnswer(String binAnswer) {
        intBin = new String[binAnswer.length() / 8];
        int subStr1 = 0;
        int subStr2 = 8;
        for (int intBinId = 0; intBinId < intBin.length; intBinId++) {
            intBin[intBinId] = binAnswer.substring(subStr1, subStr2);
            subStr1 += 8;
            subStr2 += 8;
        }
        return intBin;
    }

    //Analiza el array ordenado de binario para crear un array de estado de plataformas en error o ok
    public static int[] createArrayError(String[] intBin) {
        int[] isInError = new int[256];
        int count = 16;
        int count2 = 16;
        int tryBin = 0;
        boolean donedBin = false;
        String errorOk = "10";
        for (int intBinId = 0; intBinId < intBin.length; intBinId++) {
            if ((intBinId > 2) && (intBinId <= 34)) {
                for (int intBinChar = 0; intBinChar < intBin[0].length(); intBinChar++) {
                    if (tryBin < 2) {
                        isInError[count - 1] = (intBin[intBinId].charAt(intBinChar) == errorOk.charAt(0)) ? 3 : 0;
                        count--;
                    }
                }
                tryBin++;
                donedBin = (tryBin == 2) ? true : false;
                if (donedBin) {
                    count2 += 16;
                    count = count2;
                    tryBin = 0;
                }
            }
        }
        return isInError;
    }

    //Analiza el array ordenado de binario para crear un array de estado de plataformas en libre o ocupado
    public static int[] createArrayStatus(String[] intBin) {
        int[] statusSensors = new int[256];
        int count = 16;
        int count2 = 16;
        int tryBin = 0;
        boolean donedBin = false;
        String takenFree = "10";
        for (int intBinId = 0; intBinId < intBin.length; intBinId++) {
            if ((intBinId > 2) && (intBinId <= 34)) {
                for (int intBinChar = 0; intBinChar < intBin[0].length(); intBinChar++) {
                    if (tryBin < 2) {
                        statusSensors[count - 1] = (intBin[intBinId].charAt(intBinChar) == takenFree.charAt(0)) ? 2 : 1;
                        count--;
                    }
                }
                tryBin++;
                donedBin = (tryBin == 2) ? true : false;
                if (donedBin) {
                    count2 += 15 + 1;
                    count = count2;
                    tryBin = 0;
                }
            }
        }
        return statusSensors;
    }

    public static JSONObject platformToJson(Sensor platform) {
        JSONObject platformJson = new JSONObject();
        platformJson.put("id", "1608666305");
        platformJson.put("plataforma", String.valueOf(platform.getAddress()));
        platformJson.put("estado", String.valueOf(platform.getStatus()));
        platformJson.put("control_plataforma", "control_plataforma-1");
        platformJson.put("fecha_solicitud", formatCurrentDate());
        return platformJson;
    }

    public static JSONObject allPlatformsToJson(int id) {
        JSONObject platformJson = new JSONObject();
        Sensor[] existingPlatforms = Platforms.getPlatforms();
        platformJson.put("id", "1608666305");
        platformJson.put("plataforma", String.valueOf(existingPlatforms[id].getAddress()));
        platformJson.put("estado", String.valueOf(existingPlatforms[id].getStatus()));
        platformJson.put("control_plataforma", "control_plataforma-1");
        platformJson.put("fecha_solicitud", formatCurrentDate());
        return platformJson;
    }

    public static String formatCurrentDate() {
        Date currentDate = new Date();
        DateFormat standarDate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat standarHour = new SimpleDateFormat("HH:mm:ss");
        String currentDateFormat = standarDate.format(currentDate) + " " + standarHour.format(currentDate);
        return currentDateFormat;
    }

    public static byte[] jsonObjToArrayByte(JSONObject platformJson) throws Exception {
        byte[] dataBytes = platformJson.toJSONString().getBytes("UTF-8");
        return dataBytes;
    }
    
    //Compara el tiempo transcurrido si es mayor al tiempo del .properties para enviar las plataformas o no
    public static void comparePlatformDates(Date date1, Date date2) throws Exception {
        if ((date2.getTime() - date1.getTime()) >= refreshPlatformsTimeIn) {
            CommunicationServer.sendAllPlatforms();
            ClientSensor.setDate1(date1 = new Date());
        }
    }

    //Si el controlador se baja, entonces comparar el tiempo si es mayor al de .properties para intentar reconectarse
    public static boolean compareControllerDates(Date date1) {
        boolean reconnect = false;
        do {
            Date date2 = new Date();
            if ((date2.getTime() - date1.getTime()) >= GetPropertyValues.getRefreshControllerTime()) {
                reconnect = true;
            }
        } while (!reconnect);
        return reconnect;
    }
    
    //detener el hilo mientras no esta conectado el controlador
    public static void stopHilo() {
        do {
        } while (!Test.getConnectionSuccess());
    }
}
