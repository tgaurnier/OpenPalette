/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Msg.java                                                               *
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


import android.util.Log;
import android.app.Activity;
import android.widget.Toast;

import java.lang.Throwable;


public class Msg {
	public final static int DEBUG = 0, ERROR = 1, INFO = 2, VERBOSE = 3, WARNING = 4, WTF = 5;
	private static Msg instance = null;
	private Activity activity;


	public static synchronized void init(Activity activity) {
		if(instance == null) {
			instance = new Msg(activity);
		}
	}


	public static synchronized void log(int type, String tag, String msg) {
		switch(type) {
			case DEBUG:
				instance.debug(tag, msg);
				break;

			case ERROR:
				instance.error(tag, msg);
				break;

			case INFO:
				instance.info(tag, msg);
				break;

			case VERBOSE:
				instance.verbose(tag, msg);
				break;

			case WARNING:
				instance.warning(tag, msg);
				break;

			case WTF:
				instance.wtf(tag, msg);
				break;
		}
	}


	public static synchronized void log(int type, String tag, String msg, Throwable throwable) {
		switch(type) {
			case DEBUG:
				instance.debug(tag, msg, throwable);
				break;

			case ERROR:
				instance.error(tag, msg, throwable);
				break;

			case INFO:
				instance.info(tag, msg, throwable);
				break;

			case VERBOSE:
				instance.verbose(tag, msg, throwable);
				break;

			case WARNING:
				instance.warning(tag, msg, throwable);
				break;

			case WTF:
				instance.wtf(tag, msg, throwable);
				break;
		}
	}


	public static synchronized void destroy() {
		instance = null;
	}


	private Msg(Activity _activity) {
		activity = _activity;
	}


	private void debug(String tag, String msg) {
		Log.d(tag, msg);
		Toast.makeText(activity, tag + " debug: " + msg, Toast.LENGTH_SHORT).show();
	}


	private void debug(String tag, String msg, Throwable throwable) {
		Log.d(tag, msg, throwable);
		Toast.makeText(activity, tag + " debug: " + msg, Toast.LENGTH_SHORT).show();
	}


	private void error(String tag, String msg) {
		Log.e(tag, msg);
		Toast.makeText(activity, tag + " error: " + msg, Toast.LENGTH_LONG).show();
	}


	private void error(String tag, String msg, Throwable throwable) {
		Log.e(tag, msg, throwable);
		Toast.makeText(activity, tag + " error: " + msg, Toast.LENGTH_LONG).show();
	}


	private void info(String tag, String msg) {
		Log.i(tag, msg);
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}


	private void info(String tag, String msg, Throwable throwable) {
		Log.i(tag, msg, throwable);
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
	}


	private void verbose(String tag, String msg) {
		Log.v(tag, msg);
		Toast.makeText(activity, tag + " verbose: " + msg, Toast.LENGTH_SHORT).show();
	}


	private void verbose(String tag, String msg, Throwable throwable) {
		Log.v(tag, msg, throwable);
		Toast.makeText(activity, tag + " verbose: " + msg, Toast.LENGTH_SHORT).show();
	}


	private void warning(String tag, String msg) {
		Log.w(tag, msg);
		Toast.makeText(activity, tag + " warning: " + msg, Toast.LENGTH_SHORT).show();
	}


	private void warning(String tag, String msg, Throwable throwable) {
		Log.w(tag, msg, throwable);
		Toast.makeText(activity, tag + " warning: " + msg, Toast.LENGTH_SHORT).show();
	}


	private void wtf(String tag, String msg) {
		Log.wtf(tag, msg);
		Toast.makeText(activity, tag + " wtf: " + msg, Toast.LENGTH_LONG).show();
	}


	private void wtf(String tag, String msg, Throwable throwable) {
		Log.wtf(tag, msg, throwable);
		Toast.makeText(activity, tag + " wtf: " + msg, Toast.LENGTH_LONG).show();
	}
}