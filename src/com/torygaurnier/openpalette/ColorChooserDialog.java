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


import java.lang.Integer;

import android.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Context;

import android.widget.RelativeLayout;
import android.widget.EditText;
import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.View;

import android.text.Editable;
import android.text.TextWatcher;

import android.os.Bundle;
import android.graphics.Color;

import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ValueBar;


public class ColorChooserDialog extends DialogFragment {
	private final MainActivity activity;
	private final Data data;
	private CustomColor color	=	null;
	private Palette palette		=	null;

	// Used to keep Listeners from changing items that are being edited by user
	private boolean picker_changing_text	=	false;
	private boolean hex_changing_text		=	false;
	private boolean rgb_changing_text		=	false;


	public ColorChooserDialog(MainActivity _activity, Data _data) {
		activity	=	_activity;
		data		=	_data;
	}


	/**
	 * Open dialog for new color, recieves palette to be added to.
	 */
	public void show(Palette _palette) {
		palette = _palette;
		super.show(activity.getFragmentManager(), "color_chooser_dialog");
	}


	/**
	 * Open dialog to edit color already added to a palette.
	 */
	public void show(CustomColor _color) {
		color = _color;
		super.show(activity.getFragmentManager(), "color_chooser_dialog");
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_color_dialog_title);

		// Inflate layout color chooser view and set to builder
		LayoutInflater layout_inflater = getActivity().getLayoutInflater();
		View view = layout_inflater.inflate(R.layout.color_chooser_view, null);
		builder.setView(view);

		// Get views by IDs
		final EditText name_input		=	(EditText)view.findViewById(R.id.colorNameInput);
		final EditText hex_input		=	(EditText)view.findViewById(R.id.hexInput);
		final EditText red_input		=	(EditText)view.findViewById(R.id.redInput);
		final EditText grn_input		=	(EditText)view.findViewById(R.id.greenInput);
		final EditText blu_input		=	(EditText)view.findViewById(R.id.blueInput);
		final ColorPicker color_picker	=	(ColorPicker)view.findViewById(R.id.colorPicker);
		final SaturationBar sat_bar		=	(SaturationBar)view.findViewById(R.id.saturationBar);
		final ValueBar val_bar			=	(ValueBar)view.findViewById(R.id.valueBar);

		// Connect bars to color picker
		color_picker.addSaturationBar(sat_bar);
		color_picker.addValueBar(val_bar);

		// If not editing color, then don't show old color in center, and set color to black,
		// otherwise show old color in center and also set it as the starting color
		if(color == null) {
			color_picker.setShowOldCenterColor(false);
			color_picker.setColor(Color.GREEN);
			hex_input.setText("00ff00");
			red_input.setText("0");
			grn_input.setText("255");
			blu_input.setText("0");
		} else {
			color_picker.setOldCenterColor(color.getInt());
			color_picker.setColor(color.getInt());
			if(color.getName() != null) name_input.setText(color.getName());
			hex_input.setText((color.getHex()).substring(1));
			red_input.setText(Integer.toString(color.getRed()));
			grn_input.setText(Integer.toString(color.getGreen()));
			blu_input.setText(Integer.toString(color.getBlue()));
		}

		// When color is changed via picker, set text fields
		color_picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
			@Override
			public void onColorChanged(int color) {
				String hex_value	=	(Integer.toHexString(color)).substring(2);
				String red_value	=	Integer.toString(Color.red(color));
				String grn_value	=	Integer.toString(Color.green(color));
				String blu_value	=	Integer.toString(Color.blue(color));

				picker_changing_text = true; // Keep text changed listener from activating

				if(!hex_changing_text) hex_input.setText(hex_value);
				if(!rgb_changing_text) {
					red_input.setText(red_value);
					grn_input.setText(grn_value);
					blu_input.setText(blu_value);
				}

				picker_changing_text = false;
			}
		});

		// Add listener for when HEX value is changed
		hex_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable editable) {}

			@Override
			public void beforeTextChanged(CharSequence chars, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence chars, int start, int before, int count) {
				if(!picker_changing_text && hex_input.length() == 6) {
					hex_changing_text = true;
					color_picker.setColor(Color.parseColor("#" + chars));
					hex_changing_text = false;
				}
			}
		});

		// Listener for Red Green and Blue text inputs
		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable editable) {}

			@Override
			public void beforeTextChanged(CharSequence chars, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence chars, int start, int before, int count) {
				int red, grn, blu;

				// Get int values from rgb fields
				try { red = Integer.parseInt((red_input.getText()).toString()); }
				catch(NumberFormatException e) { red = 0; }

				try { grn = Integer.parseInt((grn_input.getText()).toString()); }
				catch(NumberFormatException e) { grn = 0; }

				try { blu = Integer.parseInt((blu_input.getText()).toString()); }
				catch(NumberFormatException e) { blu = 0; }

				// Validate values in rgb fields
				if(red < 0) red = 0;
				else if(red > 255) red = 255;
				if(grn < 0) grn = 0;
				else if(grn > 255) grn = 255;
				if(blu < 0) blu = 0;
				else if(blu > 255) blu = 255;

				// If these rgb fields were not auto-changed by color picker, then update the picker
				if(!picker_changing_text) {
					rgb_changing_text = true; // Prevent picker from re-changing
					color_picker.setColor(Color.rgb(red, grn, blu));
					rgb_changing_text = false;
				}
			}
		};

		// Set listener for when Red, Green, or Blue value is changed
		red_input.addTextChangedListener(textWatcher);
		grn_input.addTextChangedListener(textWatcher);
		blu_input.addTextChangedListener(textWatcher);

		builder.setPositiveButton(
			R.string.ok_button,
			new DialogInterface.OnClickListener() {
				// User clicked OK, add color to palette
				public void onClick(DialogInterface dialog, int id) {
					// If color is null, we are adding a new color to palette
					if(color == null) {
						// If name input is not empty, then add color with name, else just add color
						if(name_input.length() > 0) {
							palette.add(
								new CustomColor(
									"#" + (hex_input.getText()).toString(),
									(name_input.getText()).toString()
								)
							);
						} else {
							palette.add(new CustomColor("#" + (hex_input.getText()).toString()));
						}
					} else { // Else we are editing an existing color
						if(name_input.length() > 0) {
							color.setName((name_input.getText()).toString());
						} else {
							color.setName("");
						}

						color.setHex("#" + (hex_input.getText()).toString());
					}

					data.save();
				}
			}
		);

		builder.setNegativeButton(
			R.string.cancel_button,
			new DialogInterface.OnClickListener() {
				// User cancelled the dialog, don't create palette
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			}
		);

		// Create the AlertDialog object and return it
		return builder.create();
	}


	@Override
	public void onDestroy() {
		color	=	null;
		palette	=	null;
		activity.refresh();
		super.onDestroy();
	}
}