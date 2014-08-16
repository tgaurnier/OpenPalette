/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Settings.java                                                               *
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

//TODO: MOVE SETTINGS STUFF INTO CONFIG CLASS
package com.torygaurnier.openpalette;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.torygaurnier.util.Msg;


public class Settings {
	private static Settings instance		=	null;
	private MainActivity activity			=	null;
	private SharedPreferences preferences	=	null;


	private class Key {
		final static String SELECTED_PALETTE = "SelectedPalette";
	}


	public static synchronized void init(MainActivity activity) {
		if(instance == null) {
			instance = new Settings(activity);
		}
	}


	public static synchronized Settings getInstance() {
		return instance;
	}


	public static synchronized void destroy() {
		instance = null;
	}


	/**
	 * Used to remember last used palette when opening app. Returns null if no selected palette was
	 * saved.
	 */
	public String getSelectedPalette() {
		return preferences.getString(Key.SELECTED_PALETTE, null);
	}


	/**
	 * Used to remember selected palette on close.
	 */
	public void setSelectedPalette(String name) {
		if(!name.equals(getSelectedPalette())) {
			(preferences.edit())
				.putString(Key.SELECTED_PALETTE, name)
				.commit();
		}
	}


	private Settings(MainActivity activity) {
		this.activity	=	activity;
		preferences		=	activity.getPreferences(Context.MODE_PRIVATE);
	}
}