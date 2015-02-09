/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.uitest.stage;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.stage.OnUtteranceCompletedListenerContainer;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SpeakStageTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private SoundManagerMock soundManagerMock;

	private final String testText = "Test test.";
	private long byteLengthOfTestText;
	private final File speechFileTestText = new File(Constants.TEXT_TO_SPEECH_TMP_PATH, Utils.md5Checksum(testText)
			+ Constants.TEXT_TO_SPEECH_EXTENSION);

	public SpeakStageTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		soundManagerMock = new SoundManagerMock();
		Reflection.setPrivateField(SoundManager.class, "INSTANCE", soundManagerMock);

		deleteSpeechFiles();
		detectReferenceFileSize(speechFileTestText, testText);
		createTestProject();
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		deleteSpeechFiles();
	}

	private void createTestProject() {
		Sprite spriteNormal = new Sprite("testNormalBehaviour");

		Script startScriptNormal = new StartScript();
		startScriptNormal.addBrick(new SpeakBrick(testText));
		startScriptNormal.addBrick(new WaitBrick(1500));

		spriteNormal.addScript(startScriptNormal);

		ArrayList<Sprite> spriteListNormal = new ArrayList<Sprite>();
		spriteListNormal.add(spriteNormal);

		UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteListNormal, getActivity().getApplicationContext());
		prepareStageForTesting(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
	}

	private void deleteSpeechFiles() {
		File pathToSpeechFiles = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		pathToSpeechFiles.mkdirs();
		File files[] = pathToSpeechFiles.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	private void prepareStageForTesting(String projectName) {
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
	}

	@Device
	public void testSimpleSpeech() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		int currentTry = 1;
		boolean found = false;
		while (++currentTry != 10) {
			if (speechFileTestText.exists()) {
				found = true;
				break;
			}
			solo.sleep(3000);
		}

		assertTrue("speechFileTestText does not exist.", found);
	}

	private void detectReferenceFileSize(File file, String text){

		solo.sleep(1000);
	}

	private class SoundManagerMock extends SoundManager {

		private final Set<String> playedSoundFiles = new HashSet<String>();

		@Override
		public synchronized void playSoundFile(String pathToSoundfile) {
			playedSoundFiles.add(pathToSoundfile);
		}
	}
}