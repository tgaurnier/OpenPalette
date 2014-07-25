/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * MainFragment.java                                                           *
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


import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import android.os.Bundle;

import android.app.DialogFragment;
import android.app.Fragment;

import android.content.res.Configuration;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.ClipData;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ContextMenu;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.mobeta.android.dslv.DragSortListView;


public class MainFragment extends Fragment {
	private View view;
	private MainActivity activity;
	private ClipboardManager clipboard;
	private ClipData clip;
	private DrawerLayout drawer_layout;
	private ActionBarDrawerToggle drawer_toggle;
	private ListView palette_list_view;
	private DragSortListView palette_view;
	private PaletteList palette_list;


	public void hideDrawerToggle() {
		drawer_toggle.setDrawerIndicatorEnabled(false);
	}


	public void showDrawerToggle() {
		drawer_toggle.setDrawerIndicatorEnabled(true);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved_state) {
		view		=	inflater.inflate(R.layout.main_fragment, container, false);
		activity	=	(MainActivity)getActivity();
		clipboard	=	(ClipboardManager)activity.getSystemService(activity.CLIPBOARD_SERVICE);

		/**
		 * Setup Drawer for list of Palettes
		 */
		drawer_layout = (DrawerLayout)view.findViewById(R.id.rootDrawerLayout);
		drawer_toggle = new ActionBarDrawerToggle(
			activity,
			drawer_layout,
			R.drawable.ic_navigation_drawer,
			R.string.drawer_open,
			R.string.drawer_close
		) {
			// Called when drawer closes
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				activity.refresh();
			}

			// Called when drawer opens
			public void onDrawerOpened(View drawer_view) {
				super.onDrawerOpened(drawer_view);
				activity.refresh();
			}
		};

		drawer_layout.setDrawerListener(drawer_toggle);

		// Set onclick listeners for 'new palette' buttons
		view.findViewById(R.id.createFirstPaletteButton).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) { EditPaletteDialog.getInstance().show(activity.getFragmentManager()); }
		});
		view.findViewById(R.id.newPaletteButton).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) { EditPaletteDialog.getInstance().show(activity.getFragmentManager()); }
		});

		// Get palette list view by it's ID, setup PaletteList
		palette_list_view	=	(ListView)view.findViewById(R.id.paletteListView);
		palette_list		=	PaletteList.getInstance();
		palette_list_view.setAdapter(palette_list.getAdapter());

		// Set on click listener for palette list view
		palette_list_view.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView adapter_view, View view, int position, long id) {
				// User clicked a list item, select it
				palette_list.setSelectedPosition(position);
			}
		});


		/**
		 * Setup list view for current Palette
		 */
		// Get palette view by its ID
		palette_view = (DragSortListView)view.findViewById(R.id.paletteView);
		palette_view.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView adapter_view, View view, int position, long id) {
				String hex = ((palette_list.getSelectedPalette()).getColor(position)).getHex();
				ClipData clip = ClipData.newPlainText("hex", hex);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(activity, hex + " copied to clipboard", Toast.LENGTH_SHORT).show();
			}
		});

		// Register context menu for color actions
		registerForContextMenu(palette_view);

		activity.getActionBar().setDisplayHomeAsUpEnabled(true);
		activity.getActionBar().setHomeButtonEnabled(true);

		activity.refresh();
		return view;
	}


	/**
	 * Context menu for color actions.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info) {
		super.onCreateContextMenu(menu, view, info);
		MenuInflater inflater = activity.getMenuInflater();
		inflater.inflate(R.menu.color_actions, menu);
	}


	/**
	 * Color action is selected.
	 */
	public boolean onContextItemSelected(MenuItem item) {
		int position = (int)((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id;
		final Palette selected_palette = palette_list.getSelectedPalette();
		final CustomColor color = selected_palette.getColor(position);

		switch(item.getItemId()) {
			case R.id.copyHexAction:
				String hex = color.getHex();
				clip = ClipData.newPlainText("HEX", hex);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(activity, hex + " copied to clipboard", Toast.LENGTH_SHORT).show();
				return true;

			case R.id.copyRgbAction:
				String rgb = "" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
				clip = ClipData.newPlainText("RGB", rgb);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(activity, rgb + " copied to clipboard", Toast.LENGTH_SHORT).show();
				return true;

			case R.id.copyHsvAction:
				String hsv = "" + color.getHue() + ", " + color.getSaturation() + ", " +
						color.getValue();
				clip = ClipData.newPlainText("HSV", hsv);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(activity, hsv + " copied to clipboard", Toast.LENGTH_SHORT).show();
				return true;

			case R.id.duplicateColorAction:
				if(color.getName() != null) {
					selected_palette.add(new CustomColor(color.getHex(), color.getName()));
				} else {
					selected_palette.add(new CustomColor(color.getHex()));
				}

				Data.getInstance().save();
				return true;

			case R.id.editColorAction:
				ColorChooserDialog.getInstance().show(color, activity.getFragmentManager());
				return true;

			case R.id.deleteColorAction:
				selected_palette.remove(color);
				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}


	@Override
	public void onConfigurationChanged(Configuration new_config) {
		super.onConfigurationChanged(new_config);
		drawer_toggle.onConfigurationChanged(new_config);
	}


	/**
	 * Return current title of fragment to be displayed on ActionBar.
	 */
	public String getTitle() {
		// If drawer is open, return title from strings XML file
		if(isDrawerOpen()) {
			return getResources().getString(R.string.main_fragment_title);
		} else {
			// Else if there is a palette, return it's name
			if(palette_list.getSelectedPalette() != null) {
				return (palette_list.getSelectedPalette()).getName();
			} else return ""; // Else just return blank string
		}
	}


	/**
	 * Refresh main fragment view
	 */
	public void refresh() {
		// If palette list is empty, hide ListView and show new palette button
		if(palette_list.getSelectedPalette() == null) {
			palette_view.setVisibility(View.GONE);
			view.findViewById(R.id.createFirstPaletteButton).setVisibility(View.VISIBLE);
			if(!isDrawerOpen()) activity.getActionBar().setTitle("");
		} else {
			if(!isDrawerOpen()) {
				view.findViewById(R.id.createFirstPaletteButton).setVisibility(View.GONE);
				palette_view.setVisibility(View.VISIBLE);
				// Set adapter to main palette view to selected palette adapter
				palette_view.setAdapter((palette_list.getSelectedPalette()).getAdapter());
			}
		}
	}


	/**
	 * Returns true if palette drawer is open.
	 */
	public boolean isDrawerOpen() {
		return drawer_layout.isDrawerOpen(view.findViewById(R.id.paletteDrawer));
	}


	/**
	 * Returns true if selected action bar menu item was the drawer toggle.
	 */
	public boolean drawerIconPressed(MenuItem item) {
		return drawer_toggle.onOptionsItemSelected(item);
	}


	public void syncDrawerToggleState() {
		drawer_toggle.syncState();
	}
}