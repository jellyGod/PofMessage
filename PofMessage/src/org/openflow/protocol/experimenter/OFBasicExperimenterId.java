/**
*    Copyright 2011, Big Switch Networks, Inc. 
*    Originally created by David Erickson & Rob Sherwood, Stanford University
* 
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package org.openflow.protocol.experimenter;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.openflow.protocol.Instantiable;

/**
 * Basic subclass of OFVendorId that works with any vendor data format where
 * the data begins with an integral data type value.
 * 
 * @author Rob Vaterlaus (rob.vaterlaus@bigswitch.com)
 */
public class OFBasicExperimenterId extends OFExperimenterId {
    
    /**
     * The size of the data type value at the beginning of all vendor
     * data associated with this vendor id. The data type size must be
     * either 1, 2, 4 or 8.
     */
    protected int dataTypeSize;
    
    /**
     * Map of the vendor data types that have been registered for this
     * vendor id.
     */
    protected Map<Long, OFBasicExperimenterDataType> dataTypeMap =
            new HashMap<Long, OFBasicExperimenterDataType>();
    
    /**
     * Construct an OFVendorId that where the vendor data begins
     * with a data type value whose size is dataTypeSize.
     * @param id the id of the vendor, typically the OUI of a vendor
     *     prefixed with 0.
     * @param dataTypeSize the size of the integral data type value
     *     at the beginning of the vendor data. The value must be the
     *     size of an integeral data type (i.e. either 1,2,4 or 8).
     */
    public OFBasicExperimenterId(int id, int dataTypeSize) {
        super(id);
        assert (dataTypeSize == 1) || (dataTypeSize == 2) ||
               (dataTypeSize == 4) || (dataTypeSize == 8);
        this.dataTypeSize = dataTypeSize;
    }

    /**
     * Get the size of the data type value at the beginning of the vendor
     * data. OFBasicVendorId assumes that this value is common across all of
     * the vendor data formats associated with a given vendor id.
     * @return data type size
     */
    public int getDataTypeSize() {
        return dataTypeSize;
    }
    
    /**
     * Register a vendor data type with this vendor id.
     * @param experimenterDataType
     */
    public void registerExperimenterDataType(OFBasicExperimenterDataType experimenterDataType) {
        dataTypeMap.put(experimenterDataType.getTypeValue(), experimenterDataType);
    }
    
    /**
     * Lookup the OFVendorDataType instance that has been registered with
     * this vendor id.
     * 
     * @param experimenterDataType the integer code that was parsed from the 
     * @return OFExperimenterDataType
     */
    public OFExperimenterDataType lookupExperimenterDataType(int experimenterDataType) {
        return dataTypeMap.get(experimenterDataType);
    }

    /**
     * This function parses enough of the data from the buffer to be able
     * to determine the appropriate OFVendorDataType for the data. It is meant
     * to be a reasonably generic implementation that will work for most
     * formats of vendor extensions. If the vendor data doesn't fit the
     * assumptions listed below, then this method will need to be overridden
     * to implement custom parsing.
     * 
     * This implementation assumes that the vendor data begins with a data
     * type code that is used to distinguish different formats of vendor
     * data associated with a particular vendor ID.
     * The exact format of the data is vendor-defined, so we don't know how
     * how big the code is (or really even if there is a code). This code
     * assumes that the common case will be that the data does include
     * an initial type code (i.e. so that the vendor can have multiple
     * message/data types) and that the size is either 1, 2 or 4 bytes.
     * The size of the initial type code is configured by the subclass of
     * OFVendorId.
     * 
     * @param data the channel buffer containing the vendor data.
     * @param length the length to the end of the enclosing message
     * @return the OFVendorDataType that can be used to instantiate the
     *         appropriate subclass of OFVendorData.
     */
    public OFExperimenterDataType parseExperimenterDataType(ByteBuffer data, int length) {
        OFExperimenterDataType experimenterDataType = null;
        
        // Parse out the type code from the vendor data.
        long dataTypeValue = 0;
        if ((length == 0) || (length >= dataTypeSize)) {
            switch (dataTypeSize) {
                case 1:
                    dataTypeValue = data.get();
                    break;
                case 2:
                    dataTypeValue = data.getShort();
                    break;
                case 4:
                    dataTypeValue = data.getInt();
                    break;
                case 8:
                    dataTypeValue = data.getLong();
                    break;
                default:
                    // This would be indicative of a coding error where the
                    // dataTypeSize was specified incorrectly. This should have been
                    // caught in the constructor for OFVendorId.
                    assert false;
            }
            
            experimenterDataType = dataTypeMap.get(dataTypeValue);
        }
        
        // If we weren't able to parse/map the data to a known OFVendorDataType,
        // then map it to a generic vendor data type.
        if (experimenterDataType == null) {
            experimenterDataType = new OFBasicExperimenterDataType(dataTypeValue,
                new Instantiable<OFExperimenterData>() {
                    @Override
                    public OFExperimenterData instantiate() {
                        return new OFByteArrayExperimenterData();
                    }
                }
            );
        }
        
        return experimenterDataType;
    }

}