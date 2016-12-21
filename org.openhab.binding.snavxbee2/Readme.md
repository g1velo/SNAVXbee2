# SNAVXbee2 for OpenHAB 2.x Binding

## ID Request

ID Requests from sensors received via the serial gateway are answered, when the bridge/gateway is configured and working. The bridge/gateway will check for a free id in the list of things already configured and in a list of already answered id requests in the current runtime. To reduce the chance that two nodes get the same id, the response is a random id. The random id is between 1 and 254, while already used ids are spared. 

After receiving the id the node begins to represent itself and after that can be discovered.

## Configuring the gateway

The binding currently supports the gateway attached to the local serial/USB/COM port. The tageway is made of one Xbee Device cofnigured as COORDINATOR.
The first step to configure the binding is to activate the gateway/bridge. 

So far the only option to configure the Gateway is to create an entry in a things file.

Things file under "conf/things/demo.things".

Local Serial Gateway:

```
Bridge snavxbee2:bridge:gateway[ serialPort="COM9", baudRate=115200 ] 
{
        /** define things connected to that bridge here */
}
```

As You can guess I have been using the The serial gateway works with a baud rate of 115.200. If you're using a different baud rate you need to add an additional parameter "baudRate":
you will need to make sure that the coordinator XBee device is set to this speed... 

  
  
## Configuring things

Assuming you have configured a bridge, the next step is to configure things. We use the place holder in the bridge configuration and fill it with content:

From Here you should be able to discover your Xbee Devices using the discovery process from Paper UI. 

For my ( Young ) development and testing , I  attached a TOSR02-T  device behind my Xbee  and I added a temperature sensor DS18B20
For Openhab To detect your device as tosr0xt, you will need to set the "device type identifier" "DD" to "CAFE0002"
you will have to use XCTU to configure the Xbee Device. 



## Supported gateways

- SerialGateway

## Supported sensors

- TOSR02-T as tosr0xT 
- More to come.   
