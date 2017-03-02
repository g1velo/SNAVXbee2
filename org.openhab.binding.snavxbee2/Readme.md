# SNAVXbee2 for OpenHAB 2.x Binding

## Configuring the XBee Devices 
I configured the Xbees devices using XCTU ( Under Windows ) with the follwing parameters ! 

Coordinator ( The one connected to OpenHab with serial connection ) 
```
 <setting command="ID">CAFE</setting>
      <setting command="SC">FFFF</setting>
      <setting command="SD">3</setting>
      <setting command="ZS">0</setting>
      <setting command="NJ">FF</setting>
      <setting command="DH">0</setting>
      <setting command="DL">FFFF</setting>
      <setting command="NI">COORDINATOR</setting>
      <setting command="NH">1E</setting>
      <setting command="BH">0</setting>
      <setting command="AR">FF</setting>
      <setting command="DD">30000</setting>
      <setting command="NT">96</setting>
      <setting command="NO">3</setting>
      <setting command="CR">3</setting>
      <setting command="PL">4</setting>
      <setting command="PM">1</setting>
      <setting command="EE">0</setting>
      <setting command="EO">0</setting>
      <setting command="KY"></setting>
      <setting command="NK"></setting>
      <setting command="BD">7</setting>
      <setting command="NB">0</setting>
      <setting command="SB">0</setting>
      <setting command="D7">1</setting>
      <setting command="D6">0</setting>
      <setting command="AP">1</setting>
      <setting command="AO">0</setting>
      <setting command="SP">20</setting>
      <setting command="SN">1</setting>
      <setting command="D0">1</setting>
      <setting command="D1">0</setting>
      <setting command="D2">0</setting>
      <setting command="D3">0</setting>
      <setting command="D4">0</setting>
      <setting command="D5">1</setting>
      <setting command="P0">1</setting>
      <setting command="P1">0</setting>
      <setting command="P2">0</setting>
      <setting command="PR">1FFF</setting>
      <setting command="LT">0</setting>
      <setting command="RP">28</setting>
      <setting command="DO">1</setting>
      <setting command="IR">0</setting>
      <setting command="IC">0</setting>
      <setting command="V+">0</setting>
```

The end device : 
```
 <setting command="ID">CAFE</setting>
      <setting command="SC">FFFF</setting>
      <setting command="SD">3</setting>
      <setting command="ZS">0</setting>
      <setting command="NJ">FF</setting>
      <setting command="JN">0</setting>
      <setting command="DH">0</setting>
      <setting command="DL">0</setting>
      <setting command="NI">LULUTEMP</setting>
      <setting command="NH">1E</setting>
      <setting command="BH">0</setting>
      <setting command="DD">CAFE0002</setting>
      <setting command="NT">3C</setting>
      <setting command="NO">0</setting>
      <setting command="CR">3</setting>
      <setting command="SE">E8</setting>
      <setting command="DE">E8</setting>
      <setting command="CI">11</setting>
      <setting command="PL">4</setting>
      <setting command="PM">1</setting>
      <setting command="EE">0</setting>
      <setting command="EO">0</setting>
      <setting command="KY"></setting>
      <setting command="BD">3</setting>
      <setting command="NB">0</setting>
      <setting command="SB">0</setting>
      <setting command="RO">3</setting>
      <setting command="D7">1</setting>
      <setting command="D6">0</setting>
      <setting command="CT">64</setting>
      <setting command="GT">3E8</setting>
      <setting command="CC">2B</setting>
      <setting command="SM">4</setting>
      <setting command="ST">1388</setting>
      <setting command="SP">20</setting>
      <setting command="SN">1</setting>
      <setting command="SO">0</setting>
      <setting command="PO">0</setting>
      <setting command="D0">1</setting>
      <setting command="D1">2</setting>
      <setting command="D2">0</setting>
      <setting command="D3">0</setting>
      <setting command="D4">0</setting>
      <setting command="D5">1</setting>
      <setting command="P0">1</setting>
      <setting command="P1">0</setting>
      <setting command="P2">0</setting>
      <setting command="PR">1FFF</setting>
      <setting command="LT">0</setting>
      <setting command="RP">28</setting>
      <setting command="DO">1</setting>
      <setting command="IR">0</setting>
      <setting command="IC">0</setting>
      <setting command="V+">0</setting>
```

In Our case the important parameters are : 
Defining coordinator as coordinator
defining End device as an end device ( Should work fine if defined as router ) 
The DD parameter defines device type connected and will define how openHab will use it.
The PANID has to be  indentical on all dev 



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



## Configuring OH2 channels in the items file : 

here is how to reference Xbee Port in the item file.  
So if you want to reference XBee Port DIO10/PWM0 you should put in the items file something like : 

    Switch  XbeeDIO11switch  "XbeeDIO11switch  "  (All)   { channel = "snavxbee2:sample:gateway:0013A20040E31560:PWM1DIO11" }
    
In order to update a XBee Port  it has to be configured as IOMode.DIGITAL_OUT_HIGH or IOMode.DIGITAL_OUT_LOW

XBee port     | OH2 channels
------------- | -------------
DIO0/AD0      |   AD0DIO0 
DIO1/AD1      |   AD1DIO1
DIO2/AD2      |   AD2DIO2
DIO3/AD3      |   AD3DIO3
DIO4/AD4      |   AD4DIO4
DIO5/AD5      |   AD5DIO5
DIO6          |   DIO6    
DIO7          |   DIO7
DIO8          |   DIO8    
DIO9          |   DIO9
DIO10/PWM0    |   PWM0DIO10
DIO11/PWM1    |   PWM1DIO11
DIO12         |   DIO12
DIO13         |   DIO13
DIO14         |   DIO14
DIO15         |   DIO15
DIO16         |   DIO16
DIO17         |   DIO17
DIO18         |   DIO18
DIO19         |   DIO19



## Supported gateways

- SerialGateway

## Supported sensors using Xbee Serial Port 

- TOSR02-T as tosr0xT using DD 'CAFE0002' 
- More to come.   

Device        | Xbee Device indentifier ( DD ) 
------------- | -------------
TOSR02-T      |   CAFE0002


## debug / trace mode
in karaf : 

```
log:set DEBUG  org.openhab.binding.snavxbee2
```
 
 or trace ( very verbose ) 
 
 ```
log:set TRACE org.openhab.binding.snavxbee2
```