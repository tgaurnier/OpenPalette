/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * PaletteList.java                                                            *
 *                                                                             *
 * Copyright 2014 Tory Gaurnier <tory.gaurnier@linuxmail.org>                  *
 *                                                                             *
 * This program is free software; you can redistribute it and/or modify        *
 * it under the terms of the GNU Lesser General Public License as published by *
 * the Free Software Foundation; version 3.                                    *
 *                                                                             *
 * This program is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the               *
 * GNU Lesser General Public License for more details.                         *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public License    *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.       *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package com.torygaurnier.openpalette;


// Imports
import android.os.Bundle;

import android.preference.PreferenceFragment;
import android.preference.Preference;

import com.torygaurnier.util.FileUtil;


/**
 * class SettingsFragment
 *
 * View that displays any settings/configurations.
 */
public class SettingsFragment extends PreferenceFragment {
	private MainActivity activity;


	@Override
	public void onCreate(Bundle saved_state) {
		super.onCreate(saved_state);
		activity = (MainActivity)getActivity();
		addPreferencesFromResource(R.layout.settings);

		// Export data option
		Preference export_data = (Preference)findPreference("exportData");
		export_data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference pref) {
				ExportDialog export_dialog = new ExportDialog(activity);
				export_dialog.show();
				return true;
			}
		});

		if(PaletteList.getInstance().size() == 0) {
			export_data.setEnabled(false);
		}

		// Import data option
		Preference import_data = (Preference)findPreference("importData");
		import_data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference pref) {
				ImportDialog import_dialog = new ImportDialog(activity);
				import_dialog.show();
				return true;
			}
		});

		if(FileUtil.getFileNameList(Config.getInstance().getExportDir(), ".xml").size() == 0) {
			import_data.setEnabled(false);
		}

		activity.refresh();
	}


	public String getTitle() {
		return activity.getResources().getString(R.string.settings_fragment_title);
	}
}