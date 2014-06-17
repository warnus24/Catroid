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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.StorageHandler;

public abstract class ProjectWrapper {
	protected static final String AND = "AND";
	protected static final String OR = "OR";
	protected static final String EQUALS = "=";
	protected static final String NOT_EQUALS = "!=";
	protected static final String LESS_EQUALS = "<=";
	protected static final String LESS = "<";
	protected static final String GREATER_EQUALS = ">=";
	protected static final String GREATER = ">";
	protected static final String PLUS = "+";
	protected static final String MINUS = "-";
	protected static final String MULT = "*";
	protected static final String DIV = "/";
	protected static final String NOT = "!";

	protected static final String SIN = "sin";
	protected static final String COS = "cos";
	protected static final String TAN = "tan";
	protected static final String LN = "ln";
	protected static final String LOG = "log";
	protected static final String SQRT = "sqrt";
	protected static final String ROUND = "round";
	protected static final String ABS = "abs";
	protected static final String ARCSIN = "arcsin";
	protected static final String ARCCOS = "arccos";
	protected static final String ARCTAN = "arctan";
	protected static final String RANDOM = "random";
	protected static final String MOD = "mod";
	protected static final String EXP = "exp";
	protected static final String MAX = "max";
	protected static final String MIN = "min";

	protected static final String PI = "PI";
	protected static final String TRUE = "TRUE";
	protected static final String FALSE = "FALSE";

	protected static final String ACCELERATION_X = "acceleration_x";
	protected static final String ACCELERATION_Y = "acceleration_y";
	protected static final String ACCELERATION_Z = "acceleration_z";
	protected static final String COMPASS_DIRECTION = "compass_direction";
	protected static final String INCLINATION_X = "inclination_x";
	protected static final String INCLINATION_Y = "inclination_y";
	protected static final String LOUDNESS = "loudness";
	protected static final String POSITION_X = "position_x";
	protected static final String POSITION_Y = "position_y";
	protected static final String TRANSPARENCY = "transparency";
	protected static final String BRIGHTNESS = "brightness";
	protected static final String SIZE = "size";
	protected static final String DIRECTION = "direction";
	protected static final String LAYER = "layer";
	protected static final String X_VELOCITY = "x_velocity";
	protected static final String Y_VELOCITY = "y_velocity";
	protected static final String ANGULAR_VELOCITY = "angular_velocity";

	private final Project project;
	private final SpriteWrapper background;

	public ProjectWrapper(Context context, String projectName) {
		this(context, projectName, 0, 0);
	}

	public ProjectWrapper(Context context, String projectName, int width, int height) {
		project = new Project(context, projectName);
		project.setDeviceData(context);

		if (width > 0 && height > 0) {
			setSize(width, height);
		}

		SpriteWrapper.init(context, project);

		background = new SpriteWrapper(project.getSpriteList().get(0), false);
	}

	public ProjectWrapper setSize(int width, int height) {
		project.getXmlHeader().virtualScreenWidth = width;
		project.getXmlHeader().virtualScreenHeight = height;
		return this;
	}

	public SpriteWrapper getBackground() {
		return background;
	}

	public Project createProject() {
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		createSprites();

		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	protected abstract void createSprites();
}
