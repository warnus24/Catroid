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
Feature: Broadcast brick

  A Broadcast brick should send a message and When scripts should react to it.

  Background:
    Given I have a Program
    And this program has an Object 'Object'

  Scenario: A Broadcast brick sends a message in a program with one When script
    Given 'Object' has a Start script
    And this script has a Broadcast 'hello' brick
    And this script has a Print brick with
      """
      I am the Start script.
      """
    Given 'Object' has a When 'hello' script
    And this script has a Print brick with
      """
      I am the When 'hello' script.
      """
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output
      """
      I am the Start script.
      I am the When 'hello' script.
      """

  Scenario: A Broadcast brick sends a message in a program with two When scripts
    Given 'Object' has a Start script
    And this script has a Broadcast 'hello' brick
    And this script has a Wait 200 milliseconds brick
    And this script has a Print brick with
      """
      I am the Start script.
      """
    Given 'Object' has a When 'hello' script
    And this script has a Print brick with
      """
      I am the first When 'hello' script.
      """
    Given 'Object' has a When 'hello' script
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with
      """
      I am the second When 'hello' script.
      """
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output
      """
      I am the first When 'hello' script.
      I am the Start script.
      I am the second When 'hello' script.
      """
