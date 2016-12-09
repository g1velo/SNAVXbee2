/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.snavxbee2.handler;

import static org.openhab.binding.snavxbee2.SNAVXbee2BindingConstants.CHANNEL_1;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SNAVXbee2Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Stephan NAVARRO - Initial contribution
 */
public class SNAVXbee2Handler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(SNAVXbee2Handler.class);

    public SNAVXbee2Handler(Thing thing) {
        super(thing);
        logger.debug("in {} method : contructor(Thing) ", this.getClass());
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        logger.debug("in {} method : handlecommand for channel {} ", this.getClass(), channelUID);

        if (channelUID.getId().equals(CHANNEL_1)) {
            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
            logger.debug(" Coudl be handling command ----------------------------------");
            logger.debug(getThing().getStatusInfo() + "  " + getThing().getUID() + getThing().getThingTypeUID()
                    + " Could be handling command ----------------------------------");

        }

        // getBridgeHandler().

    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        logger.debug("@@@@@@@@@@@@@@@@_____________  in {} initialise()  for .getBridgeUID {} :  getThingTypeUID : {} ",
                this.getClass(), thing.getBridgeUID(), thing.getThingTypeUID());

        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

}
