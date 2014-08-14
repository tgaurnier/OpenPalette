/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * SimpleDialog.java                                                           *
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


package com.torygaurnier.dialog;


import android.os.Bundle;
import android.app.*;
import android.view.*;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;


/**
 * This abstract class is a simple implementaton of a DialogFragment using an AlertDialog. The only
 * methods that need to be implemented in the subclass are createView(), onOkClicked(),
 * onCancelClicked(), setTitle(), setOkText(), and setCancelText(). The subclass constroctor MUST
 * call one of the setTitle() methods, one of the setOkText() methods, and one of the
 * setCancelText() methods.
 */
public abstract class SimpleDialog extends DialogFragment {
	protected Activity activity;
	protected AlertDialog dialog;
	private int title_res;
	private int ok_res;
	private int cancel_res;
	private boolean ok_enabled	=	true;
	private String title_str	=	null;
	private String ok_str		=	null;
	private String cancel_str	=	null;


	public SimpleDialog(Activity _activity) {
		activity			=	_activity;
	}


	public SimpleDialog(Activity _activity, boolean _ok_enabled) {
		activity			=	_activity;
		ok_enabled			=	_ok_enabled;
	}


	public void show() {
		super.show(activity.getFragmentManager(), null);
	}


	/**
	 * This should create and return the view that will be used for the dialog.
	 */
	protected abstract View createView();


	/**
	 * This will run when the "OK" button is pressed.
	 */
	public abstract void onOkClicked(DialogInterface dialog_interface, int id);


	/**
	 * This will run when the "Cancel" button is pressed, overriding this is optional and should
	 * only be done in special cases. The default action is simply to cancel the dialog.
	 */
	public void onCancelClicked(DialogInterface dialog_interface, int id) {
		dialog_interface.cancel();
	}


	/**
	 * Set the Title of the dialog to an xml string resource.
	 * One of the setTitle methods MUST be called from the subclass constructor.
	 */
	public void setTitle(int resource_id) {
		title_res = resource_id;
	}


	/**
	 * Set the Title of the dialog to a Java String.
	 */
	public void setTitle(String string) {
		title_str = string;
	}


	/**
	 * Set the text of the OK Button to an xml string resource.
	 * One of the setOkText methods MUST be called from the subclass constructor.
	 */
	public void setOkText(int resource_id) {
		ok_res = resource_id;
	}


	/**
	 * Set the OK Button text to a Java String.
	 */
	public void setOkText(String string) {
		ok_str = string;
	}


	/**
	 * Set the Cancel Button text to an xml string resource.
	 * One of the setCancelText methods MUST be called from the subclass constructor.
	 */
	public void setCancelText(int resource_id) {
		cancel_res = resource_id;
	}


	/**
	 * Set the Cancel Button text to a Java String.
	 */
	public void setCancelText(String string) {
		cancel_str = string;
	}


	protected void setOkEnabled(boolean value) {
		ok_enabled = value;
		if(dialog != null) dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(ok_enabled);
	}


	@Override
	public void onCreate(Bundle saved_state) {
		super.onCreate(saved_state);
		activity = getActivity();
	}


	@Override
	public Dialog onCreateDialog(Bundle saved_state) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		// Set title
		if(title_str == null) {
			builder.setTitle(title_res);
		} else builder.setTitle(title_str);

		// Set view for dialog
		View view = createView();
		builder.setView(view);

		// Setup button click listeners
		DialogInterface.OnClickListener ok_listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog_interface, int id) {
				onOkClicked(dialog_interface, id);
			}
		};

		DialogInterface.OnClickListener cancel_listener = new DialogInterface.OnClickListener() {
			// User cancelled the dialog, don't create palette
			public void onClick(DialogInterface dialog_interface, int id) {
				onCancelClicked(dialog_interface, id);
			}
		};

		if(ok_str == null) builder.setPositiveButton(ok_res, ok_listener);
		else builder.setPositiveButton(ok_str, ok_listener);

		if(cancel_str == null) builder.setNegativeButton(cancel_res, cancel_listener);
		else builder.setNegativeButton(cancel_str, cancel_listener);

		// Create, show, and return the dialog
		dialog = builder.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(ok_enabled);

		return dialog;
	}
}