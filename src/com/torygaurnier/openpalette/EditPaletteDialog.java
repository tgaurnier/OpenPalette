/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * EditPaletteDialog.java                                                      *
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
import android.content.*;
import android.view.*;
import android.widget.*;
import android.text.*;

import com.torygaurnier.dialog.SimpleDialog;


public class EditPaletteDialog extends SimpleDialog {
	private PaletteList palette_list;
	private Palette palette = null;
	private View view;
	private TextView error_label;
	private EditText input;


	public EditPaletteDialog(Activity activity) {
		super(activity);
		setOkText(R.string.ok);
		setCancelText(R.string.cancel);
		palette_list = PaletteList.getInstance();
	}


	/**
	 * Show dialog to create new palette.
	 */
	@Override
	public void show() {
		setTitle(R.string.new_palette_dialog_title);
		super.show();
	}


	/**
	 * Show dialog to edit palette.
	 */
	public void show(Palette _palette) {
		palette	= _palette;
		setTitle(R.string.edit_palette_dialog_title);
		super.show();
	}


	public void onOkClicked(DialogInterface dialog_interface, int id) {
		String name = new String(input.getText().toString());

		// If creating new palette, create palette and add to list
		if(palette == null) {
			palette_list.add(new Palette((MainActivity)activity, name));
		}

		// Else edit existing palette
		else {
			palette.setName(name);
			palette_list.refresh();
		}

		Data.getInstance().save();
		((MainActivity)activity).refresh();
	}


	protected View createView() {
		view		=	activity.getLayoutInflater().inflate(R.layout.edit_palette_view, null);
		error_label	=	(TextView)view.findViewById(R.id.paletteNameErrorLabel);
		input		=	(EditText)view.findViewById(R.id.paletteNameInput);

		// If editing palette, prefill input with name
		if(palette != null) {
			input.setText(palette.getName());
		} else { // Else disable OK until a valid name is provided
			setOkEnabled(false);
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
					setOkEnabled(false);
					error_label.setText("Palette must have a name");
					error_label.setVisibility(View.VISIBLE);
				}

				// If name already exists (but is not name being edited), disable OK and set message
				else if(palette_list.getPalette(input_string) != null &&
						!(palette != null && palette.getName().equals(input_string))) {
					setOkEnabled(false);
					error_label.setText("Palette with name already exists");
					error_label.setVisibility(View.VISIBLE);
				}

				else {
					setOkEnabled(true);
					error_label.setText("");
					error_label.setVisibility(View.GONE);
				}
			}
		});

		return view;
	}
}