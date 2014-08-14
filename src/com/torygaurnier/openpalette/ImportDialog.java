/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ImportDialog.java                                                               *
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.ArrayList;

import com.torygaurnier.dialog.SimpleDialog;
import com.torygaurnier.util.Msg;


public class ImportDialog extends SimpleDialog {
	private ArrayAdapter<String> adapter;
	private ArrayList<String> selected_list;


	public ImportDialog(Activity activity) {
		super(activity, false);
		setTitle(R.string.import_dialog_title);
		setOkText(R.string.ok);
		setCancelText(R.string.cancel);
		adapter			=	initAdapter();
		selected_list	=	new ArrayList<String>();
	}


	protected View createView() {
		final View root_view = activity.getLayoutInflater().inflate(R.layout.import_view, null);

		// Setup list view
		final ListView list_view = (ListView)root_view.findViewById(R.id.importListView);
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView adapter_view, View view, int position, long id) {
				String name = (String)adapter_view.getItemAtPosition(position);
				CheckBox check_box = (CheckBox)view.findViewById(R.id.importListItemCheckBox);
				check_box.setChecked(!check_box.isChecked());
				if(check_box.isChecked()) {
					selected_list.add(name);
				} else {
					selected_list.remove(name);
				}

				// Set if ok button should be enabled
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!selected_list.isEmpty());
			}
		});

		// Populate adapter
		File export_dir = new File(Config.getInstance().getExportDir());
		for(String name : Arrays.asList(
			export_dir.list(
				new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return (name.toLowerCase()).endsWith(".xml");
					}
				}
			)
		)) {
			adapter.add(name.substring(0, name.length() - 4));
		}

		// Select-all button onClick handler
		View select_all_button = root_view.findViewById(R.id.importViewSelectAll);
		select_all_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View button) {
				for(int i = 0; i < list_view.getChildCount(); i++) {
					View item = list_view.getChildAt(i);
					CheckBox check_box = (CheckBox)item.findViewById(
							R.id.importListItemCheckBox);
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
		View select_none_button = root_view.findViewById(R.id.importViewSelectNone);
		select_none_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View button) {
				for(int i = 0; i < list_view.getChildCount(); i++) {
					View item = list_view.getChildAt(i);
					CheckBox check_box = (CheckBox)item.findViewById(
							R.id.importListItemCheckBox);
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
		View select_invert_button = root_view.findViewById(R.id.importViewSelectInvert);
		select_invert_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View button) {
				for(int i = 0; i < list_view.getChildCount(); i++) {
					View item = list_view.getChildAt(i);
					CheckBox check_box = (CheckBox)item.findViewById(R.id.importListItemCheckBox);
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


	public void onOkClicked(DialogInterface dialog_interface, int id) {
		Data.getInstance().importPalettes(selected_list.iterator(), selected_list.size());
	}


	private ArrayAdapter initAdapter() {
		return new ArrayAdapter<String>(activity, R.layout.import_list_item,
					new ArrayList<String>()) {
			// This callback method sets up each item view in the list.
			@Override
			public View getView(int position, View convert_view, ViewGroup parent) {
				// Get root layout of pallettelistitem
				LayoutInflater inflater = (LayoutInflater)activity.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.import_list_item, parent, false);
				TextView text_view = (TextView)view.findViewById(R.id.importListItemText);
				text_view.setText((adapter.getItem(position)));

				return view;
			}
		};
	}
}