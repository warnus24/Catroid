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
Feature: Restart WhenBroadcastReceived script

  A WhenBroadcastReceived script should be restarted when the message is broadcast again while the script is still
  running.

  Background:
    Given I have a Program
    And this program has an Object 'test object'

  Scenario: A program with two start scripts and one WhenBroadcastReceived script

    Given 'test object' has a Start script
    And this script has a Broadcast 'Print a, then b after 0.3 seconds' brick
    Given 'test object' has a Start script
    And this script has a Wait 200 milliseconds brick
    And this script has a Broadcast 'Print a, then b after 0.3 seconds' brick
    Given 'test object' has a WhenBroadcastReceived 'Print a, then b after 0.3 seconds' script
    And this script has a Print brick with 'a'
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aab'

  Scenario: A WhenBroadcastReceived script is restarted when the message it reacts to is sent from within the script

    Given 'test object' has a Start script
    And this script has a Broadcast 'print a immediately and send broadcast message again' brick
    Given 'test object' has a WhenBroadcastReceived 'print a immediately and send broadcast message again' script
    And this script has a Print brick with 'a'
    And this script has a Broadcast 'print a immediately and send broadcast message again' brick
    When I start the program
    And I wait for at least 1000 milliseconds
    Then I should see at least 'aaaaaaaaaa'
