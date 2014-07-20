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


import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import android.os.Bundle;

import android.app.DialogFragment;
import android.app.Activity;

import android.content.res.Configuration;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.ClipData;

import android.view.ContextMenu;
import android.view.MenuInflater;
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


//TODO: ON CLOSE SAVE WHICH PALETTE WAS LAST SELECTED, AND SELECT IT ON NEXT START
//TODO: MAKE IT SO COLORS CAN BE REORDERED
public class MainActivity extends Activity {
	private MainActivity activity;
	private ClipboardManager clipboard;
	private ClipData clip;
	private DrawerLayout drawer_layout;
	private ActionBarDrawerToggle drawer_toggle;
	private ListView palette_list_view;
	private DragSortListView palette_view;

	private ColorChooserDialog color_chooser_dialog;
	private EditPaletteDialog edit_palette_dialog;

	private PaletteList palette_list;

	private Data data;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		activity	=	this;
		clipboard	=	(ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		/**
		 * Setup Drawer for list of Palettes
		 */

		drawer_layout = (DrawerLayout)findViewById(R.id.rootDrawerLayout);
		drawer_toggle = new ActionBarDrawerToggle(
			this,
			drawer_layout,
			R.drawable.ic_navigation_drawer,
			R.string.drawer_open,
			R.string.drawer_close
		) {
			// Called when drawer closes
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				refresh();
			}

			// Called when drawer opens
			public void onDrawerOpened(View drawer_view) {
				super.onDrawerOpened(drawer_view);
				refresh();
			}
		};

		drawer_layout.setDrawerListener(drawer_toggle);

		// Set onclick listeners for 'new palette' buttons
		findViewById(R.id.createFirstPaletteButton).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) { edit_palette_dialog.show(); }
		});
		findViewById(R.id.newPaletteButton).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) { edit_palette_dialog.show(); }
		});

		// Get palette list view by it's ID, setup PaletteList
		palette_list_view	=	(ListView)findViewById(R.id.paletteListView);
		palette_list		=	PaletteList.init(this);
		palette_list_view.setAdapter(palette_list.getAdapter());

		// Set on click listener for palette list view
		palette_list_view.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView adapter_view, View view, int position, long id) {
				// User clicked a list item, select it
				palette_list.setSelectedPosition(position);
			}
		});


		// If data exists, load it
		data = Data.init(this, palette_list);
		if(data.exists()) data.load();


		/**
		 * Setup list view for current Palette
		 */


		// Get palette view by its ID
		palette_view = (DragSortListView)findViewById(R.id.paletteView);
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

		// Create dialogs
		color_chooser_dialog	=	new ColorChooserDialog(this, data);
		edit_palette_dialog		=	new EditPaletteDialog(this, palette_list, data);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		refresh();
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred
		drawer_toggle.syncState();
	}


	/**
	 * Context menu for color actions.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info) {
		super.onCreateContextMenu(menu, view, info);
		MenuInflater inflater = getMenuInflater();
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
				Toast.makeText(this, hex + " copied to clipboard", Toast.LENGTH_SHORT).show();
				return true;

			case R.id.copyRgbAction:
				String rgb = "" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
				clip = ClipData.newPlainText("RGB", rgb);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(this, rgb + " copied to clipboard", Toast.LENGTH_SHORT).show();
				return true;

			case R.id.copyHsvAction:
				String hsv = "" + color.getHue() + ", " + color.getSaturation() + ", " +
						color.getValue();
				clip = ClipData.newPlainText("HSV", hsv);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(this, hsv + " copied to clipboard", Toast.LENGTH_SHORT).show();
				return true;

			case R.id.duplicateColorAction:
				if(color.getName() != null) {
					selected_palette.add(new CustomColor(color.getHex(), color.getName()));
				} else {
					selected_palette.add(new CustomColor(color.getHex()));
				}

				data.save();
				return true;

			case R.id.editColorAction:
				color_chooser_dialog.show(color);
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


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}


	/**
	 * Prepares Action Bar whenever Drawer is opened or closed.
	 * Called whenever we call invalidateOptionsMenu()
	 */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		// Set visibility of items in palette view
		menu.findItem(R.id.addColorAction).setVisible(!isDrawerOpen());

		// Set visibility of items in palette list view
		menu.findItem(R.id.deletePaletteAction).setVisible(isDrawerOpen());
		menu.findItem(R.id.editPaletteAction).setVisible(isDrawerOpen());

		// If no palette added yet
		if(palette_list.getSelectedPalette() == null) {
			// Disable actions for palette
			menu.findItem(R.id.deletePaletteAction).setEnabled(false);
			menu.findItem(R.id.editPaletteAction).setEnabled(false);
			menu.findItem(R.id.addColorAction).setEnabled(false);
		} else {
			// Else enable actions
			menu.findItem(R.id.deletePaletteAction).setEnabled(true);
			menu.findItem(R.id.editPaletteAction).setEnabled(true);
			menu.findItem(R.id.addColorAction).setEnabled(true);
		}

		return super.onPrepareOptionsMenu(menu);
    }


	/**
	 * Handle presses on the action bar items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// If drawer icon pressed, simply return true
		if(drawer_toggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Else figure out what was pressed by ID
		switch (item.getItemId()) {
			case R.id.deletePaletteAction:
				// Confirm if palette should be deleted
				new ConfirmationDialog(this, R.string.confirm_delete_palette_message,
						R.string.confirm_delete_palette_title)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							palette_list.removeSelectedPalette();
							data.save();
						}

					})
					.setNegativeButton(R.string.no, null)
					.show();

				return true;

			// Show palette edit dialog
			case R.id.editPaletteAction:
				edit_palette_dialog.show(palette_list.getSelectedPalette());
				return true;

			// Show color chooser dialog
			case R.id.addColorAction:
				color_chooser_dialog.show(palette_list.getSelectedPalette());
				return true;

			//TODO: DECIDE IF THIS APP WILL EVEN HAVE SETTINGS
			// Show settings activity
			//case R.id.settingsAction:
				////openSettings();
				//return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}


	/**
	 * Refresh main view
	 */
	public void refresh() {
		if(isDrawerOpen()) {
			getActionBar().setTitle("Palettes");
		}

		// If color_list is empty, hide ListView and show new palette button
		// Also set adapter for palette view
		if(palette_list.getSelectedPalette() == null) {
			palette_view.setVisibility(View.GONE);
			findViewById(R.id.createFirstPaletteButton).setVisibility(View.VISIBLE);
			if(!isDrawerOpen()) getActionBar().setTitle("");
		} else {
			if(!isDrawerOpen()) {
				getActionBar().setTitle((palette_list.getSelectedPalette()).getName());
				findViewById(R.id.createFirstPaletteButton).setVisibility(View.GONE);
				palette_view.setVisibility(View.VISIBLE);
				// Set adapter to main palette view to selected palette adapter
				palette_view.setAdapter((palette_list.getSelectedPalette()).getAdapter());
			}
		}

		invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	}


	public boolean isDrawerOpen() {
		return drawer_layout.isDrawerOpen(findViewById(R.id.paletteDrawer));
	}
}
