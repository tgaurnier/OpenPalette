/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ExportDialog.java                                                     *
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


import android.os.Bundle;
import android.app.*;
import android.view.*;
import android.content.*;
import android.widget.*;

import java.util.ArrayList;

import com.torygaurnier.dialog.SimpleDialog;


public class ExportDialog extends SimpleDialog {
	private ArrayAdapter<Palette> adapter;
	private ArrayList<Palette> selected_list;
	private PaletteList palette_list;


	public ExportDialog(Activity activity) {
		super(activity, false);
		setTitle(R.string.export_dialog_title);
		setOkText(R.string.ok);
		setCancelText(R.string.cancel);
		adapter			=	initAdapter();
		palette_list	=	PaletteList.getInstance();
		selected_list	=	new ArrayList();
	}


	public void onOkClicked(DialogInterface dialog_interface, int id) {
		Data.getInstance().exportPalettes(selected_list);
	}


	protected View createView() {
		final View root_view = activity.getLayoutInflater().inflate(R.layout.export_view, null);

		// Setup list view
		final ListView list_view = (ListView)root_view.findViewById(R.id.exportListView);
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView adapter_view, View view, int position, long id) {
				Palette palette = (Palette)adapter_view.getItemAtPosition(position);
				CheckBox check_box = (CheckBox)view.findViewById(R.id.exportListItemCheckBox);
				check_box.setChecked(!check_box.isChecked());
				if(check_box.isChecked()) {
					selected_list.add(palette);
				} else {
					selected_list.remove(palette);
				}

				// Set if ok button should be enabled
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!selected_list.isEmpty());
			}
		});

		// Populate adapter
		for(Palette palette : palette_list) {
			adapter.add(palette);
		}

		// Select-all button onClick handler
		View select_all_button = root_view.findViewById(R.id.exportViewSelectAll);
		select_all_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View button) {
				for(int i = 0; i < list_view.getChildCount(); i++) {
					View item = list_view.getChildAt(i);
					CheckBox check_box = (CheckBox)item.findViewById(
							R.id.exportListItemCheckBox);
					if(!check_box.isChecked()) {
						check_box.setChecked(true);
						selected_list.add(adapter.getItem(i));
					}
				}

				// Set if ok button should be enabled
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!selected_list.isEmpty());
			}
		});

		// Select-none button onClick handler
		View select_none_button = root_view.findViewById(R.id.exportViewSelectNone);
		select_none_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View button) {
				for(int i = 0; i < list_view.getChildCount(); i++) {
					View item = list_view.getChildAt(i);
					CheckBox check_box = (CheckBox)item.findViewById(
							R.id.exportListItemCheckBox);
					if(check_box.isChecked()) {
						check_box.setChecked(false);
						selected_list.remove(adapter.getItem(i));
					}
				}

				// Set if ok button should be enabled
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!selected_list.isEmpty());
			}
		});

		// Select-invert button onClick handler
		View select_invert_button = root_view.findViewById(R.id.exportViewSelectInvert);
		select_invert_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View button) {
				for(int i = 0; i < list_view.getChildCount(); i++) {
					View item = list_view.getChildAt(i);
					CheckBox check_box = (CheckBox)item.findViewById(
							R.id.exportListItemCheckBox);
					check_box.toggle();
					if(check_box.isChecked()) selected_list.add(adapter.getItem(i));
					else selected_list.remove(adapter.getItem(i));
				}

				// Set if ok button should be enabled
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!selected_list.isEmpty());
			}
		});

		return root_view;
	}


	private ArrayAdapter initAdapter() {
		return new ArrayAdapter<Palette>(activity, R.layout.export_list_item,
					new ArrayList<Palette>()) {
			// This callback method sets up each item view in the list.
			@Override
			public View getView(int position, View convert_view, ViewGroup parent) {
				// Get root layout of pallettelistitem
				LayoutInflater inflater = (LayoutInflater)activity.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.export_list_item, parent, false);
				TextView text_view = (TextView)view.findViewById(R.id.exportListItemText);
				text_view.setText((adapter.getItem(position)).getName());

				return view;
			}
		};
	}
}