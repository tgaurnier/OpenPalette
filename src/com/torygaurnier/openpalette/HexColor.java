/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * HexColor.java                                                               *
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


import android.graphics.Color;


public class HexColor {
	private String name	=	null;
	private String hex	=	null;

	public HexColor() {
		hex		=	"#000000";
	}

	public HexColor(String _hex) {
		name	=	"";
		hex		=	_hex;
	}

	public HexColor(String _hex, String _name) {
		name	=	_name;
		hex		=	_hex;
	}

	public void setHex(String _hex) {
		hex = _hex;
	}

	public void setName(String _name) {
		name = _name;
	}

	public String getName() {
		return (name == null || name.isEmpty()) ? null : name;
	}

	public String getHex() {
		return hex;
	}

	public int getInt() {
		return Color.parseColor(hex);
	}

	public int getRed() {
		return Color.red(this.getInt());
	}

	public int getGreen() {
		return Color.green(this.getInt());
	}

	public int getBlue() {
		return Color.blue(this.getInt());
	}

	public float getHue() {
		float[] hsv = new float[3];
		Color.RGBToHSV(getRed(), getGreen(), getBlue(), hsv);
		return hsv[0];
	}

	public float getSaturation() {
		float[] hsv = new float[3];
		Color.RGBToHSV(getRed(), getGreen(), getBlue(), hsv);
		return hsv[1];
	}

	public float getValue() {
		float[] hsv = new float[3];
		Color.RGBToHSV(getRed(), getGreen(), getBlue(), hsv);
		return hsv[2];
	}

	public String toString() {
		return (name == null || name.isEmpty()) ? hex : name + "\n" + hex;
	}
}