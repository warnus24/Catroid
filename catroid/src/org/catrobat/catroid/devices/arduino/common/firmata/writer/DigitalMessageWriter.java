package org.catrobat.catroid.devices.arduino.common.firmata.writer;

import org.catrobat.catroid.devices.arduino.common.firmata.message.DigitalMessage;
import org.catrobat.catroid.devices.arduino.common.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.*;

/**
 * MessageWriter for DigitalMessage
 */
public class DigitalMessageWriter implements IMessageWriter<DigitalMessage> {

    public static final int COMMAND = 0x90;

    public void write(DigitalMessage message, ISerial serial) throws SerialException {
        serial.write(COMMAND | ENCODE_CHANNEL(message.getPort()));
        serial.write(LSB(message.getValue()));
        serial.write(MSB(message.getValue()));
    }
}
