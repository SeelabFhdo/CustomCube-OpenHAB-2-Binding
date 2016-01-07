package org.openhab.binding.customcube.internal.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.openhab.binding.customcube.handler.CustomCubeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CubeService extends Thread {

    private Logger logger = LoggerFactory.getLogger(CubeService.class);
    private CustomCubeHandler customCubeHandler;
    private DatagramSocket socket;

    private static final Integer CUBE_MESSAGE_LENGTH = 1;
    private static final byte CUBE_ACTION_POSITION_CHANGED = 0;
    private static final byte CUBE_ACTION_ROTATED = 1;
    private static final byte CUBE_VALUE_ROTATED_LEFT = 0;
    private static final byte CUBE_VALUE_ROTATED_RIGHT = 1;

    private static final byte BIT_MASK_CUBE_ACTION = (byte) 0xe0;
    private static final byte BIT_MASK_CUBE_POSITION = (byte) 0x1c;
    private static final byte BIT_MASK_CUBE_VALUE = (byte) 0x03;

    public CubeService(DatagramSocket socket, CustomCubeHandler customCubeHandler) {
        this.socket = socket;
        this.customCubeHandler = customCubeHandler;
    }

    @Override
    public void run() {

        byte[] data = new byte[32];

        while (!isInterrupted()) {
            try {

                // logger.debug("Waiting for Cube connection ...");
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                if (packet.getLength() == CUBE_MESSAGE_LENGTH) {
                    // logger.debug("Just connected to " + packet.getSocketAddress());
                    byte action = (byte) ((packet.getData()[0] & BIT_MASK_CUBE_ACTION) >> 5);
                    byte position = (byte) ((packet.getData()[0] & BIT_MASK_CUBE_POSITION) >> 2);
                    byte value = (byte) (packet.getData()[0] & BIT_MASK_CUBE_VALUE);
                    logger.debug("Action: " + action + ", position: " + position + ", value: " + value);

                    // Die Position wird stets mitgesendet, also auch beim Rotations-Event. Wieso? UDP!
                    // Würde die Information nur bei einem Positionswechsel-Event übertragen werden und diese
                    // aufgrund von UDP verloren gehen, dann würden sich folgende Rotationen auf die alte
                    // bekannte Position auswirken. Wozu dann die Unterscheidung von action?
                    // Weitere Aktionen wie z.B. Kippbewegungen sind denkbar. :-)
                    if (action == CUBE_ACTION_POSITION_CHANGED || position != customCubeHandler.getPosition()) {
                        if (position >= 1 && position <= 6) {
                            customCubeHandler.setPosition(new Integer(position));
                        }
                    }

                    if (action == CUBE_ACTION_ROTATED) {
                        if (value == CUBE_VALUE_ROTATED_LEFT) {
                            customCubeHandler.decrease();
                        } else if (value == CUBE_VALUE_ROTATED_RIGHT) {
                            customCubeHandler.increase();
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
