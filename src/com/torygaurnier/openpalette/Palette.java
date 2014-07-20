/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright 2014 Tory Gaurnier                                                *
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


import android.widget.ArrayAdapter;
import android.widget.TextView;

import android.content.DialogInterface;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

import java.lang.Iterable;

import com.mobeta.android.dslv.DragSortListView;


public class Palette implements Iterable<CustomColor> {
	private final MainActivity activity;
	private final ArrayAdapter<CustomColor> adapter;
	private final ArrayList<CustomColor> list;
	private String name = new String();

	public Palette(MainActivity _activity) {
		activity	=	_activity;
		list		=	new ArrayList<CustomColor>();
		adapter		=	initAdapter();
		adapter.setNotifyOnChange(true);
	}


	public Palette(MainActivity _activity, String palette_name) {
		activity	=	_activity;
		name		=	palette_name;
		list		=	new ArrayList<CustomColor>();
		adapter		=	initAdapter();
		adapter.setNotifyOnChange(true);
	}


	@Override
	public Iterator<CustomColor> iterator() {
		return new Iterator<CustomColor>() {
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < adapter.getCount();
			}

			@Override
			public CustomColor next() {
				return getColor(i++);
			}

			// Iterator shouldn't be able to remove colors
			@Override
			public void remove() {}
		};
	}


	private DragSortArrayAdapter initAdapter() {
		return new DragSortArrayAdapter<CustomColor>(activity, R.layout.palette_item, list) {
			// This callback method sets up each item view in the list
			@Override
			public View getView(int position, View convert_view, ViewGroup parent) {
				// Get root layout of palletteitem
				LayoutInflater inflater = (LayoutInflater)activity.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.palette_item, parent, false);
				// Set color view background color
				View color_view = view.findViewById(R.id.paletteItemColorView);
				color_view.setBackgroundColor(list.get(position).getInt());
				// Get text view for hex
				TextView hex_view = (TextView)view.findViewById(R.id.paletteItemHexText);
				hex_view.setText(list.get(position).getHex());
				// Get text view for color name
				TextView name_view = (TextView)view.findViewById(R.id.paletteItemNameText);
				if(list.get(position).getName() != null) {
					name_view.setText(list.get(position).getName());
				}

				return view;
			}
		};
	}


	/**
	 * Remove selected palette.
	 */
	public void remove(final CustomColor color) {
		// Confirm if color should be deleted
		new ConfirmationDialog(activity, R.string.confirm_delete_color_message,
				R.string.confirm_delete_color_title)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					adapter.remove(color);
					Data.getInstance().save();
				}

			})
			.setNegativeButton(R.string.no, null)
			.show();
	}


	/**
	 * Adds color to PaletteAdapter
	 */
	public void add(CustomColor color) {
		adapter.add(color);
	}


	public ArrayAdapter<CustomColor> getAdapter() {
		return adapter;
	}


	/**
	 * Returns color at position, or null if position not found.
	 */
	public CustomColor getColor(int position) {
		if(adapter.getCount() > position) {
			return adapter.getItem(position);
		}

		return null;
	}


	public String getName() {
		return name;
	}


	public void setName(String palette_name) {
		name = palette_name;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		Palette palette = (Palette)obj;

		if((palette.getName()).equals(this.getName())) {
			return true;
		}

		else return false;
	}
}