// ----------------------------------------------------------------------------
// Copyright 2006-2010, GeoTelematic Solutions, Inc.
// All rights reserved
// ----------------------------------------------------------------------------
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ----------------------------------------------------------------------------
// Change History:
//  2006/03/26  Martin D. Flynn
//     -Initial release
//  2006/03/31  Martin D. Flynn
//     -Added new status codes:
//      STATUS_INITIALIZED, STATUS_WAYMARK
//  2006/06/17  Martin D. Flynn
//      Copied from the OpenDMTP Server package.
//      OpenDMTP Protocol Definition v0.1.1 Conformance: These status-code values
//      conform to the definition specified by the OpenDMTP protocol and must
//      remain as specified here.  When extending the status code values to
//      encompass other purposes, it is recommended that values in the following
//      range be used: 0x0001 to 0xDFFF
//  2007/01/25  Martin D. Flynn
//     -Added new status codes:
//      STATUS_QUERY, STATUS_LOW_BATTERY, STATUS_OBC_FAULT, STATUS_OBC_RANGE,
//      STATUS_OBC_RPM_RANGE, STATUS_OBC_FUEL_RANGE, STATUS_OBC_OIL_RANGE,
//      STATUS_OBC_TEMP_RANGE, STATUS_MOTION_MOVING
//     -Changed "Code" descriptions to start their indexing at '1' (instead of '0')
//      since this string value is used to display to the user on various reports.
//  2007/03/11  Martin D. Flynn
//     -'GetCodeDescription' defaults to hex value of status code (or status code
//      name) if code is not found in the table.
//  2007/03/30  Martin D. Flynn
//     -Added new status code: STATUS_POWER_FAILURE
//  2007/04/15  Martin D. Flynn
//     -Added new status codes: STATUS_STATE_ENTER, STATUS_STATE_EXIT
//  2007/11/28  Martin D. Flynn
//     -Added new status codes: STATUS_EXCESS_BRAKING
//  2008/04/11  Martin D. Flynn
//     -Added "IsDigitalInput..." methods
//  2008/07/27  Martin D. Flynn
//     -Changed 'description' of digital inputs/outputs to start at '0' (instead of '1')
//     -Added "GetDigitalInputIndex".
//  2008/09/01  Martin D. Flynn
//     -Changed status 'description' index's to start at '0' to match the status 'name'.
//  2008/10/16  Martin D. Flynn
//     -Added the following status codes: STATUS_MOTION_IDLE, STATUS_POWER_RESTORED,
//      STATUS_WAYMARK_3,  STATUS_INPUT_ON_08/09, STATUS_INPUT_OFF_08/09,
//      STATUS_OUTPUT_ON_08/09, STATUS_OUTPUT_OFF_08/09
//  2009/01/01  Martin D. Flynn
//     -Internationalized StatusCode descriptions.
//  2009/05/01  Martin D. Flynn
//     -Added STATUS_GPS_EXPIRED, STATUS_GPS_FAILURE, STATUS_CONNECTION_FAILURE
//     -Added STATUS_IGNITION_ON, STATUS_IGNITION_OFF
//  2009/12/16  Martin D. Flynn
//     -Added Garmin GFMI status codes.
//  2010/04/11  Martin D. Flynn
//     -Added STATUS_WAYMARK_4..8, STATUS_NOTIFY, STATUS_IMPACT, STATUS_PANIC_*
//      STATUS_ASSIST_*, STATUS_MEDICAL_*, STATUS_OBC_INFO_#, STATUS_CONFIG_RESET, ...
// ----------------------------------------------------------------------------
package org.gc.gts.util;

public class StatusCodes
{

    /* Digital Input index for explicit STATUS_IGNITION_ON/STATUS_IGNITION_OFF codes */
    public static final int IGNITION_INPUT_INDEX        = 99;

// ----------------------------------------------------------------------------
// Reserved status codes: [E0-00 through FF-FF]
// Groups:
//      0xF0..  - Generic
//      0xF1..  - Motion
//      0xF2..  - Geofence
//      0xF4..  - Digital input/output
//      0xF6..  - Sensor input
//      0xF7..  - Temperature input
//      0xF9..  - OBC/J1708
//      0xFD..  - Device status
// ----------------------------------------------------------------------------

    public static final int STATUS_IGNORE               = -1;

// ----------------------------------------------------------------------------
// Reserved: 0x0000 to 0x0FFF
// No status code: 0x0000

    public static final int STATUS_NONE                 = 0x0000;

// ----------------------------------------------------------------------------
// Available: 0x1000 to 0xCFFF
    public static final int STATUS_1000                 = 0x1000;
    // ...
    public static final int STATUS_CFFF                 = 0xCFFF;

// ----------------------------------------------------------------------------
// Reserved: 0xD000 to 0xEFFF

    // Garmin GFMI interface [0xE100 - 0xE1FF]
    public static final int STATUS_GFMI_CMD_03          = 0xE103;   // 57603 send non-ack message
    public static final int STATUS_GFMI_CMD_04          = 0xE104;   // 57604 send ack message
    public static final int STATUS_GFMI_CMD_05          = 0xE105;   // 57605 send answerable message
    public static final int STATUS_GFMI_CMD_06          = 0xE106;   // 57606 send stop location
    public static final int STATUS_GFMI_CMD_08          = 0xE108;   // 57608 request stop ETA
    public static final int STATUS_GFMI_CMD_09          = 0xE109;   // 57609 set auto arrival criteria
    public static final int STATUS_GFMI_LINK_OFF        = 0xE110;   // GFMI Link lost
    public static final int STATUS_GFMI_LINK_ON         = 0xE111;   // GFMI Link established
    public static final int STATUS_GFMI_ACK             = 0xE1A0;   // received ACK
    public static final int STATUS_GFMI_MESSAGE         = 0xE1B1;   // received message
    public static final int STATUS_GFMI_MESSAGE_ACK     = 0xE1B2;   // received message ACK

// ----------------------------------------------------------------------------
// Generic codes: 0xF000 to 0xF0FF

    public static final int STATUS_INITIALIZED          = 0xF010;   // 61456
    // Description:
    //      General Status/Location information (event generated by some
    //      initialization function performed by the device).
    // Notes:
    //      - This contents of the payload must at least contain the current
    //      timestamp (and latitude and longitude if available).

    public static final int STATUS_LOCATION             = 0xF020;   // 61472
    // Description:
    //      General Status/Location information.  This status code indicates
    //      no more than just the location of the device at a particular time.
    // Notes:
    //      - This contents of the payload must at least contain the current
    //      timestamp, latitude, and longitude.

    public static final int STATUS_WAYMARK_0            = 0xF030;   // 61488
    public static final int STATUS_WAYMARK_1            = 0xF031;   // 61489
    public static final int STATUS_WAYMARK_2            = 0xF032;   // 61490
    public static final int STATUS_WAYMARK_3            = 0xF033;   // 61491
    public static final int STATUS_WAYMARK_4            = 0xF034;   // 61492
    public static final int STATUS_WAYMARK_5            = 0xF035;   // 61493
    public static final int STATUS_WAYMARK_6            = 0xF036;   // 61494
    public static final int STATUS_WAYMARK_7            = 0xF037;   // 61495
    public static final int STATUS_WAYMARK_8            = 0xF038;   // 61496
    // Description:
    //      General Status/Location information (event generated by manual user
    //      intervention at the device. ie. By pressing a 'Waymark' button).
    // Notes:
    //      - This status code can also be used to indicate a "PANIC" situation
    //      by redefining the description for the chosen code (this can be done
    //      in the "StatusCodes" table).
    //      - This contents of the payload must at least contain the current
    //      timestamp, latitude, and longitude.

    public static final int STATUS_QUERY                = 0xF040;   // 61504
    // Description:
    //      General Status/Location information (event generated by 'query'
    //      from the server).
    // Notes:
    //      - This contents of the payload must at least contain the current
    //      timestamp, latitude, and longitude.

    public static final int STATUS_NOTIFY               = 0xF044;   //
    // Description:
    //      General notification triggered by device operator

// ----------------------------------------------------------------------------
// Motion codes: 0xF100 to 0xF1FF

    public static final int STATUS_MOTION_START         = 0xF111;   // 61713
    // Description:
    //      Device start of motion
    // Notes:
    //      The definition of motion-start is provided by property PROP_MOTION_START

    public static final int STATUS_MOTION_IN_MOTION     = 0xF112;   // 61714
    // Description:
    //      Device in-motion interval
    // Notes:
    //      The in-motion interval is provided by property PROP_MOTION_IN_MOTION

    public static final int STATUS_MOTION_STOP          = 0xF113;   // 61715
    // Description:
    //      Device stopped motion
    // Notes:
    //      The definition of motion-stop is provided by property PROP_MOTION_STOP

    public static final int STATUS_MOTION_DORMANT       = 0xF114;   // 61716
    // Description:
    //      Device dormant interval (ie. not moving)
    // Notes:
    //      The dormant interval is provided by property PROP_MOTION_DORMANT

    public static final int STATUS_MOTION_IDLE          = 0xF116;   // 61718
    // Description:
    //      Device idle interval (ie. not moving, but engine may still be on)

    public static final int STATUS_MOTION_EXCESS_SPEED  = 0xF11A;   // 61722
    // Description:
    //      Device exceeded preset speed limit
    // Notes:
    //      The excess-speed threshold is provided by property PROP_MOTION_EXCESS_SPEED

    public static final int STATUS_MOTION_MOVING        = 0xF11C;   // 61724
    // Description:
    //      Device is moving
    // Notes:
    //      - This status code may be used to indicating that the device was moving
    //      at the time the event was generated. It is typically not associated
    //      with the status codes STATUS_MOTION_START, STATUS_MOTION_STOP, and
    //      STATUS_MOTION_IN_MOTION, and may be used independently of these codes.
    //      - This status code is typically used for devices that need to periodically
    //      report that they are moving, apart from the standard start/stop/in-motion
    //      events.

    public static final int STATUS_ODOM_0               = 0xF130;
    public static final int STATUS_ODOM_1               = 0xF131;
    public static final int STATUS_ODOM_2               = 0xF132;
    public static final int STATUS_ODOM_3               = 0xF133;
    public static final int STATUS_ODOM_4               = 0xF134;
    public static final int STATUS_ODOM_5               = 0xF135;
    public static final int STATUS_ODOM_6               = 0xF136;
    public static final int STATUS_ODOM_7               = 0xF137;
    // Description:
    //      Odometer value
    // Notes:
    //      The odometer limit is provided by property PROP_ODOMETER_#_LIMIT

    public static final int STATUS_ODOM_LIMIT_0         = 0xF140;
    public static final int STATUS_ODOM_LIMIT_1         = 0xF141;
    public static final int STATUS_ODOM_LIMIT_2         = 0xF142;
    public static final int STATUS_ODOM_LIMIT_3         = 0xF143;
    public static final int STATUS_ODOM_LIMIT_4         = 0xF144;
    public static final int STATUS_ODOM_LIMIT_5         = 0xF145;
    public static final int STATUS_ODOM_LIMIT_6         = 0xF146;
    public static final int STATUS_ODOM_LIMIT_7         = 0xF147;
    // Description:
    //      Odometer has exceeded a set limit
    // Notes:
    //      The odometer limit is provided by property PROP_ODOMETER_#_LIMIT

// ----------------------------------------------------------------------------
// Geofence: 0xF200 to 0xF2FF

    public static final int STATUS_GEOFENCE_ARRIVE      = 0xF210;   // 61968
    // Description:
    //      Device arrived at geofence
    // Notes:
    //      - Client may wish to include FIELD_GEOFENCE_ID in the event packet.

    public static final int STATUS_JOB_ARRIVE           = 0xF215;   // 61973
    // Description:
    //      Device arrived at job-site (typically driver entered)
    // Notes:
    //      - Client may wish to include FIELD_GEOFENCE_ID in the event packet.

    public static final int STATUS_GEOFENCE_DEPART      = 0xF230;   // 62000
    // Description:
    //      Device departed geofence
    // Notes:
    //      - Client may wish to include FIELD_GEOFENCE_ID in the event packet.

    public static final int STATUS_JOB_DEPART           = 0xF235;   // 62005
    // Description:
    //      Device departed job-site (typically driver entered)
    // Notes:
    //      - Client may wish to include FIELD_GEOFENCE_ID in the event packet.

    public static final int STATUS_GEOFENCE_VIOLATION   = 0xF250;   // 62032
    // Description:
    //      Geofence violation
    // Notes:
    //      - Client may wish to include FIELD_GEOFENCE_ID in the event packet.

    public static final int STATUS_CORRIDOR_VIOLATION   = 0xF258;   // 62040
    // Description:
    //      GeoCorridor violation

    public static final int STATUS_GEOFENCE_ACTIVE      = 0xF270;   // 62064
    // Description:
    //      Geofence now active
    // Notes:
    //      - Client may wish to include FIELD_GEOFENCE_ID in the event packet.

    public static final int STATUS_CORRIDOR_ACTIVE      = 0xF278;   // 62072
    // Description:
    //      GeoCorridor now active

    public static final int STATUS_GEOFENCE_INACTIVE    = 0xF280;   // 62080
    // Description:
    //      Geofence now inactive
    // Notes:
    //      - Client may wish to include FIELD_GEOFENCE_ID in the event packet.

    public static final int STATUS_CORRIDOR_INACTIVE    = 0xF288;   // 62088
    // Description:
    //      Geofence now inactive

    public static final int STATUS_STATE_ENTER          = 0xF2A0;   // 62112
    // Description:
    //      Device has entered a state

    public static final int STATUS_STATE_EXIT           = 0xF2B0;   // 62128
    // Description:
    //      Device has exited a state

// ----------------------------------------------------------------------------
// Digital input/output (state change): 0xF400 to 0xF4FF

    public static final int STATUS_INPUT_STATE          = 0xF400;   // 62464
    // Description:
    //      Current input ON state (bitmask)
    // Notes:
    //      - Client should include FIELD_INPUT_STATE in the event packet,
    //      otherwise this status code would have no meaning.

    public static final int STATUS_IGNITION_ON          = 0xF401;   // 62465
    // Description:
    //      Ignition turned ON
    // Notes:
    //      - This status code may be used to indicate that the ignition input
    //      turned ON.

    public static final int STATUS_INPUT_ON             = 0xF402;   // 62466
    // Description:
    //      Input turned ON
    // Notes:
    //      - Client should include FIELD_INPUT_ID in the event packet,
    //      otherwise this status code would have no meaning.
    //      - This status code may be used to indicate that an arbitrary input
    //      'thing' turned ON, and the 'thing' can be identified by the 'Input ID'.
    //      This 'ID' can also represent the index of a digital input.

    public static final int STATUS_IGNITION_OFF         = 0xF403;   // 62467
    // Description:
    //      Ignition turned OFF
    // Notes:
    //      - This status code may be used to indicate that the ignition input
    //      turned OFF.

    public static final int STATUS_INPUT_OFF            = 0xF404;   // 62468
    // Description:
    //      Input turned OFF
    // Notes:
    //      - Client should include FIELD_INPUT_ID in the event packet,
    //      otherwise this status code would have no meaning.
    //      - This status code may be used to indicate that an arbitrary input
    //      'thing' turned OFF, and the 'thing' can be identified by the 'Input ID'.
    //      This 'ID' can also represent the index of a digital input.

    public static final int STATUS_OUTPUT_STATE         = 0xF406;
    // Description:
    //      Current output ON state (bitmask)
    // Notes:
    //      - Client should include FIELD_OUTPUT_STATE in the event packet,
    //      otherwise this status code would have no meaning.

    public static final int STATUS_OUTPUT_ON            = 0xF408;
    // Description:
    //      Output turned ON
    // Notes:
    //      - Client should include FIELD_OUTPUT_ID in the event packet,
    //      otherwise this status code would have no meaning.
    //      - This status code may be used to indicate that an arbitrary output
    //      'thing' turned ON, and the 'thing' can be identified by the 'Output ID'.
    //      This 'ID' can also represent the index of a digital output.

    public static final int STATUS_OUTPUT_OFF           = 0xF40A;
    // Description:
    //      Output turned OFF
    // Notes:
    //      - Client should include FIELD_OUTPUT_ID in the event packet,
    //      otherwise this status code would have no meaning.
    //      - This status code may be used to indicate that an arbitrary output
    //      'thing' turned OFF, and the 'thing' can be identified by the 'Output ID'.
    //      This 'ID' can also represent the index of a digital output.

    public static final int STATUS_INPUT_ON_00          = 0xF420;   // 62496
    public static final int STATUS_INPUT_ON_01          = 0xF421;   // 62497
    public static final int STATUS_INPUT_ON_02          = 0xF422;   // 62498
    public static final int STATUS_INPUT_ON_03          = 0xF423;   // 62499
    public static final int STATUS_INPUT_ON_04          = 0xF424;   // 62500
    public static final int STATUS_INPUT_ON_05          = 0xF425;   // 62501
    public static final int STATUS_INPUT_ON_06          = 0xF426;   // 62502
    public static final int STATUS_INPUT_ON_07          = 0xF427;   // 62503
    public static final int STATUS_INPUT_ON_08          = 0xF428;   // 62504
    public static final int STATUS_INPUT_ON_09          = 0xF429;   // 62505
    public static final int STATUS_INPUT_ON_10          = 0xF42A;   // 62406
    public static final int STATUS_INPUT_ON_11          = 0xF42B;   // 62407
    public static final int STATUS_INPUT_ON_12          = 0xF42C;   // 62408
    public static final int STATUS_INPUT_ON_13          = 0xF42D;   // 62409
    public static final int STATUS_INPUT_ON_14          = 0xF42E;   // 62510
    public static final int STATUS_INPUT_ON_15          = 0xF42F;   // 62511
    // Description:
    //      Digital input state changed to ON
    //      0xFA28 through 0xFA2F reserved

    public static final int STATUS_INPUT_OFF_00         = 0xF440;   // 62528
    public static final int STATUS_INPUT_OFF_01         = 0xF441;   // 62529
    public static final int STATUS_INPUT_OFF_02         = 0xF442;   // 62530
    public static final int STATUS_INPUT_OFF_03         = 0xF443;   // 62531
    public static final int STATUS_INPUT_OFF_04         = 0xF444;   // 62532
    public static final int STATUS_INPUT_OFF_05         = 0xF445;   // 62533
    public static final int STATUS_INPUT_OFF_06         = 0xF446;   // 62534
    public static final int STATUS_INPUT_OFF_07         = 0xF447;   // 62535
    public static final int STATUS_INPUT_OFF_08         = 0xF448;   // 62536
    public static final int STATUS_INPUT_OFF_09         = 0xF449;   // 62537
    public static final int STATUS_INPUT_OFF_10         = 0xF44A;   // 62538
    public static final int STATUS_INPUT_OFF_11         = 0xF44B;   // 62539
    public static final int STATUS_INPUT_OFF_12         = 0xF44C;   // 62540
    public static final int STATUS_INPUT_OFF_13         = 0xF44D;   // 62541
    public static final int STATUS_INPUT_OFF_14         = 0xF44E;   // 62542
    public static final int STATUS_INPUT_OFF_15         = 0xF44F;   // 62543
    // Description:
    //      Digital input state changed to OFF
    //      0xFA48 through 0xFA4F reserved

    public static final int STATUS_OUTPUT_ON_00         = 0xF460;
    public static final int STATUS_OUTPUT_ON_01         = 0xF461;
    public static final int STATUS_OUTPUT_ON_02         = 0xF462;
    public static final int STATUS_OUTPUT_ON_03         = 0xF463;
    public static final int STATUS_OUTPUT_ON_04         = 0xF464;
    public static final int STATUS_OUTPUT_ON_05         = 0xF465;
    public static final int STATUS_OUTPUT_ON_06         = 0xF466;
    public static final int STATUS_OUTPUT_ON_07         = 0xF467;
    public static final int STATUS_OUTPUT_ON_08         = 0xF468;
    public static final int STATUS_OUTPUT_ON_09         = 0xF469;
    // Description:
    //      Digital output state set to ON
    //      0xFA68 through 0xFA6F reserved

    public static final int STATUS_OUTPUT_OFF_00        = 0xF480;
    public static final int STATUS_OUTPUT_OFF_01        = 0xF481;
    public static final int STATUS_OUTPUT_OFF_02        = 0xF482;
    public static final int STATUS_OUTPUT_OFF_03        = 0xF483;
    public static final int STATUS_OUTPUT_OFF_04        = 0xF484;
    public static final int STATUS_OUTPUT_OFF_05        = 0xF485;
    public static final int STATUS_OUTPUT_OFF_06        = 0xF486;
    public static final int STATUS_OUTPUT_OFF_07        = 0xF487;
    public static final int STATUS_OUTPUT_OFF_08        = 0xF488;
    public static final int STATUS_OUTPUT_OFF_09        = 0xF489;
    // Description:
    //      Digital output state set to OFF
    //      0xFA88 through 0xFA8F reserved

    public static final int STATUS_ELAPSED_00           = 0xF4A0;
    public static final int STATUS_ELAPSED_01           = 0xF4A1;
    public static final int STATUS_ELAPSED_02           = 0xF4A2;
    public static final int STATUS_ELAPSED_03           = 0xF4A3;
    public static final int STATUS_ELAPSED_04           = 0xF4A4;
    public static final int STATUS_ELAPSED_05           = 0xF4A5;
    public static final int STATUS_ELAPSED_06           = 0xF4A6;
    public static final int STATUS_ELAPSED_07           = 0xF4A7;
    // Description:
    //      Elapsed time
    //      0xFAA8 through 0xFAAF reserved
    // Notes:
    //      - Client should include FIELD_ELAPSED_TIME in the event packet,
    //      otherwise this status code would have no meaning.

    public static final int STATUS_ELAPSED_LIMIT_00     = 0xF4B0;   // 62640
    public static final int STATUS_ELAPSED_LIMIT_01     = 0xF4B1;   // 62641
    public static final int STATUS_ELAPSED_LIMIT_02     = 0xF4B2;   // 62642
    public static final int STATUS_ELAPSED_LIMIT_03     = 0xF4B3;   // 62643
    public static final int STATUS_ELAPSED_LIMIT_04     = 0xF4B4;   // 62644
    public static final int STATUS_ELAPSED_LIMIT_05     = 0xF4B5;   // 62645
    public static final int STATUS_ELAPSED_LIMIT_06     = 0xF4B6;   // 62646
    public static final int STATUS_ELAPSED_LIMIT_07     = 0xF4B7;   // 62647
    // Description:
    //      Elapsed timer has exceeded a set limit
    //      0xFAB8 through 0xFABF reserved
    // Notes:
    //      - Client should include FIELD_ELAPSED_TIME in the event packet,
    //      otherwise this status code would have no meaning.

// ----------------------------------------------------------------------------
// Analog/etc sensor values (extra data): 0xF600 to 0xF6FF

    public static final int STATUS_SENSOR32_0           = 0xF600;
    public static final int STATUS_SENSOR32_1           = 0xF601;
    public static final int STATUS_SENSOR32_2           = 0xF602;
    public static final int STATUS_SENSOR32_3           = 0xF603;
    public static final int STATUS_SENSOR32_4           = 0xF604;
    public static final int STATUS_SENSOR32_5           = 0xF605;
    public static final int STATUS_SENSOR32_6           = 0xF606;
    public static final int STATUS_SENSOR32_7           = 0xF607;
    // Description:
    //      32-bit unsigned sensor value
    // Notes:
    //      - Client should include FIELD_SENSOR32 in the event packet,
    //      otherwise this status code would have no meaning.
    //      - The server must be able to convert this 32-bit value to something
    //      meaningful to the user.  This can be done using the following formula:
    //         Actual_Value = ((double)Sensor32_Value * <Gain>) + <Offset>;
    //      Where <Gain> & <Offset> are user configurable values provided at setup.
    //      For instance: Assume Sensor32-0 contains a temperature value that can
    //      have a range of -40.0C to +125.0C.  The client would encode -14.7C
    //      by adding 40.0 and multiplying by 10.0.  The resulting value would be
    //      253.  The server would then be configured to know how to convert this
    //      value back into the proper temperature using the above formula by
    //      substituting 0.1 for <Gain>, and -40.0 for <Offset>: eg.
    //          -14.7 = ((double)253 * 0.1) + (-40.0);

    public static final int STATUS_SENSOR32_RANGE_0     = 0xF620;
    public static final int STATUS_SENSOR32_RANGE_1     = 0xF621;
    public static final int STATUS_SENSOR32_RANGE_2     = 0xF622;
    public static final int STATUS_SENSOR32_RANGE_3     = 0xF623;
    public static final int STATUS_SENSOR32_RANGE_4     = 0xF624;
    public static final int STATUS_SENSOR32_RANGE_5     = 0xF625;
    public static final int STATUS_SENSOR32_RANGE_6     = 0xF626;
    public static final int STATUS_SENSOR32_RANGE_7     = 0xF627;
    // Description:
    //      32-bit unsigned sensor value out-of-range violation
    // Notes:
    //      - Client should include FIELD_SENSOR32 in the event packet,
    //      otherwise this status code would have no meaning.

// ----------------------------------------------------------------------------
// Temperature sensor values (extra data): 0xF700 to 0xF7FF

    public static final int STATUS_TEMPERATURE_0        = 0xF710;
    public static final int STATUS_TEMPERATURE_1        = 0xF711;
    public static final int STATUS_TEMPERATURE_2        = 0xF712;
    public static final int STATUS_TEMPERATURE_3        = 0xF713;
    public static final int STATUS_TEMPERATURE_4        = 0xF714;
    public static final int STATUS_TEMPERATURE_5        = 0xF715;
    public static final int STATUS_TEMPERATURE_6        = 0xF716;
    public static final int STATUS_TEMPERATURE_7        = 0xF717;
    // Description:
    //      Temperature value
    // Notes:
    //      - Client should include at least the field FIELD_TEMP_AVER in the
    //      event packet, and may also wish to include FIELD_TEMP_LOW and
    //      FIELD_TEMP_HIGH.

    public static final int STATUS_TEMPERATURE_RANGE_0  = 0xF730;
    public static final int STATUS_TEMPERATURE_RANGE_1  = 0xF731;
    public static final int STATUS_TEMPERATURE_RANGE_2  = 0xF732;
    public static final int STATUS_TEMPERATURE_RANGE_3  = 0xF733;
    public static final int STATUS_TEMPERATURE_RANGE_4  = 0xF734;
    public static final int STATUS_TEMPERATURE_RANGE_5  = 0xF735;
    public static final int STATUS_TEMPERATURE_RANGE_6  = 0xF736;
    public static final int STATUS_TEMPERATURE_RANGE_7  = 0xF737;
    // Description:
    //      Temperature value out-of-range [low/high/average]
    // Notes:
    //      - Client should include at least one of the fields FIELD_TEMP_AVER,
    //      FIELD_TEMP_LOW, or FIELD_TEMP_HIGH.

    public static final int STATUS_TEMPERATURE          = 0xF7F1;
    // Description:
    //      All temperature averages [aver/aver/aver/...]

// ----------------------------------------------------------------------------
// Miscellaneous

    public static final int STATUS_LOGIN                = 0xF811;
    // Description:
    //      Generic 'login'

    public static final int STATUS_LOGOUT               = 0xF812;
    // Description:
    //      Generic 'logout'

    public static final int STATUS_CONNECT              = 0xF821;
    // Description:
    //      Connect/Hook/On

    public static final int STATUS_DISCONNECT           = 0xF822;
    // Description:
    //      Disconnect/Drop/Off

    public static final int STATUS_ACK                  = 0xF831;
    // Description:
    //      Acknowledge

    public static final int STATUS_NAK                  = 0xF832;
    // Description:
    //      Negative Acknowledge

    public static final int STATUS_PANIC_ON             = 0xF841;
    // Description:
    //      Panic condition activated

    public static final int STATUS_PANIC_OFF            = 0xF842;
    // Description:
    //      Panic condition deactivated

    public static final int STATUS_ASSIST_ON            = 0xF851;
    // Description:
    //      Assist condition activated

    public static final int STATUS_ASSIST_OFF           = 0xF852;
    // Description:
    //      Assist condition deactivated

    public static final int STATUS_MEDICAL_ON           = 0xF861;
    // Description:
    //      Medical Call condition activated

    public static final int STATUS_MEDICAL_OFF          = 0xF862;
    // Description:
    //      Medical Call condition deactivated

// ----------------------------------------------------------------------------
// OBC/J1708 status: 0xF900 to 0xF9FF

    public static final int STATUS_OBC_INFO_0           = 0xF900;
    public static final int STATUS_OBC_INFO_1           = 0xF901;
    public static final int STATUS_OBC_INFO_2           = 0xF902;
    public static final int STATUS_OBC_INFO_3           = 0xF903;
    public static final int STATUS_OBC_INFO_4           = 0xF904;
    // Description:
    //      OBC/J1708 information packet

    public static final int STATUS_OBC_FAULT            = 0xF911;
    // Description:
    //      OBC/J1708 fault code occurred.
    // Notes:
    //      - Client should include the field FIELD_OBC_J1708_FAULT

    public static final int STATUS_OBC_RANGE            = 0xF920;
    // Description:
    //      Generic OBC/J1708 value out-of-range
    // Notes:
    //      - Client should include at least one of the FIELD_OBC_xxxxx fields.

    public static final int STATUS_OBC_RPM_RANGE        = 0xF922;
    // Description:
    //      OBC/J1708 RPM out-of-range
    // Notes:
    //      - Client should include the field FIELD_OBC_ENGINE_RPM.

    public static final int STATUS_OBC_FUEL_RANGE       = 0xF924;
    // Description:
    //      OBC/J1708 Fuel level out-of-range (ie. to low)
    // Notes:
    //      - Client should include the field FIELD_OBC_FUEL_LEVEL.

    public static final int STATUS_OBC_OIL_RANGE        = 0xF926;
    // Description:
    //      OBC/J1708 Oil level out-of-range (ie. to low)

    public static final int STATUS_OBC_TEMP_RANGE       = 0xF928;
    // Description:
    //      OBC/J1708 Temperature out-of-range
    // Notes:
    //      - Client should include at least one of the FIELD_OBC_xxxxx fields
    //      which indicates an OBC temperature out-of-range.

    public static final int STATUS_EXCESS_BRAKING       = 0xF930;
    // Description:
    //      Excessive acceleration/deceleration detected

    public static final int STATUS_IMPACT               = 0xF941;
    // Description:
    //      Excessive acceleration/deceleration detected

// ----------------------------------------------------------------------------
// Internal device status

    public static final int STATUS_LOW_BATTERY          = 0xFD10;   // 64784
    // Description:
    //      Low battery indicator

    public static final int STATUS_POWER_FAILURE        = 0xFD13;
    // Description:
    //      Power failure indicator (or running on internal battery)

    public static final int STATUS_POWER_RESTORED       = 0xFD15;
    // Description:
    //      Power restored (after previous failure)

    public static final int STATUS_GPS_EXPIRED          = 0xFD21;   // 64801
    // Description:
    //      GPS fix expiration detected

    public static final int STATUS_GPS_FAILURE          = 0xFD22;   // 64802
    // Description:
    //      GPS receiver failure detected

    public static final int STATUS_CONNECTION_FAILURE   = 0xFD31;   // 64817
    // Description:
    //      Connection failure detected

    public static final int STATUS_CONFIG_RESET         = 0xFD41;   //
    // Description:
    //      Configuration reset

}

