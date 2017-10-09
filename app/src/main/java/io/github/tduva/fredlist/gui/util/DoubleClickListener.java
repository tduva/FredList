package io.github.tduva.fredlist.gui.util;

import android.view.View;

/**
 * Created by tduva on 18.08.2017.
 */

public class DoubleClickListener implements View.OnClickListener {

    private static final long DOUBLE_CLICK_DELAY = 400;

    private long lastClicked;

    @Override
    public void onClick(View view) {
        if (System.currentTimeMillis() - lastClicked < DOUBLE_CLICK_DELAY) {
            onDoubleClick(view);
        } else {
            onSingleClick(view);
            lastClicked = System.currentTimeMillis();
        }
    }

    /**
     * Called when no other click has been done within a short time window. Does not wait for
     * double-click to be executed, so it also triggers on the first click of a double-click.
     *
     * @param view
     */
    public void onSingleClick(View view) {
        // To be overridden
    }

    /**
     * Called when another click has been done within a short time window.
     *
     * @param view
     */
    public void onDoubleClick(View view) {
        // To be overridden
    }

}
