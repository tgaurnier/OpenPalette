/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ColorChooserDialog.java                                                     *
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
import android.widget.*;
import android.view.*;
import android.text.*;
import android.graphics.Color;

import java.lang.Integer;

import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ValueBar;

import com.torygaurnier.dialog.SimpleDialog;



public class ColorChooserDialog extends SimpleDialog {
	private HexColor color		=	null;
	private Palette palette		=	null;
	private EditText name_input;
	private EditText hex_input;
	private EditText red_input;
	private EditText grn_input;
	private EditText blu_input;
	private ColorPicker color_picker;
	private SaturationBar sat_bar;
	private ValueBar val_bar;

	// Used to keep Listeners from changing items that are being edited by user
	private boolean picker_changing_text	=	false;
	private boolean hex_changing_text		=	false;
	private boolean rgb_changing_text		=	false;


	public ColorChooserDialog(Activity activity) {
		super(activity);
		setTitle(R.string.color_chooser_dialog_title);
		setOkText(R.string.ok);
		setCancelText(R.string.cancel);
	}


	/**
	 * Open dialog for new color, recieves palette to be added to.
	 */
	public void show(Palette _palette) {
		palette = _palette;
		show();
	}


	/**
	 * Open dialog to edit color already added to a palette.
	 */
	public void show(HexColor _color) {
		color = _color;
		show();
	}


	public void onOkClicked(DialogInterface dialog_interface, int id) {
		// If color is null, we are adding a new color to palette
		if(color == null) {
			// If name input is not empty, then add color with name, else just add color
			if(name_input.length() > 0) {
				palette.add(
					new HexColor(
						"#" + (hex_input.getText()).toString(),
						(name_input.getText()).toString()
					)
				);
			} else {
				palette.add(new HexColor("#" + (hex_input.getText()).toString()));
			}
		} else { // Else we are editing an existing color
			if(name_input.length() > 0) {
				color.setName((name_input.getText()).toString());
			} else {
				color.setName("");
			}

			color.setHex("#" + (hex_input.getText()).toString());
		}

		Data.getInstance().save();
		((MainActivity)activity).refresh();
	}


	protected View createView() {
		// Inflate layout color chooser view and set to builder
		LayoutInflater layout_inflater = activity.getLayoutInflater();
		View view = layout_inflater.inflate(R.layout.color_chooser_view, null);

		// Get views by IDs
		name_input		=	(EditText)view.findViewById(R.id.colorNameInput);
		hex_input		=	(EditText)view.findViewById(R.id.hexInput);
		red_input		=	(EditText)view.findViewById(R.id.redInput);
		grn_input		=	(EditText)view.findViewById(R.id.greenInput);
		blu_input		=	(EditText)view.findViewById(R.id.blueInput);
		color_picker	=	(ColorPicker)view.findViewById(R.id.colorPicker);
		sat_bar			=	(SaturationBar)view.findViewById(R.id.saturationBar);
		val_bar			=	(ValueBar)view.findViewById(R.id.valueBar);

		// Connect bars to color picker
		color_picker.addSaturationBar(sat_bar);
		color_picker.addValueBar(val_bar);

		// If not editing color, then don't show old color in center, and set color to green,
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

		return view;
	}
}