/**
 * 
 */
package com.intel.samples.contextsensingapiflow.listener;

import com.intel.context.item.Item;
import com.intel.context.sensing.ContextTypeListener;

/**
 *
 */
public interface IApplicationListener extends ContextTypeListener {
    Item getLastKnownItem();
    
    void setLastKnownItem(Item item);
}
