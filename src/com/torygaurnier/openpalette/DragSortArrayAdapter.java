/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DragSortArrayAdapter.java                                                   *
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


import android.widget.ArrayAdapter;
import android.widget.Toast;

import android.content.DialogInterface;

import java.util.List;

import com.mobeta.android.dslv.DragSortListView;


public abstract class DragSortArrayAdapter<T> extends ArrayAdapter<T>
		implements DragSortListView.DropListener, DragSortListView.RemoveListener {
	private MainActivity activity;
	private List<T> list;
	public DragSortArrayAdapter(MainActivity _activity, int resource, List<T> _list) {
		super(_activity, resource, _list);
		activity = _activity;
		list = _list;
	}


	@Override
	public void drop(int from, int to) {
		T object = list.get(from);
		list.remove(from);
		list.add(to, object);
		notifyDataSetChanged();
		Data.getInstance().save();
	}

	@Override
	public void remove(final int position) {
		// Confirm removal
		new ConfirmationDialog(activity, R.string.confirm_delete_color_message,
				R.string.confirm_delete_color_title)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					remove(getItem(position));
					Data.getInstance().save();
				}

			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.refresh();
				}
			})
			.show();
	}
}