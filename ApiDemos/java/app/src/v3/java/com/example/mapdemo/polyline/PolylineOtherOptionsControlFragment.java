package com.example.mapdemo.polyline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.example.mapdemo.R;

/**
 * Fragment with clickability, geodesic, and visibility UI controls for Polylines, to be used in
 * ViewPager.
 */
public class PolylineOtherOptionsControlFragment extends PolylineControlFragment
    implements View.OnClickListener {

    private CheckBox clickabilityCheckBox;
    private CheckBox geodesicCheckBox;
    private CheckBox visibilityCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.polyline_other_options_control_fragment, container, false);
        clickabilityCheckBox = view.findViewById(R.id.clickabilityCheckBox);
        clickabilityCheckBox.setOnClickListener(this);
        geodesicCheckBox = view.findViewById(R.id.geodesicCheckBox);
        geodesicCheckBox.setOnClickListener(this);
        visibilityCheckBox = view.findViewById(R.id.visibilityCheckBox);
        visibilityCheckBox.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (polyline == null) {
            return;
        }
        if (view == clickabilityCheckBox) {
            polyline.setClickable(clickabilityCheckBox.isChecked());
        } else if (view == geodesicCheckBox) {
            polyline.setGeodesic(geodesicCheckBox.isChecked());
        } else if (view == visibilityCheckBox) {
            polyline.setVisible(visibilityCheckBox.isChecked());
        }
    }

    @Override
    public void refresh() {
        clickabilityCheckBox.setChecked((polyline != null) && polyline.isClickable());
        geodesicCheckBox.setChecked((polyline != null) && polyline.isGeodesic());
        visibilityCheckBox.setChecked((polyline != null) && polyline.isVisible());
    }
}

