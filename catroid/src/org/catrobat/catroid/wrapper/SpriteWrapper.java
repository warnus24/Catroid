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

package org.catrobat.catroid.wrapper;

import android.content.Context;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ChangeVolumeByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetVolumeToBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.StopAllSoundsBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.conditional.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.conditional.ChangeGhostEffectByNBrick;
import org.catrobat.catroid.content.bricks.conditional.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.conditional.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.conditional.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.conditional.ClearGraphicEffectBrick;
import org.catrobat.catroid.content.bricks.conditional.GlideToBrick;
import org.catrobat.catroid.content.bricks.conditional.HideBrick;
import org.catrobat.catroid.content.bricks.conditional.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.conditional.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.conditional.NextLookBrick;
import org.catrobat.catroid.content.bricks.conditional.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.conditional.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.conditional.PointToBrick;
import org.catrobat.catroid.content.bricks.conditional.SetBrightnessBrick;
import org.catrobat.catroid.content.bricks.conditional.SetGhostEffectBrick;
import org.catrobat.catroid.content.bricks.conditional.SetLookBrick;
import org.catrobat.catroid.content.bricks.conditional.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.conditional.SetXBrick;
import org.catrobat.catroid.content.bricks.conditional.SetYBrick;
import org.catrobat.catroid.content.bricks.conditional.ShowBrick;
import org.catrobat.catroid.content.bricks.conditional.TurnLeftBrick;
import org.catrobat.catroid.content.bricks.conditional.TurnRightBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.content.bricks.SetBounceBrick;
import org.catrobat.catroid.physics.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.physics.content.bricks.SetGravityBrick;
import org.catrobat.catroid.physics.content.bricks.SetMassBrick;
import org.catrobat.catroid.physics.content.bricks.SetPhysicsObjectTypeBrick;
import org.catrobat.catroid.physics.content.bricks.SetVelocityBrick;
import org.catrobat.catroid.physics.content.bricks.TurnLeftSpeedBrick;
import org.catrobat.catroid.soundrecorder.SoundRecorder;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteWrapper {

	private static Context context;
	private static Project project;

	private final Sprite sprite;
	private Script currentScript;

	public static String ANYBODY;

	private static final Map<Integer, LookData> copiedLooks = new HashMap<Integer, LookData>();
	private static final Map<Integer, SoundInfo> copiedSounds = new HashMap<Integer, SoundInfo>();

	private final Map<Integer, LookData> lookDataMap = new HashMap<Integer, LookData>();
	private final Map<Integer, SoundInfo> soundInfoMap = new HashMap<Integer, SoundInfo>();

	public SpriteWrapper(Sprite sprite, boolean addToProject) {
		this.sprite = sprite;

		if (addToProject) {
			project.addSprite(this.sprite);
		}
	}


	public SpriteWrapper(Sprite sprite) {
		this(sprite, true);
	}

	public static void init(Context context, Project project) {
		SpriteWrapper.context = context;
		SpriteWrapper.project = project;
		SpriteWrapper.ANYBODY = context.getString(R.string.collision_with_anybody);
	}

	public SpriteWrapper clone() {
		return new SpriteWrapper(sprite.clone());
	}

	private void add(Script script) {
		currentScript = script;
		sprite.addScript(script);
	}

	private void add(Brick brick) {
		if (currentScript == null) {
			whenProgramStarted();
		}
		currentScript.addBrick(brick);
	}

	public SpriteWrapper whenProgramStarted() {
		add(new StartScript(sprite));
		return this;
	}

	public SpriteWrapper whenTapped() {
		add(new WhenScript(sprite));
		return this;
	}

	public SpriteWrapper whenCollisionBetween(SpriteWrapper collisionSprite) {
		return whenCollisionBetween(collisionSprite.sprite);
	}

	public SpriteWrapper whenCollisionBetween(Sprite collisionSprite) {
		return whenCollisionBetween(collisionSprite.getName());
	}

	public SpriteWrapper whenCollisionBetween(String collisionSpriteName) {
		add(new CollisionScript(sprite, sprite.getName() + "<->" + collisionSpriteName));
		return this;
	}

	public SpriteWrapper wait(double seconds) {
		return wait(new Formula(seconds));
	}

	public SpriteWrapper wait(String seconds) {
		return wait(FormulaParser.parse(seconds));
	}

	public SpriteWrapper wait(Formula seconds) {
		add(new WaitBrick(sprite, seconds));
		return this;
	}

	public SpriteWrapper whenIReceive(String message) {
		add(new BroadcastScript(sprite, message));
		return this;
	}

	public SpriteWrapper broadcast(String message) {
		add(new BroadcastBrick(sprite, message));
		return this;
	}

	public SpriteWrapper broadcastAndWait(String message) {
		add(new BroadcastWaitBrick(sprite, message));
		return this;
	}

	public SpriteWrapper note(String note) {
		add(new NoteBrick(sprite, note));
		return this;
	}

	public SpriteWrapper forever() {
		add(new ForeverBrick(sprite));
		return this;
	}

	public SpriteWrapper endForever() {
		add(new LoopEndlessBrick(sprite, getLoopBeginBrick()));
		return this;
	}

	public SpriteWrapper ifCondition(double condition) {
		return ifCondition(new Formula(condition));
	}

	public SpriteWrapper ifCondition(String condition) {
		return ifCondition(FormulaParser.parse(condition));
	}

	public SpriteWrapper ifCondition(Formula condition) {
		add(new IfLogicBeginBrick(sprite, condition));
		return this;
	}

	public SpriteWrapper elseCondition() {
		add(new IfLogicElseBrick(sprite, getIfLogicBeginBrick()));
		return this;
	}

	public SpriteWrapper endIfCondition() {
		add(new IfLogicEndBrick(sprite, getIfLogicElseBrick(), getIfLogicBeginBrick()));
		return this;
	}

	public SpriteWrapper repeat(double timesToRepeat) {
		return repeat(new Formula(timesToRepeat));
	}

	public SpriteWrapper repeat(String timesToRepeat) {
		return repeat(FormulaParser.parse(timesToRepeat));
	}

	public SpriteWrapper repeat(Formula timesToRepeat) {
		add(new RepeatBrick(sprite, timesToRepeat));
		return this;
	}

	public SpriteWrapper endRepeat() {
		add(new LoopEndBrick(sprite, getLoopBeginBrick()));
		return this;
	}

	public SpriteWrapper placeAt(double x, double y) {
		return placeAt(new Formula(x), new Formula(y));
	}

	public SpriteWrapper placeAt(String x, String y) {
		return placeAt(FormulaParser.parse(x), FormulaParser.parse(y));
	}

	public SpriteWrapper placeAt(Formula x, Formula y) {
		add(new PlaceAtBrick(sprite, x, y));
		return this;
	}

	public SpriteWrapper setX(double x) {
		return setX(new Formula(x));
	}

	public SpriteWrapper setX(String x) {
		return setX(FormulaParser.parse(x));
	}

	public SpriteWrapper setX(Formula x) {
		add(new SetXBrick(sprite, x));
		return this;
	}

	public SpriteWrapper setY(double y) {
		return setY(new Formula(y));
	}

	public SpriteWrapper setY(String y) {
		return setY(FormulaParser.parse(y));
	}

	public SpriteWrapper setY(Formula y) {
		add(new SetYBrick(sprite, y));
		return this;
	}

	public SpriteWrapper changeXBy(double x) {
		return changeXBy(new Formula(x));
	}

	public SpriteWrapper changeXBy(String x) {
		return changeXBy(FormulaParser.parse(x));
	}

	public SpriteWrapper changeXBy(Formula x) {
		add(new ChangeXByNBrick(sprite, x));
		return this;
	}

	public SpriteWrapper changeYBy(double y) {
		return changeYBy(new Formula(y));
	}

	public SpriteWrapper changeYBy(String y) {
		return changeYBy(FormulaParser.parse(y));
	}

	public SpriteWrapper changeYBy(Formula y) {
		add(new ChangeYByNBrick(sprite, y));
		return this;
	}

	public SpriteWrapper ifOnEdgeBounce() {
		add(new IfOnEdgeBounceBrick(sprite));
		return this;
	}

	public SpriteWrapper moveNSteps(double steps) {
		return moveNSteps(new Formula(steps));
	}

	public SpriteWrapper moveNSteps(String steps) {
		return moveNSteps(FormulaParser.parse(steps));
	}

	public SpriteWrapper moveNSteps(Formula steps) {
		add(new MoveNStepsBrick(sprite, steps));
		return this;
	}

	public SpriteWrapper turnLeft(double degrees) {
		return turnLeft(new Formula(degrees));
	}

	public SpriteWrapper turnLeft(String degrees) {
		return turnLeft(FormulaParser.parse(degrees));
	}

	public SpriteWrapper turnLeft(Formula degrees) {
		add(new TurnLeftBrick(sprite, degrees));
		return this;
	}

	public SpriteWrapper turnRight(double degrees) {
		return turnRight(new Formula(degrees));
	}

	public SpriteWrapper turnRight(String degrees) {
		return turnRight(FormulaParser.parse(degrees));
	}

	public SpriteWrapper turnRight(Formula degrees) {
		add(new TurnRightBrick(sprite, degrees));
		return this;
	}

	public SpriteWrapper pointInDirection(double direction) {
		return pointInDirection(new Formula(direction));
	}

	public SpriteWrapper pointInDirection(String direction) {
		return pointInDirection(FormulaParser.parse(direction));
	}

	public SpriteWrapper pointInDirection(Formula direction) {
		add(new PointInDirectionBrick(sprite, direction));
		return this;
	}

	public SpriteWrapper pointTowards(Sprite pointedSprite) {
		add(new PointToBrick(sprite, pointedSprite));
		return this;
	}

	public SpriteWrapper pointTowards(SpriteWrapper pointedSprite) {
		add(new PointToBrick(sprite, pointedSprite.sprite));
		return this;
	}

	public SpriteWrapper glideTo(double x, double y, double seconds) {
		return glideTo(new Formula(x), new Formula(y), new Formula(seconds));
	}

	public SpriteWrapper glideTo(String x, String y, String seconds) {
		return glideTo(FormulaParser.parse(x), FormulaParser.parse(y), FormulaParser.parse(seconds));
	}

	public SpriteWrapper glideTo(Formula x, Formula y, Formula seconds) {
		add(new GlideToBrick(sprite, x, y, seconds));
		return this;
	}

	public SpriteWrapper setPhysicalObject(PhysicsObject.Type type) {
		add(new SetPhysicsObjectTypeBrick(sprite, type));
		return this;
	}

	public SpriteWrapper setMass(double kg) {
		return setMass(new Formula(kg));
	}

	public SpriteWrapper setMass(String kg) {
		return setMass(FormulaParser.parse(kg));
	}

	public SpriteWrapper setMass(Formula kg) {
		add(new SetMassBrick(sprite, kg));
		return this;
	}

	public SpriteWrapper setBounceFactor(double bounceFactor) {
		return setBounceFactor(new Formula(bounceFactor));
	}

	public SpriteWrapper setBounceFactor(String bounceFactor) {
		return setBounceFactor(FormulaParser.parse(bounceFactor));
	}

	public SpriteWrapper setBounceFactor(Formula bounceFactor) {
		add(new SetBounceBrick(sprite, bounceFactor));
		return this;
	}

	public SpriteWrapper setFriction(double friction) {
		return setFriction(new Formula(friction));
	}

	public SpriteWrapper setFriction(String friction) {
		return setFriction(FormulaParser.parse(friction));
	}

	public SpriteWrapper setFriction(Formula friction) {
		add(new SetFrictionBrick(sprite, friction));
		return this;
	}

	public SpriteWrapper setGravity(double x, double y) {
		return setGravity(new Formula(x), new Formula(y));
	}

	public SpriteWrapper setGravity(String x, String y) {
		return setGravity(FormulaParser.parse(x), FormulaParser.parse(y));
	}

	public SpriteWrapper setGravity(Formula x, Formula y) {
		add(new SetGravityBrick(sprite, x, y));
		return this;
	}

	public SpriteWrapper setVelocity(double x, double y) {
		return setVelocity(new Formula(x), new Formula(y));
	}

	public SpriteWrapper setVelocity(String x, String y) {
		return setVelocity(FormulaParser.parse(x), FormulaParser.parse(y));
	}

	public SpriteWrapper setVelocity(Formula x, Formula y) {
		add(new SetVelocityBrick(sprite, x, y));
		return this;
	}

	public SpriteWrapper turnLeftSpeed(double degreesPerSecond) {
		return turnLeftSpeed(new Formula(degreesPerSecond));
	}

	public SpriteWrapper turnLeftSpeed(String degreesPerSecond) {
		return turnLeftSpeed(FormulaParser.parse(degreesPerSecond));
	}

	public SpriteWrapper turnLeftSpeed(Formula degreesPerSecond) {
		add(new TurnLeftSpeedBrick(sprite, degreesPerSecond));
		return this;
	}

	public SpriteWrapper turnRightSpeed(double degreesPerSecond) {
		return turnRightSpeed(new Formula(degreesPerSecond));
	}

	public SpriteWrapper turnRightSpeed(String degreesPerSecond) {
		return turnRightSpeed(FormulaParser.parse(degreesPerSecond));
	}

	public SpriteWrapper turnRightSpeed(Formula degreesPerSecond) {
		add(new TurnRightBrick(sprite, degreesPerSecond));
		return this;
	}

	public SpriteWrapper startSound(int id) {
		PlaySoundBrick playSoundBrick = new PlaySoundBrick(sprite);
		playSoundBrick.setSoundInfo(getSoundInfo(id));
		add(playSoundBrick);
		return this;
	}

	public SpriteWrapper stopAllSounds() {
		add(new StopAllSoundsBrick(sprite));
		return this;
	}

	public SpriteWrapper setVolume(double volume) {
		return setVolume(new Formula(volume));
	}

	public SpriteWrapper setVolume(String volume) {
		return setVolume(FormulaParser.parse(volume));
	}

	public SpriteWrapper setVolume(Formula volume) {
		add(new SetVolumeToBrick(sprite, volume));
		return this;
	}

	public SpriteWrapper changeVolumeBy(double volume) {
		return changeVolumeBy(new Formula(volume));
	}

	public SpriteWrapper changeVolumeBy(String volume) {
		return changeVolumeBy(FormulaParser.parse(volume));
	}

	public SpriteWrapper changeVolumeBy(Formula volume) {
		add(new ChangeVolumeByNBrick(sprite, volume));
		return this;
	}

	public SpriteWrapper speak(String text) {
		add(new SpeakBrick(sprite, text));
		return this;
	}

	public SpriteWrapper setLook(int id) {
		SetLookBrick setLookBrick = new SetLookBrick(sprite);
		setLookBrick.setLook(getLookData(id));
		add(setLookBrick);
		return this;
	}

	public SpriteWrapper nextLook() {
		add(new NextLookBrick(sprite));
		return this;
	}

	public SpriteWrapper setSize(double size) {
		return setSize(new Formula(size));
	}

	public SpriteWrapper setSize(String size) {
		return setSize(FormulaParser.parse(size));
	}

	public SpriteWrapper setSize(Formula size) {
		add(new SetSizeToBrick(sprite, size));
		return this;
	}

	public SpriteWrapper changeSizeBy(double size) {
		return changeSizeBy(new Formula(size));
	}

	public SpriteWrapper changeSizeBy(String size) {
		return changeSizeBy(FormulaParser.parse(size));
	}

	public SpriteWrapper changeSizeBy(Formula size) {
		add(new ChangeSizeByNBrick(sprite, size));
		return this;
	}

	public SpriteWrapper hide() {
		add(new HideBrick(sprite));
		return this;
	}

	public SpriteWrapper show() {
		add(new ShowBrick(sprite));
		return this;
	}

	public SpriteWrapper setTransparency(double transparency) {
		return setTransparency(new Formula(transparency));
	}

	public SpriteWrapper setTransparency(String transparency) {
		return setTransparency(FormulaParser.parse(transparency));
	}

	public SpriteWrapper setTransparency(Formula transparancy) {
		add(new SetGhostEffectBrick(sprite, transparancy));
		return this;
	}

	public SpriteWrapper changeTransparencyBy(double transparency) {
		return changeTransparencyBy(new Formula(transparency));
	}

	public SpriteWrapper changeTransparencyBy(String transparency) {
		return changeTransparencyBy(FormulaParser.parse(transparency));
	}

	public SpriteWrapper changeTransparencyBy(Formula transparency) {
		add(new ChangeGhostEffectByNBrick(sprite, transparency));
		return this;
	}

	public SpriteWrapper setBrightness(double brightness) {
		return setBrightness(new Formula(brightness));
	}

	public SpriteWrapper setBrightness(String brightness) {
		return setBrightness(FormulaParser.parse(brightness));
	}

	public SpriteWrapper setBrightness(Formula brightness) {
		add(new SetBrightnessBrick(sprite, brightness));
		return this;
	}

	public SpriteWrapper changeBrightnessBy(double brightness) {
		return changeBrightnessBy(new Formula(brightness));
	}

	public SpriteWrapper changeBrightnessBy(String brightness) {
		return changeBrightnessBy(FormulaParser.parse(brightness));
	}

	public SpriteWrapper changeBrightnessBy(Formula brightness) {
		add(new ChangeBrightnessByNBrick(sprite, brightness));
		return this;
	}

	public SpriteWrapper clearGraphicEffects() {
		add(new ClearGraphicEffectBrick(sprite));
		return this;
	}

	public SpriteWrapper setVariable(String variable, double value) {
		return setVariable(variable, new Formula(value));
	}

	public SpriteWrapper setVariable(String variable, String value) {
		return setVariable(variable, FormulaParser.parse(value));
	}

	public SpriteWrapper setVariable(String variable, Formula value) {
		add(new SetVariableBrick(sprite, value, getUserVariable(variable)));
		return this;
	}

	public SpriteWrapper changeVariable(String variable, double value) {
		return changeVariable(variable, new Formula(value));
	}

	public SpriteWrapper changeVariable(String variable, String value) {
		return changeVariable(variable, FormulaParser.parse(value));
	}

	public SpriteWrapper changeVariable(String variable, Formula value) {
		add(new ChangeVariableBrick(sprite, value, getUserVariable(variable)));
		return this;
	}

	private UserVariable getUserVariable(String variable) {
		return project.getUserVariables().getUserVariable(variable, sprite);
	}

	public SpriteWrapper addSpriteVariable(String variable) {
		ProjectManager.getInstance().setCurrentSprite(sprite);
		project.getUserVariables().addSpriteUserVariable(variable);
		return this;
	}

	public SpriteWrapper addProjectVariable(String variable) {
		project.getUserVariables().addProjectUserVariable(variable);
		return this;
	}

	public SpriteWrapper addLook(int id, String name) {
		if (!copiedLooks.containsKey(id)) {
			copyLook(id);
		}

		LookData lookData = new LookData();
		lookData.setLookName(name);
		lookData.setLookFilename(copiedLooks.get(id).getLookFileName());
		sprite.getLookDataList().add(lookData);

		lookDataMap.put(id, lookData);

		return this;
	}

	public LookData getLookData(int id) {
		if (!lookDataMap.containsKey(id)) {
			addLook(id, String.valueOf(id));
		}
		return lookDataMap.get(id);
	}

	public void copyLook(int id) {
		String name = String.valueOf(id);
		try {
			File file = UtilFile.copyImageFromResourceIntoProject(project.getName(), name
					+ Constants.IMAGE_STANDARD_EXTENTION, id, context, true, 1.0);

			LookData lookData = new LookData();
			lookData.setLookName(name);
			lookData.setLookFilename(file.getName());

			copiedLooks.put(id, lookData);
		} catch (IOException ioException) {
		}
	}

	public SpriteWrapper addSound(int id, String name) {
		if (!copiedSounds.containsKey(id)) {
			copySounds(id);
		}

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setTitle(name);
		soundInfo.setSoundFileName(copiedSounds.get(id).getSoundFileName());
		sprite.getSoundList().add(soundInfo);

		copiedSounds.put(id, soundInfo);

		return this;
	}

	public SoundInfo getSoundInfo(int id) {
		if (!soundInfoMap.containsKey(id)) {
			addSound(id, String.valueOf(id));
		}
		return soundInfoMap.get(id);
	}

	public void copySounds(int id) {
		String name = String.valueOf(id);
		try {
			File file = UtilFile.copySoundFromResourceIntoProject(project.getName(), name
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_sound_mole_1, context, true);

			SoundInfo soundInfo = new SoundInfo();
			soundInfo.setTitle(name);
			soundInfo.setSoundFileName(file.getName());

			copiedSounds.put(id, soundInfo);
		} catch (IOException ioException) {
		}
	}

	private LoopBeginBrick getLoopBeginBrick() {
		return (LoopBeginBrick) getBrickOfType(LoopBeginBrick.class);
	}

	private IfLogicBeginBrick getIfLogicBeginBrick() {
		return (IfLogicBeginBrick) getBrickOfType(IfLogicBeginBrick.class);
	}

	private IfLogicElseBrick getIfLogicElseBrick() {
		return (IfLogicElseBrick) getBrickOfType(IfLogicElseBrick.class);
	}

	private Object getBrickOfType(Class<?> type) {
		List<Brick> bricks = currentScript.getBrickList();

		for (int index = bricks.size() - 1; index >= 0; index--) {
			Brick currentBrick = bricks.get(index);
			if (type.isInstance(currentBrick)) {
				return currentBrick;
			}
		}
		return null;
	}
}
