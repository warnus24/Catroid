/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.livewallpaper;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import org.catrobat.catroid.ProjectHandler;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.livewallpaper.ui.SelectProgramActivity;
import org.catrobat.catroid.livewallpaper.ui.SelectProgramFragment;

/**
 * Created by Tom on 30.09.2014.
 */
public class LoadWallpaperTask extends AsyncTask<String, String, String> {
		private ProgressDialog progress;
		private String selectedProject;
		FragmentActivity activity;
		SelectProgramFragment fragment;
		public LoadWallpaperTask(FragmentActivity activity,String name,SelectProgramFragment fragment) {
			if(activity != null){
			progress = new ProgressDialog(activity);
			progress.setTitle(activity.getString(R.string.please_wait));
			progress.setMessage(activity.getString(R.string.loading));
			progress.setCancelable(false);}
			selectedProject = name;
			this.activity = activity;
			this.fragment = fragment;
		}

		@Override
		protected void onPreExecute() {
			//LiveWallpaper.getInstance().presetSprites();
			if(activity != null)
				progress.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			//Project project = StorageHandler.getInstance().loadProject(selectedProject);
			//if (project != null) {
			//	if (projectManager.getCurrentProject() != null
			//			&& projectManager.getCurrentProject().getName().equals(selectedProject)) {
			//		getFragmentManager().beginTransaction().remove(selectProgramFragment).commit();
			//		getFragmentManager().popBackStack();
			//		return null;
			//	}
			//	projectManager.setProject(project);
			//	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			//	Editor editor = sharedPreferences.edit();
			//	editor.putString(Constants.PREF_PROJECTNAME_KEY, selectedProject);
			//	editor.commit();
			//}
			String str_loadable = ProjectLoadableEnum.IS_ALREADY_LOADED.toString();

			synchronized (LiveWallpaper.getInstance()) {

				/*if (ProjectManager.getInstance().getCurrentProject() != null) {
					//getFragmentManager().beginTransaction().remove(selectProgramFragment).commit();
					//getFragmentManager().popBackStack();
					return str_loadable;
				}*/

				boolean preview_loadable = true;
				try {
					ProjectHandler.getInstance().setLiveWallpaperProject(ProjectManager.getInstance().loadProject(selectedProject, LiveWallpaper.getInstance()
							.getContext()));
				} catch (LoadingProjectException e) {
					preview_loadable = false;
					e.printStackTrace();
				} catch (OutdatedVersionProjectException e) {
					preview_loadable = false;
					e.printStackTrace();
				} catch (CompatibilityProjectException e) {
					preview_loadable = false;
					e.printStackTrace();
				}

				if (!preview_loadable) {
					fragment.getFragmentManager().beginTransaction().remove(fragment).commit();
					fragment.getFragmentManager().popBackStack();
					str_loadable = ProjectLoadableEnum.IS_NOT_LOADABLE.toString();
					return str_loadable;
				}
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(Constants.PREF_LWP_PROJECTNAME_KEY, selectedProject);
				editor.commit();
				str_loadable = ProjectLoadableEnum.IS_LOADABLE.toString();
			}

			return str_loadable;

		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals(ProjectLoadableEnum.IS_NOT_LOADABLE.toString())
					|| result.equals(ProjectLoadableEnum.IS_ALREADY_LOADED.toString())) {
				if (progress.isShowing()) {
					progress.dismiss();
				}
				Toast toast = Toast.makeText(LiveWallpaper.getInstance().getContext(), result, Toast.LENGTH_LONG);
				toast.show();

				return;
			}

			Toast toast = Toast.makeText(LiveWallpaper.getInstance().getContext(), result, Toast.LENGTH_LONG);
			toast.show();
			if (progress.isShowing()) {
				ProjectHandler.getInstance().changeToLiveWallpaper(activity);
				LiveWallpaper.getInstance().changeWallpaperProgram();
				progress.dismiss();
			}
			super.onPostExecute(result);
			((SelectProgramActivity)activity).goBack();
		}
	}
