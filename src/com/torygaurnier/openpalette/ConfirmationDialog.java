/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ConfirmationDialog.java                                                     *
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


import android.app.*;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.*;
import android.view.*;

import com.torygaurnier.dialog.SimpleDialog;


public class ConfirmationDialog extends SimpleDialog {
	/**
	 * Listener class used to connect to button presses for "OK" and "No".
	 *
	 * public void onAccept(): Required, Called when user presses "OK".
	 *
	 * public void onDecline(): Optional, called when user presses "No", this only needs to be
	 * implemented when some kind of special actions needs to occur when "No" is pressed, the
	 * default action is just to cancel the dialog.
	 *
	 * public void afterChoice(): Optional, called after a choice has been made, and the dialog has
	 * been detached from the activity, if any cleanup is needed it should go here.
	 */
	public static abstract class OnChoiceListener {
		public abstract void onAccept();
		public void onDecline() {}
		public void afterChoice() {}
	}


	/**
	 * Private member viarables
	 */
	private OnChoiceListener on_choice_listener	=	null;
	private TextView message_view				=	null;
	private CheckBox check_box					=	null;
	private String remember_choice				=	"NONE";
	private boolean recurrent					=	false;
	private int message_res						=	-1;
	private String message_str					=	null;


	/**
	 * Initialize ConfirmationDialog.
	 *
	 * Activity activity: The root activity/context of the dialog.
	 *
	 * int title: XML string resource to display as dialog title.
	 *
	 * boolean recurrent: If this instance of the dialog will be recurrent, if this is true then the
	 * "Don't ask again" check box will be displayed.
	 */
	public ConfirmationDialog(Activity activity, int title, boolean recurrent) {
		super(activity);
		setTitle(title);
		setOkText(R.string.ok);
		setCancelText(R.string.no);
		this.recurrent = recurrent;
	}


	/**
	 * Initialize ConfirmationDialog with recurrent defaulting to false.
	 */
	public ConfirmationDialog(Activity activity, int title) {
		super(activity);
		setTitle(title);
		setOkText(R.string.ok);
		setCancelText(R.string.no);
	}


	/**
	 * Show ConfirmationDialog with XML string resource message, if ConfirmationDialog was created
	 * with recurrent == true, and the "Don't ask again" check box was checked, then the dialog will
	 * not show, and instead the previous answer will just be chosen again.
	 *
	 * int message: XML string resource to be displayed in confirmation dialog as the message.
	 *
	 * OnChoiceListener: Listener to use when a choice has been made (OK, or No has been pressed).
	 */
	public void confirm(int message, OnChoiceListener on_choice_listener) {
		this.on_choice_listener = on_choice_listener;
		setMessage(message);

		if(remember_choice.equals("ACCEPT")) {
			on_choice_listener.onAccept();
			on_choice_listener.afterChoice();
		} else if(remember_choice.equals("DECLINE")) {
			on_choice_listener.onDecline();
			on_choice_listener.afterChoice();
		} else {
			super.show();
		}
	}


	/**
	 * Same as previous confirm() method, except message is a Java String instead of an XML string
	 * resource.
	 */
	public void confirm(String message, OnChoiceListener on_choice_listener) {
		this.on_choice_listener = on_choice_listener;
		setMessage(message);

		if(remember_choice.equals("ACCEPT")) {
			on_choice_listener.onAccept();
			on_choice_listener.afterChoice();
		} else if(remember_choice.equals("DECLINE")) {
			on_choice_listener.onDecline();
			on_choice_listener.afterChoice();
		} else {
			super.show();
		}
	}


	protected View createView() {
		View root_view	=	activity.getLayoutInflater().inflate(R.layout.confirmation_view, null);
		message_view	=	(TextView)root_view.findViewById(R.id.confirmationViewMessage);

		if(message_str != null) {
			message_view.setText(message_str);
		} else {
			message_view.setText(message_res);
		}

		if(recurrent) {
			check_box = (CheckBox)root_view.findViewById(R.id.confirmationViewCheckBox);
			check_box.setVisibility(View.VISIBLE);
		}

		return root_view;
	}


	private void setMessage(int resource) {
		message_res = resource;
	}


	private void setMessage(String string) {
		message_str = string;
	}


	public void onOkClicked(DialogInterface dialog_interface, int id) {
		if(doNotAskAgain()) remember_choice = "ACCEPT";
		on_choice_listener.onAccept();
	}


	public void onCancelClicked(DialogInterface dialog_interface, int id) {
		if(doNotAskAgain()) remember_choice = "DECLINE";
		on_choice_listener.onDecline();
		dialog_interface.cancel();
	}


	private boolean doNotAskAgain() {
		if(check_box == null) {
			return false;
		} else {
			return check_box.isChecked();
		}
	}


	@Override
	public void onDetach() {
		super.onDetach();
		on_choice_listener.afterChoice();
	}
}