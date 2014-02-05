# Catroid: An on-device visual programming system for Android devices
# Copyright (C) 2010-2014 The Catrobat Team
# (<http://developer.catrobat.org/credits>)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# An additional term exception under section 7 of the GNU Affero
# General Public License, version 3, is available at
# http://developer.catrobat.org/license_additional_term
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
Feature: If on edge bounce brick

  A brick causing a moving Object to bounce at the edge of the screen

  Background:
    Given I have a Program
    And this program has an Object which should bounce off screen borders

  Scenario Outline: Bounce at the border of the screen
    When an Object would move beyond the <given> border
    Then the Object should still be located within the boundaries of the screen

    Examples:
      | given  |
      | top    |
      | bottom |
      | left   |
      | right  |

  Scenario Outline: Bounce at the corner of the screen
    When an Object would move beyond the <given> corner in <this> direction
    Then the Object should still be located within the boundaries of the screen and move in <that> direction

    Examples:
      | given        | this | that |
      | up left      | 135  | 135  |
      | up left      | -45  | 135  |
      | up right     | -135 | -135 |
      | up right     | 45   | -135 |
      | bottom left  | 45   | 45   |
      | bottom left  | -135 | 45   |
      | bottom right | -45  | -45  |
      | bottom right | 135  | -45  |
