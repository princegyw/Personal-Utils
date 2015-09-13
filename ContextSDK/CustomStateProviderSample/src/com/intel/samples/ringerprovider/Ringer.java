package com.intel.samples.ringerprovider;

import com.intel.context.item.Item;

/**
 * This is the POJO Item class that represents the item we
 * want to publish with our custom state provider.
 */
public final class Ringer extends Item {
    private String ringerMode;

    /**
     * This method must be implemented to return the URN
     * identifier of the custom state provider. Make sure
     * the URN string you return is the same as stated in
     * the XML descriptor file of the custom state provider
     * project.
     */
    @Override
    public String getContextType() {
        return "urn:mycompany:context:device:ringer";
    }

    public String getRingerMode() {
    	return ringerMode;
    }
    
    public void setRingerMode(String ringerMode) {
    	this.ringerMode = ringerMode;
    }
}
