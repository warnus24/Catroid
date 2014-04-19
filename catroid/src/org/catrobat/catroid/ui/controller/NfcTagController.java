/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.NfcTagViewHolder;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.NfcTagBaseAdapter;
import org.catrobat.catroid.ui.adapter.SoundBaseAdapter;
import org.catrobat.catroid.ui.fragment.BackPackSoundFragment;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;

public final class NfcTagController {
    public static final String BUNDLE_ARGUMENTS_SELECTED_NFCTAG = "selected_nfctag";
    public static final String SHARED_PREFERENCE_NAME = "showDetailsNfcTags";
    public static final int ID_LOADER_MEDIA_IMAGE = 1;
    public static final int REQUEST_SELECT_NFC = 0;

    private static final NfcTagController INSTANCE = new NfcTagController();
    private static final String TAG = NfcTagController.class.getSimpleName();

    private NfcTagController() {
    }

    public static NfcTagController getInstance() {
        return INSTANCE;
    }

    public void updateNfcTagLogic(Context context, final int position, final NfcTagViewHolder holder,
                                 final NfcTagBaseAdapter nfcTagAdapter) {
        final NfcTagData nfcTagData = nfcTagAdapter.getNfcTagDataItems().get(position);

        if (nfcTagData == null) {
            return;
        }
        holder.scanNewTagButton.setTag(position);
        holder.titleTextView.setText(nfcTagData.getNfcTagName());

        handleCheckboxes(position, holder, nfcTagAdapter);
        handleNfcTagData(holder, nfcTagData, nfcTagAdapter, position, context);
        handleDetails(nfcTagAdapter, holder, nfcTagData);
        setClickListener(nfcTagAdapter, holder, nfcTagData);
    }

    private void setClickListener(final NfcTagBaseAdapter nfcTagAdapter, final NfcTagViewHolder holder,
                                  final NfcTagData nfcTagData) {
        OnClickListener listItemOnClickListener = (new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (nfcTagAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
                    holder.checkbox.setChecked(!holder.checkbox.isChecked());
                }
            }
        });

        if (nfcTagAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
            holder.scanNewTagButton.setOnClickListener(listItemOnClickListener);
        } else {
            if (nfcTagData.isScanning) {
                holder.scanNewTagButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (nfcTagAdapter.getOnNfcTagEditListener() != null) {
                            //nfcTagAdapter.getOnSoundEditListener().onSoundPause(view);
                        }
                    }
                });
            } else {
                holder.scanNewTagButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (nfcTagAdapter.getOnNfcTagEditListener() != null) {
                            //nfcTagAdapter.getOnNfcTagEditListener().onSoundPlay(view);
                        }
                    }
                });
            }
        }
        holder.nfcTagFragmentButtonLayout.setOnClickListener(listItemOnClickListener);
    }

    private void handleDetails(NfcTagBaseAdapter nfcTagAdapter, NfcTagViewHolder holder, NfcTagData nfcTagData) {
        if (nfcTagAdapter.getShowDetails()) {
            holder.nfcTagUidTextView.setText(nfcTagData.getNfcTagUid());
            holder.nfcTagUidTextView.setVisibility(TextView.VISIBLE);
            holder.nfcTagUidPrefixTextView.setVisibility(TextView.VISIBLE);
        } else {
            holder.nfcTagUidTextView.setVisibility(TextView.GONE);
            holder.nfcTagUidPrefixTextView.setVisibility(TextView.GONE);
        }
    }

    private void handleNfcTagData(NfcTagViewHolder holder, NfcTagData nfcTagData, NfcTagBaseAdapter nfcTagAdapter,
                                 int position, Context context) {
        try {
            if (nfcTagData.isScanning) {
                holder.scanNewTagButton.setImageResource(R.drawable.ic_media_stop);
                holder.scanNewTagButton.setContentDescription(context.getString(R.string.sound_stop));
                /*
                if (nfcTagAdapter.getCurrentPlayingPosition() == Constants.NO_POSITION) {
                    startPlayingSound(holder.timePlayedChronometer, position, soundAdapter);
                } else if ((position == nfcTagAdapter.getCurrentPlayingPosition())
                        && (SoundBaseAdapter.getElapsedMilliSeconds() > (milliseconds - 1000))) {
                    stopPlayingSound(soundInfo, holder.timePlayedChronometer, nfcTagAdapter);
                } else {
                    continuePlayingSound(holder.timePlayedChronometer, SystemClock.elapsedRealtime());
                }
                */
            } else {
                holder.scanNewTagButton.setImageResource(R.drawable.ic_media_play);
                holder.scanNewTagButton.setContentDescription(context.getString(R.string.nfctag_scan));
                //stopPlayingSound(soundInfo, holder.timePlayedChronometer, nfcTagAdapter);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Cannot get view.", ex);
        }
    }

    private void handleCheckboxes(final int position, NfcTagViewHolder holder, final NfcTagBaseAdapter nfcTagAdapter) {
        holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (nfcTagAdapter.getSelectMode() == ListView.CHOICE_MODE_SINGLE) {
                        nfcTagAdapter.clearCheckedItems();
                    }
                    nfcTagAdapter.getCheckedItems().add(position);
                } else {
                    nfcTagAdapter.getCheckedItems().remove(position);
                }
                nfcTagAdapter.notifyDataSetChanged();

                if (nfcTagAdapter.getOnNfcTagEditListener() != null) {
                    nfcTagAdapter.getOnNfcTagEditListener().onNfcTagChecked();
                }
            }
        });

        if (nfcTagAdapter.getSelectMode() != ListView.CHOICE_MODE_NONE) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.nfcTagFragmentButtonLayout.setBackgroundResource(R.drawable.button_background_shadowed);
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.GONE);
            holder.nfcTagFragmentButtonLayout.setBackgroundResource(R.drawable.button_background_selector);
            holder.checkbox.setChecked(false);
            nfcTagAdapter.clearCheckedItems();
        }

        if (nfcTagAdapter.getCheckedItems().contains(position)) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }
    }

    private void startPlayingSound(Chronometer chronometer, int position, final SoundBaseAdapter soundAdapter) {
        soundAdapter.setCurrentPlayingPosition(position);
        SoundBaseAdapter.setCurrentPlayingBase(SystemClock.elapsedRealtime());
        continuePlayingSound(chronometer, SoundBaseAdapter.getCurrentPlayingBase());
    }

    private void continuePlayingSound(Chronometer chronometer, long base) {
        chronometer.setBase(base);
        chronometer.start();
    }

    private void stopPlayingSound(SoundInfo soundInfo, Chronometer chronometer, final SoundBaseAdapter soundAdapter) {
        chronometer.stop();
        soundAdapter.setCurrentPlayingPosition(Constants.NO_POSITION);
        soundInfo.isPlaying = false;
    }

    public void backPackSound(SoundInfo selectedSoundInfo, BackPackSoundFragment backPackSoundActivity,
                              ArrayList<SoundInfo> soundInfoList, SoundBaseAdapter adapter) {
        copySoundBackPack(selectedSoundInfo, soundInfoList, adapter);
    }

    private void copySoundBackPack(SoundInfo selectedSoundInfo, ArrayList<SoundInfo> soundInfoList,
                                   SoundBaseAdapter adapter) {
        try {
            StorageHandler.getInstance().copySoundFileBackPack(selectedSoundInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateBackPackActivity(selectedSoundInfo.getTitle(), selectedSoundInfo.getSoundFileName(), soundInfoList,
                adapter);
    }

    public NfcTagData copyNfcTag(NfcTagData selectedNfcTagData, ArrayList<NfcTagData> nfcTagDataList, NfcTagBaseAdapter adapter) {

        return updateNfcTagAdapter(selectedNfcTagData.getNfcTagName(), selectedNfcTagData.getNfcTagUid(), nfcTagDataList,
                adapter);
    }

    public void copySound(int position, ArrayList<SoundInfo> soundInfoList, SoundBaseAdapter adapter) {
        SoundInfo soundInfo = soundInfoList.get(position);
        try {
            StorageHandler.getInstance().copySoundFile(soundInfo.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        SoundController.getInstance().updateSoundAdapter(soundInfo.getTitle(), soundInfo.getSoundFileName(),
                soundInfoList, adapter);
    }

    private void deleteNfcTag(int position, ArrayList<NfcTagData> nfcTagDataList, Activity activity) {
        nfcTagDataList.remove(position);
        ProjectManager.getInstance().getCurrentSprite().setNfcTagList(nfcTagDataList);
        activity.sendBroadcast(new Intent(ScriptActivity.ACTION_SOUND_DELETED));
    }

    public void deleteCheckedNfcTags(Activity activity, NfcTagBaseAdapter adapter, ArrayList<NfcTagData> nfcTagDataList) {
        SortedSet<Integer> checkedNfcTags = adapter.getCheckedItems();
        Iterator<Integer> iterator = checkedNfcTags.iterator();
        NfcTagController.getInstance().stopScanAndUpdateList(nfcTagDataList, adapter);
        int numberDeleted = 0;
        while (iterator.hasNext()) {
            int position = iterator.next();
            deleteNfcTag(position - numberDeleted, nfcTagDataList, activity);
            ++numberDeleted;
        }
    }

    public SoundInfo updateBackPackActivity(String title, String fileName, ArrayList<SoundInfo> soundInfoList,
                                            SoundBaseAdapter adapter) {
        title = Utils.getUniqueSoundName(title);

        SoundInfo newSoundInfo = new SoundInfo();
        newSoundInfo.setTitle(title);
        newSoundInfo.setSoundFileName(fileName);
        soundInfoList.add(newSoundInfo);

        adapter.notifyDataSetChanged();
        return newSoundInfo;
    }

    public NfcTagData updateNfcTagAdapter(String title, String uid, ArrayList<NfcTagData> nfcTagDataList,
                                        NfcTagBaseAdapter adapter) {

        //title = Utils.getUniqueSoundName(title);

        NfcTagData newNfcTagData= new NfcTagData();
        newNfcTagData.setNfcTagName(title);
        newNfcTagData.setNfcTagUid(uid);
        nfcTagDataList.add(newNfcTagData);

        adapter.notifyDataSetChanged();
        return newNfcTagData;
    }

    public boolean isSoundPlaying(MediaPlayer mediaPlayer) {
        return mediaPlayer.isPlaying();
    }

    public void stopSound(MediaPlayer mediaPlayer, ArrayList<SoundInfo> soundInfoList) {
        if (isSoundPlaying(mediaPlayer)) {
            mediaPlayer.stop();
        }

        for (int i = 0; i < soundInfoList.size(); i++) {
            soundInfoList.get(i).isPlaying = false;
        }
    }

    public void handlePlaySoundButton(View view, ArrayList<SoundInfo> soundInfoList, MediaPlayer mediaPlayer,
                                      final SoundBaseAdapter adapter) {
        final int position = (Integer) view.getTag();
        final SoundInfo soundInfo = soundInfoList.get(position);

        stopSound(mediaPlayer, soundInfoList);
        if (!soundInfo.isPlaying) {
            startSound(soundInfo, mediaPlayer);
            adapter.notifyDataSetChanged();
        }

        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaplayer) {
                soundInfo.isPlaying = false;
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void stopSoundAndUpdateList(ArrayList<SoundInfo> soundInfoList,
                                       SoundBaseAdapter adapter) {
        stopSound(null, soundInfoList);
        adapter.notifyDataSetChanged();
    }

    public void stopScanAndUpdateList(ArrayList<NfcTagData> nfcTagDataList,
                                       NfcTagBaseAdapter adapter) {
        //stopSound(null, nfcTagDataList);
        adapter.notifyDataSetChanged();
    }

    public void startSound(SoundInfo soundInfo, MediaPlayer mediaPlayer) {
        if (!soundInfo.isPlaying) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(soundInfo.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();

                soundInfo.isPlaying = true;
            } catch (IOException ioException) {
                Log.e(TAG, "Cannot start sound.", ioException);
            }
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle arguments, Activity activity) {
        Uri audioUri = null;

        if (arguments != null) {
            audioUri = (Uri) arguments.get(BUNDLE_ARGUMENTS_SELECTED_NFCTAG);
        }
        String[] projection = { MediaStore.Audio.Media.DATA };
        return new CursorLoader(activity, audioUri, projection, null, null, null);
    }

    public String onLoadFinished(Loader<Cursor> loader, Cursor data, Activity activity) {
        String audioPath = "";
        CursorLoader cursorLoader = (CursorLoader) loader;

        if (data == null) {
            audioPath = cursorLoader.getUri().getPath();
        } else {
            data.moveToFirst();
            audioPath = data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA));
        }

        //workaround for android 4.4 issue #848
        if (audioPath == null && Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            audioPath = getPathForVersionAboveEqualsVersion19(activity, cursorLoader.getUri());
        }
        if (audioPath.equalsIgnoreCase("")) {
            Utils.showErrorDialog(activity, R.string.error_load_sound);
            audioPath = "";
            return audioPath;
        } else {
            return audioPath;
        }

    }
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     *
     * solution according to: http://stackoverflow.com/questions/19834842/android-gallery-on-kitkat-returns-different-uri-for-intent-action-get-content
     *
     * @author paulburke
     */
    @TargetApi(19)
    private static String getPathForVersionAboveEqualsVersion19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
            {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



    public void handleAddButtonFromNew(NfcTagFragment nfcTagFragment) {
        ScriptActivity scriptActivity = (ScriptActivity) nfcTagFragment.getActivity();
        if (scriptActivity.getIsNfcTagFragmentFromWhenNfcBrickNew()
                && !scriptActivity.getIsNfcTagFragmentHandleAddButtonHandled()) {
            scriptActivity.setIsNfcTagFragmentHandleAddButtonHandled(true);
            nfcTagFragment.handleAddButton();
        }
    }

    public void switchToScriptFragment(NfcTagFragment nfcTagFragment) {
        ScriptActivity scriptActivity = (ScriptActivity) nfcTagFragment.getActivity();
        scriptActivity.setCurrentFragment(ScriptActivity.FRAGMENT_SCRIPTS);

        FragmentTransaction fragmentTransaction = scriptActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(nfcTagFragment);
        fragmentTransaction.show(scriptActivity.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG));
        fragmentTransaction.commit();

        scriptActivity.setIsNfcTagFragmentFromWhenNfcBrickNewFalse();
        scriptActivity.setIsNfcTagFragmentHandleAddButtonHandled(false);
    }

}
