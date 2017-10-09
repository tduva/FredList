package io.github.tduva.fredlist.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by tduva on 17.10.2016.
 */

public class NoLineBreak implements TextWatcher {

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void afterTextChanged(Editable s) {
        /*
         * The loop is in reverse for a purpose,
         * each replace or delete call on the Editable will cause
         * the afterTextChanged method to be called again.
         * Hence the return statement after the first removal.
         * http://developer.android.com/reference/android/text/TextWatcher.html#afterTextChanged(android.text.Editable)
         */
        for (int i = s.length() - 1; i > 0; i--) {
            if (s.charAt(i) == '\n') {
                s.replace(i, i + 1, " ");
                return;
            }
        }
    }
}
