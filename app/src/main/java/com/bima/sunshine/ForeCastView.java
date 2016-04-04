package com.bima.sunshine;

import java.util.ArrayList;

/**
 * Created by bimapw on 4/5/16.
 */
public interface ForeCastView {
    void showLoading(boolean show);
    void setAdapter(ArrayList<String> foreCastArray);
}
