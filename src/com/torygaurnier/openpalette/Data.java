/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Data.java                                                                   *
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


import android.os.Environment;
import android.content.Context;
import android.content.DialogInterface;
import android.util.*;

import org.xmlpull.v1.*;

import java.io.*;
import java.util.*;

import com.torygaurnier.util.Msg;


/**
 * Data
 *
 * This object handles reading/writing user data to file.
 */
public class Data {
	private final MainActivity activity;
	private PaletteList palette_list;
	private static Data instance = null;
	private ConfirmationDialog confirmation_dialog = null;


	private Data(MainActivity _activity) {
		activity		=	_activity;
		palette_list	=	PaletteList.getInstance();
	}


	public static synchronized void init(MainActivity _activity) {
		if(instance == null) {
			instance = new Data(_activity);
		}
	}


	public static synchronized void destroy() {
		instance = null;
	}


	public static synchronized Data getInstance() {
		return instance;
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
					Msg.log(Msg.ERROR, "Data.exists()", "Failed to close file input stream", e);
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
			readPalettes(xml);
		} catch(FileNotFoundException e) {
			Msg.log(Msg.ERROR, "Data.load()", "Failed to open file", e);
		} catch(XmlPullParserException e) {
			Msg.log(Msg.ERROR, "Data.load()", "XmlPullParser error", e);
		} catch(IOException e) {
			Msg.log(Msg.ERROR, "Data.load()", "IO error with XmlPullParser", e);
		} finally {
			if(file != null) {
				try {
					file.close();
				} catch(IOException e) {
					Msg.log(Msg.ERROR, "Data.load()", "Failed to close file input stream", e);
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
				writePalette(xml, palette);
			}

			xml.endTag(null, "Palettes");
			xml.endDocument();
		} catch(FileNotFoundException e) {
			Msg.log(Msg.ERROR, "Data.save()", "Failed to find or create file", e);
		} catch(IOException e) {
			Msg.log(Msg.ERROR, "Data.save()", "IO error with XmlSerializer", e);
		} finally {
			if(file != null) {
				try {
					file.close();
				} catch(IOException e) {
					Msg.log(Msg.INFO, "Data.save()", "Failed to close file input stream", e);
				}
			}
		}
	}


	/**
	 * Exports user data to OpenPalette/Exported, uses external sd card if it exists, else uses
	 * internal storage.
	 */
	public void exportPalettes(ArrayList<Palette> export_list) {
		// If media storage is mounted
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String path				=	null;
			FileOutputStream file	=	null;
			XmlSerializer xml		=	null;

			try {
				for(Palette palette : export_list) {
					path	=	Config.getInstance().getExportDir() + palette.getName() + ".xml";
					file	=	new FileOutputStream(path, false);
					xml		=	Xml.newSerializer();
					xml.setOutput(file, "UTF-8");
					xml.startDocument("UTF-8", true);
					writePalette(xml, palette);
					xml.endDocument();
				}

				Msg.log(Msg.INFO, "Data.export()", "Data exported to sd card");
			} catch(FileNotFoundException e) {
				Msg.log(Msg.ERROR, "Data.export()", "Failed to find or create file " + path, e);
			} catch(IOException e) {
				Msg.log(Msg.ERROR, "Data.export()", "IO error with XmlSerializer", e);
			} finally {
				if(file != null) {
					try {
						file.close();
					} catch(IOException e) {
						Msg.log(Msg.ERROR, "Data.export()", "Failed to close file", e);
					}
				}
			}
		}

		else {
			Msg.log(Msg.ERROR, "Data.export()", "External storage not available");
		}
	}


	/**
	 * This method recursively calls itself until iterator.hasNext() returns false.
	 */
	public void importPalettes(final Iterator<String> iterator, final int length) {
		if(confirmation_dialog == null) {
			confirmation_dialog = new ConfirmationDialog(activity,
					R.string.confirm_replace_palette_title,
					(length > 1) ? true : false);
		}

		if(iterator.hasNext()) {
			final String name = iterator.next();
			// If palette already exists, confirm overwrite
			if(palette_list.getPalette(name) != null) {
				confirmation_dialog.confirm(
					name + activity.getText(R.string.confirm_replace_palette_message),
					new ConfirmationDialog.OnChoiceListener() {
						public void onAccept() {
							palette_list.remove(name);
							importPalette(name);
						}
						public void afterChoice() {
							importPalettes(iterator, length);
						}
					}
				);
			} else {
				importPalette(name);
				importPalettes(iterator, length);
			}

		} else {
			confirmation_dialog = null;
		}
	}


	private void importPalette(String name) {
		if(palette_list.getPalette(name) == null) {
			FileInputStream file	=	null;
			XmlPullParser xml		=	null;
			String path				=	Config.getInstance().getExportDir() + name + ".xml";

			try {
				file	=	new FileInputStream(path);
				xml		=	Xml.newPullParser();
				xml.setInput(file, "UTF-8");
				readPalettes(xml);
			} catch(FileNotFoundException e) {
				Msg.log(Msg.ERROR, "Data.importPalette()", "Failed to open file", e);
			} catch(XmlPullParserException e) {
				Msg.log(Msg.ERROR, "Data.importPalette()", "XmlPullParser error", e);
			} catch(IOException e) {
				Msg.log(Msg.ERROR, "Data.importPalette()", "IO error with XmlPullParser", e);
			} finally {
				if(file != null) {
					try {
						file.close();
					} catch(IOException e) {
						Msg.log(Msg.ERROR, "Data.importPalette()",
								"Failed to close file input stream", e);
					}
				}
			}

			// Save imported palette and log success
			save();
			Msg.log(Msg.INFO, "Data.importPalette()", "Successfully imported " + name);
		}
	}


	/**
	 * This is a convenience function which scans an XmlPullParser and adds ALL found palettes to
	 * the palette list.
	 */
	private void readPalettes(XmlPullParser xml) throws XmlPullParserException, IOException {
		// Iterate through XML adding values to PaletteList
		Palette palette = null;
		HexColor color = null;
		for(int type = xml.getEventType(); type != XmlPullParser.END_DOCUMENT; type = xml.next()) {
			switch(type) {
				case XmlPullParser.START_TAG:
					if((xml.getName()).equals("Palette")) {
						palette = new Palette(activity, xml.getAttributeValue(0));
					}

					else if((xml.getName()).equals("Color")) {
						color = new HexColor();
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
	}


	/**
	 * Convenience method to write a Palette to an XmlSerializer.
	 */
	private void writePalette(XmlSerializer xml, Palette palette) throws IOException {
		xml.startTag(null, "Palette");
		xml.attribute(null, "name", palette.getName());

		// Iterate through colors of palette adding to xml
		for(HexColor color : palette) {
			xml.startTag(null, "Color");
			if(color.getName() != null) xml.attribute(null, "name", color.getName());
			xml.text(color.getHex());
			xml.endTag(null, "Color");
		}

		xml.endTag(null, "Palette");
	}
}