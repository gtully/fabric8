/**
 *  Copyright 2005-2015 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.gateway.handlers.detecting.protocol.openwire.codec.v1;

import io.fabric8.gateway.handlers.detecting.protocol.openwire.codec.OpenWireFormat;
import io.fabric8.gateway.handlers.detecting.protocol.openwire.command.ConsumerId;
import io.fabric8.gateway.handlers.detecting.protocol.openwire.codec.BooleanStream;
import io.fabric8.gateway.handlers.detecting.protocol.openwire.command.ActiveMQDestination;
import io.fabric8.gateway.handlers.detecting.protocol.openwire.command.DataStructure;
import io.fabric8.gateway.handlers.detecting.protocol.openwire.command.MessageDispatchNotification;
import io.fabric8.gateway.handlers.detecting.protocol.openwire.command.MessageId;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import java.io.IOException;


/**
 * Marshalling code for Open Wire Format for MessageDispatchNotificationMarshaller
 *
 *
 * NOTE!: This file is auto generated - do not modify!
 *        Modify the 'apollo-openwire-generator' module instead.
 *
 */
public class MessageDispatchNotificationMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure we marshal
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return MessageDispatchNotification.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new object instance
     */
    public DataStructure createObject() {
        return new MessageDispatchNotification();
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o the object to un-marshal
     * @param dataIn the data input stream to build the object from
     * @throws IOException
     */
    public void tightUnmarshal(OpenWireFormat wireFormat, Object o, DataByteArrayInputStream dataIn, BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);

        MessageDispatchNotification info = (MessageDispatchNotification)o;
        info.setConsumerId((ConsumerId)tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setDestination((ActiveMQDestination)tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setDeliverySequenceId(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setMessageId((MessageId)tightUnmarsalNestedObject(wireFormat, dataIn, bs));

    }


    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    public int tightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) throws IOException {

        MessageDispatchNotification info = (MessageDispatchNotification)o;

        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getConsumerId(), bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getDestination(), bs);
        rc += tightMarshalLong1(wireFormat, info.getDeliverySequenceId(), bs);
        rc += tightMarshalNestedObject1(wireFormat, (DataStructure)info.getMessageId(), bs);

        return rc + 0;
    }

    /**
     * Write a object instance to data output stream
     *
     * @param o the instance to be marshaled
     * @param dataOut the output stream
     * @throws IOException thrown if an error occurs
     */
    public void tightMarshal2(OpenWireFormat wireFormat, Object o, DataByteArrayOutputStream dataOut, BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);

        MessageDispatchNotification info = (MessageDispatchNotification)o;
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getConsumerId(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getDestination(), dataOut, bs);
        tightMarshalLong2(wireFormat, info.getDeliverySequenceId(), dataOut, bs);
        tightMarshalNestedObject2(wireFormat, (DataStructure)info.getMessageId(), dataOut, bs);

    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o the object to un-marshal
     * @param dataIn the data input stream to build the object from
     * @throws IOException
     */
    public void looseUnmarshal(OpenWireFormat wireFormat, Object o, DataByteArrayInputStream dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);

        MessageDispatchNotification info = (MessageDispatchNotification)o;
        info.setConsumerId((ConsumerId)looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setDestination((ActiveMQDestination)looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setDeliverySequenceId(looseUnmarshalLong(wireFormat, dataIn));
        info.setMessageId((MessageId)looseUnmarsalNestedObject(wireFormat, dataIn));

    }


    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object o, DataByteArrayOutputStream dataOut) throws IOException {

        MessageDispatchNotification info = (MessageDispatchNotification)o;

        super.looseMarshal(wireFormat, o, dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getConsumerId(), dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getDestination(), dataOut);
        looseMarshalLong(wireFormat, info.getDeliverySequenceId(), dataOut);
        looseMarshalNestedObject(wireFormat, (DataStructure)info.getMessageId(), dataOut);

    }
}
