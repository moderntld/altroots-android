/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.otakuchiyan.dnsman;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.Preference;
import android.util.AttributeSet;

public class AppVersionPreference extends Preference {
    public Handler mHandler = new Handler();
    protected int mClickCount;

    private final Runnable mResetCounterRunnable = new Runnable() {

        @Override
        public void run() {
            mClickCount = 0;
        }
    };

    public AppVersionPreference(final Context context) {
        this(context, null);
    }

    public AppVersionPreference(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.preferenceStyle);
    }

    public AppVersionPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        final PackageManager pm = context.getPackageManager();
        try {
            final PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            setTitle(info.applicationInfo.loadLabel(pm));
            setSummary(info.versionName);
        } catch (final PackageManager.NameNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    protected void onClick() {
        mHandler.removeCallbacks(mResetCounterRunnable);
        mClickCount++;
        if (mClickCount >= 5) {
            final Context context = getContext();
            if (context != null) {
                mClickCount = 0;
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("很慚愧，就做了一點微小的工作，謝謝大家。")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        }
    }

}