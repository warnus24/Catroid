package org.catrobat.catroid.devices.arduino.common.firmata.wrapper;

/**
 * Filters messages
  */
public interface IMessageFilter {

    /**
     * Return true if message is allowed and should not be filtered
     */
    boolean isAllowed(MessageWithProperties data);
}
