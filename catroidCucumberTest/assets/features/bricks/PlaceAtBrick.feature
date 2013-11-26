# Catroid: An on-device visual programming system for Android devices
# Copyright (C) 2010-2013 The Catrobat Team
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
Feature: Place at brick

  A brick placing an Object at a position

  Background:
    Given I have a Program
    And this program has an Object 'Object'

  Scenario Outline: Place an Object
    Given 'Object' has a Start script
    And this script has a place at <xPosition> <yPosition> brick
    When I start the program
    And I wait until the program has stopped
    Then 'Object' should be at position <xPosition> <yPosition>

    Examples:
      | xPosition   | yPosition   |
      | 200         | 100         |
      | 2147483647  | 2147483647  |
      | -2147483648 | -2147483648 |
