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


import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import java.lang.Iterable;

import com.torygaurnier.util.Msg;


/**
 * PaletteList
 *
 * This is a thread-safe singleton object, it contains an ArrayAdapter for the Palette List View in
 * the drawer, and an ArrayList for the adapter.
 */
public class PaletteList implements Iterable<Palette> {
	private final MainActivity activity;
	private final ArrayAdapter<Palette> adapter;
	private final ArrayList<Palette> list;
	private int selected_pos = -1;
	private static PaletteList instance = null; // Keep track of singleton instance


	private PaletteList(MainActivity _activity) {
		activity	=	_activity;
		list		=	new ArrayList<Palette>();
		adapter		=	initAdapter();
		adapter.setNotifyOnChange(true);
	}


	public static PaletteList getInstance() {
		return instance;
	}


	/**
	 * Helper method to clear internal list from static init() method.
	 */
	private void init() {
		adapter.clear();
	}


	/**
	 * Initialize PaletteList singleton, if it is already instantiated then clear internal list.
	 * Make synchronized so that it is thread safe.
	 */
	public static synchronized PaletteList init(MainActivity _activity) {
		if(instance == null) {
			instance = new PaletteList(_activity);
		} else instance.init();

		return instance;
	}


	public static synchronized void destroy() {
		instance = null;
	}


	@Override
	public Iterator<Palette> iterator() {
		return new Iterator<Palette>() {
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < adapter.getCount();
			}

			@Override
			public Palette next() {
				return getPalette(i++);
			}

			// Iterator shouldn't be able to remove Palettes
			@Override
			public void remove() {}
		};
	}


	public ArrayAdapter<Palette> getAdapter() {
		return adapter;
	}


	/**
	 * Notify adapter that data may have changed, so it can refresh views attached to it.
	 */
	public void refresh() {
		adapter.notifyDataSetChanged();
	}


	/**
	 * Removes palette with name, returns true on success, false if palette with name doesn't exist.
	 */
	public boolean remove(String name) {
		for(Palette palette : this) {
			if((palette.getName()).equals(name)) {
				adapter.remove(palette);
				activity.refresh();
				Data.getInstance().save();
				return true;
			}
		}

		return false;
	}


	/**
	 * Remove selected palette.
	 */
	public void removeSelectedPalette() {
		Palette palette = adapter.getItem(selected_pos);
		adapter.remove(palette);
		if(!(selected_pos == 0 && adapter.getCount() > 0)) selected_pos--;
		activity.refresh();
		Data.getInstance().save();
	}


	/**
	 * Sets selection to matching palette.
	 */
	public void setSelectedPalette(Palette palette) {
		boolean found = false;
		for(int i = 0; i < adapter.getCount(); i++) {
			if(palette.equals(adapter.getItem(i))) {
				selected_pos	=	i;
				found			=	true;

				// Remember selected palette for when app is closed
				Config.getInstance().setSelectedPalette(palette.getName());
			}
		}

		if(!found) {
			Msg.log(Msg.WARNING, "PaletteList.setSelectedPalette(Palette)",
					"Palette '" + palette.getName() + "' not in list");
		}
	}


	/**
	 * Sets selection to palette at position.
	 */
	public void setSelectedPosition(int position) {
		if(position < adapter.getCount()) {
			selected_pos = position;
			adapter.notifyDataSetChanged();

			// Remember selected palette for when app is closed
			Config.getInstance().setSelectedPalette(getPalette(position).getName());
		} else {
			Msg.log(Msg.WARNING, "PaletteList.setSelectedPalette(int)",
					"Palette at position '" + position + "' does not exist");
		}
	}


	/**
	 * Returns position of selected palette.
	 */
	public int getSelectedPosition() {
		return selected_pos;
	}


	/**
	 * Returns number of palettes in list.
	 */
	public int size() {
		return adapter.getCount();
	}


	/**
	 * Returns selected palette, or null if no palette exists or is selected.
	 */
	public Palette getSelectedPalette() {
		if(adapter.getCount() == 0 || selected_pos == -1) return null;
		else return getPalette(selected_pos);
	}


	/**
	 * Returns palette at position, or null if palette doesn't exist
	 */
	public Palette getPalette(int position) {
		if(adapter.getCount() > position) {
			return adapter.getItem(position);
		} else return null;
	}


	/**
	 * Returns palette with name, or null if palette doesn't exist
	 */
	public Palette getPalette(String name) {
		for(Palette palette : this) {
			if(palette.getName().equals(name)) {
				return palette;
			}
		}

		return null;
	}


	/**
	 * Get position of palette in ArrayList, if palette not located returns -1.
	 */
	public int getPalettePosition(Palette palette) {
		for(int i = 0; i < adapter.getCount(); i++) {
			if(palette.equals(adapter.getItem(i))) {
				return i;
			}
		}

		return -1;
	}


	/**
	 * Adds palette, sorts list, and selects new palette.
	 */
	public void add(Palette palette) {
		adapter.add(palette);
		adapter.sort(new PaletteComparator());
		selected_pos = getPalettePosition(palette);
	}


	private ArrayAdapter<Palette> initAdapter() {
		return new ArrayAdapter<Palette>(activity, R.layout.palette_list_item, list) {
			// This callback method sets up each item view in the list.
			@Override
			public View getView(int position, View convert_view, ViewGroup parent) {
				// Get root layout of pallettelistitem
				LayoutInflater inflater = (LayoutInflater)activity.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.palette_list_item, parent, false);
				// Get text view, and set it's text to palette name
				TextView text_view = (TextView)view.findViewById(R.id.paletteListItemText);
				text_view.setText((list.get(position)).getName());

				// Set color based on selected state
				if(selected_pos == position) {
					text_view.setBackgroundColor(
						activity.getResources().getColor(android.R.color.holo_blue_light)
					);
				} else {
					text_view.setBackgroundColor(
						activity.getResources().getColor(android.R.color.transparent)
					);
				}

				return view;
			}
		};
	}
}