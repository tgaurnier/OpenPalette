/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * MainActivity.java                                                           *
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


import android.os.Bundle;

import android.app.FragmentManager;
import android.app.Activity;
import android.app.Fragment;

import android.content.DialogInterface;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Menu;

import android.widget.Toast;

import java.util.ArrayList;


//TODO: ON CLOSE SAVE WHICH PALETTE WAS LAST SELECTED, AND SELECT IT ON NEXT START
public class MainActivity extends Activity {
	// All fragments should have a type added here, for keeping track of active fragment
	private enum FragmentType {
		MAIN,
		SETTINGS
	}

	// Private Variables
	private MainFragment main_fragment;
	private SettingsFragment settings_fragment;
	private Data data;
	private PaletteList palette_list;
	private FragmentType cur_fragment;


	@Override
	public void onCreate(Bundle saved_state) {
		super.onCreate(saved_state);
		setContentView(R.layout.main);

		// If we're not being restored from a previous state then load main fragment
		if(saved_state == null) {
			// Initialize palette list
			palette_list = PaletteList.init(this);

			// If data exists, load it
			data = Data.init(this);
			if(data.exists()) data.load();

			// Make sure on first load, first palette is selected instead of last
			palette_list.setSelectedPosition(0);

			ColorChooserDialog.init();
			EditPaletteDialog.init();

			// Initialize fragments
			main_fragment = new MainFragment();
			settings_fragment = new SettingsFragment();

			// Add the fragment to the fragment container
			getFragmentManager().beginTransaction()
				.replace(R.id.fragmentContainer, main_fragment)
				.commit();

			// When moving back on stack, set current fragment to main
			getFragmentManager().addOnBackStackChangedListener(
				new FragmentManager.OnBackStackChangedListener() {
					public void onBackStackChanged() {
						if(getFragmentManager().getBackStackEntryCount() == 0) {
							cur_fragment = FragmentType.MAIN;
							refresh();
						}
					}
				}
			);
		}
	}


	@Override
	public void onAttachFragment(Fragment fragment) {
		if(fragment == main_fragment) {
			cur_fragment = FragmentType.MAIN;
		}

		if(fragment == settings_fragment) {
			cur_fragment = FragmentType.SETTINGS;
		}
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
		// If in main fragment
		if(cur_fragment == FragmentType.MAIN) {
			// Make settings action visible
			menu.findItem(R.id.settingsAction).setVisible(true);

			// Set visibility of items in palette view
			menu.findItem(R.id.addColorAction).setVisible(!main_fragment.isDrawerOpen());

			// Set visibility of items in palette list view
			menu.findItem(R.id.deletePaletteAction).setVisible(main_fragment.isDrawerOpen());
			menu.findItem(R.id.editPaletteAction).setVisible(main_fragment.isDrawerOpen());

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
		} else { // Else hide all actions from main fragment
			menu.findItem(R.id.deletePaletteAction).setVisible(false);
			menu.findItem(R.id.editPaletteAction).setVisible(false);
			menu.findItem(R.id.addColorAction).setVisible(false);
			menu.findItem(R.id.settingsAction).setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
    }


	/**
	 * Handle presses on the action bar items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// If drawer icon pressed, simply return true
		if(main_fragment.drawerIconPressed(item)) {
			return true;
		}

		// Else figure out what was pressed by ID
		else switch (item.getItemId()) {
			// Delete palette
			case R.id.deletePaletteAction:
				// Confirm if palette should be deleted
				new ConfirmationDialog(this, R.string.confirm_delete_palette_message,
						R.string.confirm_delete_palette_title)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							palette_list.removeSelectedPalette();
						}

					})
					.setNegativeButton(R.string.no, null)
					.show();

				return true;

			// Show palette edit dialog
			case R.id.editPaletteAction:
				EditPaletteDialog.getInstance().show(palette_list.getSelectedPalette(), getFragmentManager());
				return true;

			// Show color chooser dialog
			case R.id.addColorAction:
				ColorChooserDialog.getInstance().show(palette_list.getSelectedPalette(), getFragmentManager());
				return true;

			// Show settings fragment
			case R.id.settingsAction:
				setFragment(settings_fragment);
				return true;

			case android.R.id.home:
				onBackPressed();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@Override
	protected void onPostCreate(Bundle saved_state) {
		super.onPostCreate(saved_state);
		// Sync the toggle state after onRestoreInstanceState has occurred
		main_fragment.syncDrawerToggleState();
	}


	public MainFragment getMainFragment() {
		return main_fragment;
	}


	public void setFragment(Fragment fragment) {
		getFragmentManager().beginTransaction()
			.replace(R.id.fragmentContainer, fragment)
			.addToBackStack(null)
			.commit();
	}


	public void refresh() {
		if(cur_fragment == FragmentType.SETTINGS) {
			getActionBar().setTitle(settings_fragment.getTitle());
			main_fragment.hideDrawerToggle();
		}

		if(cur_fragment == FragmentType.MAIN) {
			getActionBar().setTitle(main_fragment.getTitle());
			main_fragment.showDrawerToggle();

			main_fragment.refresh();
		}

		invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	}
}
