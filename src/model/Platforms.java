package model;

import rest.CommunicationServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Platforms extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(Platforms.class);

    private static int ammountPlatformsOut;
    private static Sensor[] platforms;

    public Platforms(int ammountPlatformsIn) {
        platforms = new Sensor[ammountPlatformsIn];
        ammountPlatformsOut = ammountPlatformsIn;
    }

    public void firstFillPlatforms() {
        for (int id = 0; id < platforms.length; id++) {
            platforms[id] = new Sensor((id + 1), -1, false);
        }
    }

    public static Sensor[] getPlatforms() {
        return platforms;
    }

    public static int getAmmountPlatformsOut() {
        return ammountPlatformsOut;
    }

    public static void setPlatforms(Sensor[] newPlatforms) {
        platforms = newPlatforms;
    }

    //Enviar la plataforma al servidor si hubo un cambio
    public static void platformsSyncTrue() throws Exception {
        try {
            for (int id = 0; id < platforms.length; id++) {
                if (platforms[id].getSync()) {
                    CommunicationServer.sendPlatformsSyncTrue(platforms[id]);
                    platforms[id].setSync(false);
                }
            }
        } catch (Exception ConnectException) {
            logger.error("Error... Se perdio conexion con el servidor");
        }
    }

    //Verificar si hubo algun cambio, si hay alguno, entonces avisar para luego subirlo al servidor
    public static Sensor[] validatePlatformsError(int[] arrayErrorSensors) {
        Sensor[] existingPlatforms = Platforms.getPlatforms();
        for (int id = 0; id < arrayErrorSensors.length; id++) {
            try {
                if (((existingPlatforms[id].getAddress() - 1) == id) && existingPlatforms[id].getStatus() != arrayErrorSensors[id]) {
                    if (arrayErrorSensors[id] == 0 && ((existingPlatforms[id].getStatus() == 1) || existingPlatforms[id].getStatus() == 2)) {
                    } else {
                        existingPlatforms[id].setStatus(arrayErrorSensors[id]);
                        existingPlatforms[id].setSync(true);
                    }
                }
            } catch (Exception ArrayIndexOutOfBoundsException) {
            }
        }
        return existingPlatforms;
    }

    //Verificar si hubo algun cambio, si hay alguno, entonces avisar para luego subirlo al servidor
    public static Sensor[] validatePlatformsStatus(int[] arrayStatusSensors) {
        Sensor[] existingPlatforms = Platforms.getPlatforms();
        for (int id = 0; id < arrayStatusSensors.length; id++) {
            try {
                if (((existingPlatforms[id].getAddress() - 1) == id) && (existingPlatforms[id].getStatus() != 3)) {
                    if (existingPlatforms[id].getStatus() == arrayStatusSensors[id]) {
                    } else {
                        existingPlatforms[id].setStatus(arrayStatusSensors[id]);
                        existingPlatforms[id].setSync(true);
                    }
                }
            } catch (Exception ArrayIndexOutOfBoundsException) {
            }
        }
        return existingPlatforms;
    }
}
