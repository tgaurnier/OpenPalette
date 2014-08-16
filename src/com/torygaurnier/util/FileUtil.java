/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * FileUtil.java                                                               *
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


package com.torygaurnier.util;


import java.io.*;
import java.util.Arrays;
import java.util.List;


public class FileUtil {
	public static synchronized List<String> getFileNameList(final String path, final String ext) {
		return Arrays.asList(
			(ext != null) ?
				new File(path).list(
					new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return (name.toLowerCase()).endsWith(ext);
						}
					}
				) :
			new File(path).list()
		);
	}
}