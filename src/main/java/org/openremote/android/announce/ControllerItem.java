package org.openremote.android.announce;

import org.fourthline.cling.model.meta.Device;

public class ControllerItem {

    Device controller;

    public ControllerItem(Device controller) {
        this.controller = controller;
    }

    public Device getController() {
        return controller;
    }

    public String getPresentationURI() {
        return getController().getDetails().getPresentationURI().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControllerItem that = (ControllerItem) o;
        return controller.equals(that.controller);
    }

    @Override
    public int hashCode() {
        return controller.hashCode();
    }

    @Override
    public String toString() {
        return getController().getDetails() != null && getController().getDetails().getFriendlyName() != null
                ? getController().getDetails().getFriendlyName()
                : getController().getDisplayString();
    }
}