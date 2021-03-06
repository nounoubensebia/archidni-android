package com.archidni.archidni.Model.Interests;

import com.archidni.archidni.R;
import com.archidni.archidni.UiUtils.SelectorItem;


public class ParkingItem extends SelectorItem {


    public ParkingItem(int id, String name) {
        super(id, name);
    }

    @Override
    public int getIconDisabled() {
        return R.drawable.ic_parking_disabled_24dp;
    }

    @Override
    public int getMarkerIcon() {
        return R.drawable.ic_parking_enabled_24dp;
    }

    @Override
    public int getIconEnabled() {
        return R.drawable.ic_parking_enabled_24dp;
    }

    @Override
    public int getColor() {
        return R.color.colorBlue;
    }

    @Override
    public int getTheme() {
        return R.style.TransportMeanTheme1;
    }

    @Override
    public int getCoordinateDrawable() {
        return R.drawable.ic_coordinate_transport_mean_1;
    }


}
