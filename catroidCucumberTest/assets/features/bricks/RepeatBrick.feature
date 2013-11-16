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
Feature: Repeat brick

  A Repeat brick repeats another set of bricks a given number of times.

  Background:
    Given I have a Program
    And this program has an Object 'Object'

  Scenario: Increment variable inside loop
    Given 'Object' has a Start script
    And this script has a set 'i' to 0 brick
    And this script has a Repeat 8 times brick
    And this script has a change 'i' by 1 brick
    And this script has a Repeat end brick
    When I start the program
    And I wait until the program has stopped
    Then the variable 'i' should be equal 8
