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


import android.content.Context;

import android.util.Xml;
import android.util.Log;

import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;


/**
 * Data
 *
 * This object handles reading/writing user data to file.
 */
public class Data {
	private final MainActivity activity;
	private PaletteList palette_list;
	private static Data instance = null;


	public static Data getInstance() {
		return instance;
	}


	public static synchronized Data init(MainActivity _activity, PaletteList _palette_list) {
		if(instance == null) {
			instance = new Data(_activity, _palette_list);
		}

		return instance;
	}


	private Data(MainActivity _activity, PaletteList _palette_list) {
		activity		=	_activity;
		palette_list	=	_palette_list;
	}


	/**
	 * Detect if user data already exists, and return true or false.
	 */
	public boolean exists() {
		FileInputStream file = null;

		try {
			file = activity.openFileInput("Palettes.xml");
		} catch(FileNotFoundException e) {
			return false;
		} finally {
			if(file != null) {
				try {
					file.close();
				} catch(IOException e) {
					Log.e("Data.exists()", "Failed to close file input stream", e);
					Toast.makeText(activity, "Data.exists() failed to close file",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		return true;
	}


	/**
	 * Loads xml data into PaletteList singleton.
	 */
	public void load() {
		FileInputStream file	=	null;
		XmlPullParser xml		=	null;

		try {
			file	=	activity.openFileInput("Palettes.xml");
			xml		=	Xml.newPullParser();
			xml.setInput(file, "UTF-8");

			// Iterate through XML adding values to PaletteList
			Palette palette = null;
			CustomColor color = null;
			for(int type = xml.getEventType(); type != XmlPullParser.END_DOCUMENT;
					type = xml.next()) {
				switch(type) {
					case XmlPullParser.START_TAG:
						if((xml.getName()).equals("Palette")) {
							palette = new Palette(activity, xml.getAttributeValue(0));
						}

						else if((xml.getName()).equals("Color")) {
							color = new CustomColor();
							if(xml.getAttributeCount() > 0) {
								color.setName(xml.getAttributeValue(0));
							}
						}

						break;

					case XmlPullParser.END_TAG:
						if((xml.getName()).equals("Palette")) {
							palette_list.add(palette);
						}

						else if((xml.getName()).equals("Color")) {
							palette.add(color);
						}

						break;

					case XmlPullParser.TEXT:
						if((xml.getText()).startsWith("#")) {
							if(color != null) {
								color.setHex(xml.getText());
							}
						}

						break;
				}
			}
		} catch(FileNotFoundException e) {
			Log.e("Data.load()", "Failed to open file", e);
			Toast.makeText(activity, "Data.load() failed to open file", Toast.LENGTH_SHORT).show();
		} catch(XmlPullParserException e) {
			Log.e("Data.load()", "XmlPullParser error", e);
			Toast.makeText(activity, "Data.load() XmlPullParser error", Toast.LENGTH_SHORT).show();
		} catch(IOException e) {
			Log.e("Data.load()", "IO error with XmlPullParser", e);
			Toast.makeText(activity, "Data.load() XmlPullParser error", Toast.LENGTH_SHORT).show();
		} finally {
			if(file != null) {
				try {
					file.close();
				} catch(IOException e) {
					Log.e("Data.load()", "Failed to close file input stream", e);
					Toast.makeText(activity, "Data.load() failed to close file",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}


	/**
	 * Saves palettes from PaletteList singleton into xml.
	 */
	public void save() {
		FileOutputStream file	=	null;
		XmlSerializer xml		=	null;

		try {
			file	=	activity.openFileOutput("Palettes.xml", Context.MODE_PRIVATE);
			xml		=	Xml.newSerializer();
			xml.setOutput(file, "UTF-8");
			xml.startDocument("UTF-8", true);
			xml.startTag(null, "Palettes");

			// Iterate through palettes adding to xml
			for(Palette palette : palette_list) {
				xml.startTag(null, "Palette");
				xml.attribute(null, "name", palette.getName());

				// Iterate through colors of palette adding to xml
				for(CustomColor color : palette) {
					xml.startTag(null, "Color");
					if(color.getName() != null) xml.attribute(null, "name", color.getName());
					xml.text(color.getHex());
					xml.endTag(null, "Color");
				}

				xml.endTag(null, "Palette");
			}

			xml.endTag(null, "Palettes");
			xml.endDocument();
		} catch(FileNotFoundException e) {
			Log.e("Data.save()", "Failed to open file", e);
			Toast.makeText(activity, "Data.save() failed to open file", Toast.LENGTH_SHORT).show();
		} catch(IOException e) {
			Log.e("Data.save()", "IO error with XmlSerializer", e);
			Toast.makeText(activity, "Data.save() XmlSerializer error", Toast.LENGTH_SHORT).show();
		} finally {
			if(file != null) {
				try {
					file.close();
				} catch(IOException e) {
					Log.e("Data.save()", "Failed to close file output stream", e);
					Toast.makeText(activity, "Data.save() failed to close file",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}