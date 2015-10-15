package org.openremote.android.announce;

import android.app.Activity;
import android.widget.ArrayAdapter;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerAnnounceListener extends DefaultRegistryListener {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerAnnounceListener.class);

    public static final DeviceType CONTROLLER_DEVICE_TYPE = new DeviceType("openremote", "Controller", 1);

    final protected Activity activity;
    final protected ArrayAdapter<ControllerItem> controllerItems;

    public ControllerAnnounceListener(Activity activity, ArrayAdapter<ControllerItem> controllerItems) {
        this.activity = activity;
        this.controllerItems = controllerItems;
    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        deviceAdded(device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        deviceRemoved(device);
    }

    public void deviceAdded(final Device device) {
        if (!device.getType().implementsVersion(CONTROLLER_DEVICE_TYPE))
            return;

        activity.runOnUiThread(new Runnable() {
            public void run() {
                ControllerItem controllerItem = new ControllerItem(device);
                int position = controllerItems.getPosition(controllerItem);
                if (position >= 0) {
                    // Device already in the list, re-set new value at same position
                    controllerItems.remove(controllerItem);
                    controllerItems.insert(controllerItem, position);
                } else {
                    controllerItems.add(controllerItem);
                }
            }
        });
    }

    public void deviceRemoved(final Device device) {
        if (!device.getType().implementsVersion(CONTROLLER_DEVICE_TYPE))
            return;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                controllerItems.remove(new ControllerItem(device));
            }
        });
    }
}

