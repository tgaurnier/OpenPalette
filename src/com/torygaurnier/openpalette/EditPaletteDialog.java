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


import android.app.FragmentManager;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import android.text.TextWatcher;
import android.text.Editable;

import android.os.Bundle;


public class EditPaletteDialog extends DialogFragment {
	private final MainActivity activity;
	private final PaletteList palette_list;
	private final Data data;
	private Palette palette = null;
	private AlertDialog dialog;
	private View view;
	private TextView error_label;
	private EditText input;
	private int title = 0;

	public EditPaletteDialog(MainActivity _activity, PaletteList _palette_list, Data _data) {
		activity		=	_activity;
		palette_list	=	_palette_list;
		data			=	_data;
	}


	/**
	 * Show dialog to edit palette
	 */
	public void show(Palette p) {
		palette	=	p;
		title	=	R.string.edit_palette_dialog_title;

		super.show(activity.getFragmentManager(), "edit_palette_dialog");
	}


	/**
	 * Show dialog to create a new palette
	 */
	public void show() {
		palette = null;
		super.show(activity.getFragmentManager(), "new_palette_dialog");
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		// Get error label
		error_label = (TextView)view.findViewById(R.id.paletteNameErrorLabel);

		// Get text input
		input = (EditText)view.findViewById(R.id.paletteNameInput);

		// If editing palette, prefill input with name
		if(palette != null) {
			input.setText(palette.getName());
		} else { // Else disable OK until a valid name is provided
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		}

		// Set a text watcher to validate entry
		input.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable editable) {}

			@Override
			public void beforeTextChanged(CharSequence chars, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence chars, int start, int before, int count) {
				String input_string = (input.getText()).toString();
				// If input is empty, disable OK button
				if(input_string.isEmpty()) {
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
					error_label.setText("Palette must have a name");
					error_label.setVisibility(View.VISIBLE);
				}

				// If name already exists (but is not name being edited), disable OK and set message
				else if(palette_list.getPalette(input_string) != null &&
						!(palette != null && palette.getName().equals(input_string))) {
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
					error_label.setText("Palette with name already exists");
					error_label.setVisibility(View.VISIBLE);
				}

				else {
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
					error_label.setText("");
					error_label.setVisibility(View.GONE);
				}
			}
		});

		return super.onCreateView(inflater, container, savedState);
	}


	@Override
	public Dialog onCreateDialog(Bundle savedState) {
		// Build the AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle((title == 0) ? R.string.new_palette_dialog_title : title);
		builder.setMessage(R.string.edit_palette_dialog_message);

		// Set view for dialog
		view = activity.getLayoutInflater().inflate(R.layout.edit_palette_view, null);
		builder.setView(view);

		builder.setPositiveButton(
			R.string.ok_button,
			new DialogInterface.OnClickListener() {
				// User clicked OK, create new palette
				public void onClick(DialogInterface dialog_interface, int id) {
					String name = new String(input.getText().toString());

					// If creating new palette, create palette and add to list
					if(palette == null) {
						palette_list.add(new Palette(activity, name));
					}

					// Else edit existing palette
					else {
						palette.setName(name);
						palette_list.refresh();
					}

					data.save();
				}
			}
		);

		builder.setNegativeButton(
			R.string.cancel_button,
			new DialogInterface.OnClickListener() {
				// User cancelled the dialog, don't create palette
				public void onClick(DialogInterface dialog_interface, int id) {
					dialog_interface.cancel();
				}
			}
		);

		// Create the AlertDialog object
		dialog = builder.create();
		dialog.show(); // Required to be able to disable OK button

		// Return dialog
		return dialog;
	}


	// When dialog is destroyed refresh the main activity
	@Override
	public void onDestroy() {
		palette = null;
		activity.refresh();
		super.onDestroy();
	}
}