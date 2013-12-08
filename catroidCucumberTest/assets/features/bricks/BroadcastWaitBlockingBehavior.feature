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
Feature: Broadcast & Wait Blocking Behavior (like in Scratch)

  If a broadcast is sent while a Broadcast Wait brick is waiting for the same message, the
  responding When scripts should be restarted and the Broadcast Wait brick should stop waiting
  and immediately continue executing the rest of the script.

  Background:
    Given I have a Program
    And this program has an Object 'Object'

  Scenario: A waiting BroadcastWait brick is unblocked when the same broadcast message is present
    Given 'Object' has a Start script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Wait 100 milliseconds brick
    And this script has a Print brick with '-S1-'
    Given 'Object' has a Start script
    And this script has a Wait 300 milliseconds brick
    And this script has a Broadcast 'hello' brick
    Given 'Object' has a When 'hello' script
    And this script has a Print brick with '-W1-'
    And this script has a Wait 400 milliseconds brick
    And this script has a Print brick with '-W2-'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output '-W1--W1--S1--W2-'

  Scenario: A waiting BroadcastWait brick is unblocked via another BroadcastWait brick
    Given 'Object' has a Start script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Wait 100 milliseconds brick
    And this script has a Print brick with '-S1-'
    Given 'Object' has a Start script
    And this script has a Wait 200 milliseconds brick
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with '-S2-'
    Given 'Object' has a When 'hello' script
    And this script has a Wait 1000 milliseconds brick
    And this script has a Print brick with '-W1-'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output '-S1--W1--S2-'

  Scenario: A waiting BroadcastWait brick is unblocked when the same broadcast message is present and there are two When scripts
    Given 'Object' has a Start script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with '-S1-'
    Given 'Object' has a When 'hello' script
    And this script has a Wait 400 milliseconds brick
    And this script has a Print brick with '-W1-'
    Given 'Object' has a When 'hello' script
    And this script has a Wait 500 milliseconds brick
    And this script has a Print brick with '-W2-'
    Given 'Object' has a Start script
    And this script has a Wait 100 milliseconds brick
    And this script has a Broadcast 'hello' brick
    And this script has a Wait 100 milliseconds brick
    And this script has a Print brick with '-S2-'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output '-S1--S2--W1--W2-'
