/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.customcube.handler;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.DatagramSocket;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.customcube.CustomCubeBindingConstants;
import org.openhab.binding.customcube.internal.io.CubeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CustomCubeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Marcel Zillekens - Initial contribution
 */
public class CustomCubeHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(CustomCubeHandler.class);
    private CubeService cubeService;

    // Current side of cube
    private Integer position = 0;

    public CustomCubeHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("There is no command to handle.");
    }

    @Override
    public void initialize() {

        Configuration config = getThing().getConfiguration();
        Integer port = ((BigDecimal) config.get(CustomCubeBindingConstants.CUBE_PARAMETER_PORT)).intValue();

        try {
            DatagramSocket socket = new DatagramSocket(port);
            cubeService = new CubeService(socket, this);
            cubeService.start();

            // Nicht ganz korrekt, da sich der Würfel bei neuen Ereignissen
            // via UDP zum CubeService verbindet. Der Würfel ist somit
            // nicht dauerhaft online.
            updateStatus(ThingStatus.ONLINE);

        } catch (IOException e) {
            logger.error("Could not start CubeService. Exception message:\n" + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        cubeService.interrupt();
        super.dispose();
    }

    public synchronized void increase() {
        // logger.debug("Cube rotated right, increase (position: " + position + ")");

        ChannelUID channelUID = new ChannelUID(getThing().getUID(), CustomCubeBindingConstants.CHANNEL_CUBE_ROTATION);
        updateState(channelUID, new DecimalType(1));
    }

    public synchronized void decrease() {
        // logger.debug("Cube rotated left, decrease (position: " + position + ")");

        ChannelUID channelUID = new ChannelUID(getThing().getUID(), CustomCubeBindingConstants.CHANNEL_CUBE_ROTATION);
        updateState(channelUID, new DecimalType(-1));
    }

    public synchronized void setPosition(Integer position) {
        // logger.debug("Cube position changed to " + position);
        this.position = position;

        ChannelUID channelUID = new ChannelUID(getThing().getUID(), CustomCubeBindingConstants.CHANNEL_CUBE_FLIP);
        updateState(channelUID, new DecimalType(position));
    }

    public synchronized Integer getPosition() {
        return position;
    }
}
