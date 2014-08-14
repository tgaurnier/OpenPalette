/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Config.java                                                               *
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

import java.io.File;


public class Config {
	private static Config instance = null;
	private String export_dir;


	private Config() {
		export_dir	=	Environment.getExternalStorageDirectory().getAbsolutePath()
						+ "/OpenPalette/Exported Palettes/";

		// Make sure export directory exists
		(new File(export_dir)).mkdirs();
	}


	public static synchronized void init() {
		if(instance == null) {
			instance = new Config();
		}
	}


	public static synchronized void destroy() {
		instance = null;
	}


	public static synchronized Config getInstance() {
		return instance;
	}


	public String getExportDir() {
		return export_dir;
	}


	public void setExportDir(String path) {
		export_dir = path;
	}
}