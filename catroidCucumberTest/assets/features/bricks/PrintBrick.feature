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
Feature: Print brick

  A Print brick prints a given text on the screen.

  Background:
    Given I have a Program
    And this program has an Object 'Object'

  Scenario: A Print brick prints one line
    Given 'Object' has a Start script
    And this script has a Print brick with 'Hello, world!'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'Hello, world!'

  Scenario: A Print brick prints two lines
    Given 'Object' has a Start script
    And this script has a Print brick with
      """
      Hello,
      world!
      """
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output
      """
      Hello,
      world!
      """

  Scenario: A Print brick with an empty text
    Given 'Object' has a Start script
    And this script has a Print brick with ''
    When I start the program
    And I wait until the program has stopped
    Then I should see no printed output
