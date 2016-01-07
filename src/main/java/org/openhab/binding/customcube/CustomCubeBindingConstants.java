/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.customcube;

import java.util.Collection;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.Lists;

/**
 * The {@link CustomCubeBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Marcel Zillekens - Initial contribution
 */
public class CustomCubeBindingConstants {

    public static final String BINDING_ID = "customcube";

    // List of all Thing Type UIDs
    public final static ThingTypeUID CUBE_THING_TYPE = new ThingTypeUID(BINDING_ID, "customcube");
    public final static Collection<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Lists
            .newArrayList(CustomCubeBindingConstants.CUBE_THING_TYPE);

    // List of all Channel ids
    public final static String CHANNEL_CUBE_ROTATION = "rotation";
    public final static String CHANNEL_CUBE_FLIP = "flip";

    // List of all Parameters
    public final static String CUBE_PARAMETER_PORT = "internal_server_port";
}
