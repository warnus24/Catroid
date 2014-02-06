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
Feature: BroadcastAndWait Blocking Behavior (like in Scratch)

  If there exists no WhenBroadcastReceived script, a BroadcastAndWait should not wait at all. If there are one or more
  matching WhenBroadcastReceived scripts, execution of the script containing the BroadcastAndWait is paused until all
  WhenBroadcastReceived scripts are finished. If a broadcast is sent while a BroadcastAndWait brick is waiting for the
  same message, the responding WhenBroadcastReceived scripts is restarted; the BroadcastAndWait brick  stops waiting
  and immediately continues executing the rest of the script. The same applies for a BroadcastAndWait brick which is
  unblocked by another BroadcastAndWait brick; the first one continues while the seconds one starts waiting. Just
  like a Broadcast brick, a BroadcastAndWait brick triggers all matching WhenBroadcastReceived in all Objects of the
  current program.

  Background:
    Given I have a Program
    And this program has an Object 'test object'

  Scenario: A BroadcastAndWait brick without a corresponding WhenBroadcastReceived script should *not* wait for
    anything.

    Given 'test object' has a Start script
    And this script has a BroadcastAndWait 'This message does not matter as there is no receiver script' brick
    And this script has a Print brick with 'a'
    Given 'test object' has a Start script
    And this script has a Wait 200 milliseconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'ab'

  Scenario: A waiting BroadcastAndWait brick is unblocked when the broadcast message is sent again.

    Given 'test object' has a Start script
    And this script has a BroadcastAndWait 'Print b after 0.3 seconds, and then c after another 0.5 seconds' brick
    And this script has a Print brick with 'a'
    Given 'test object' has a Start script
    And this script has a Wait 600 milliseconds brick
    And this script has a Broadcast 'Print b after 0.3 seconds, and then c after another 0.5 seconds' brick
    Given 'test object' has a WhenBroadcastReceived 'Print b after 0.3 seconds, and then c after another 0.5 seconds' script
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with 'b'
    And this script has a Wait 500 milliseconds brick
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'babc'

  Scenario: A waiting BroadcastAndWait brick is unblocked via another BroadcastAndWait brick.

    Given 'test object' has a Start script
    And this script has a BroadcastAndWait 'Print b after 5 seconds' brick
    And this script has a Wait 1 second brick
    And this script has a Print brick with 'a'
    Given 'test object' has a Start script
    And this script has a Wait 2 seconds brick
    And this script has a BroadcastAndWait 'Print b after 5 seconds' brick
    And this script has a Print brick with 'd'
    Given 'test object' has a WhenBroadcastReceived 'Print b after 5 seconds' script
    And this script has a Wait 5 seconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abd'

  Scenario: A waiting BroadcastAndWait brick is unblocked when the same broadcast message is sent again and there are
    two WhenBroadcastReceived scripts responding to the same message.

    Given 'test object' has a Start script
    And this script has a BroadcastAndWait 'Print b and c from different scripts' brick
    And this script has a Print brick with 'a'
    Given 'test object' has a WhenBroadcastReceived 'Print b and c from different scripts' script
    And this script has a Wait 400 milliseconds brick
    And this script has a Print brick with 'b'
    Given 'test object' has a WhenBroadcastReceived 'Print b and c from different scripts' script
    And this script has a Wait 500 milliseconds brick
    And this script has a Print brick with 'c'
    Given 'test object' has a Start script
    And this script has a Wait 100 milliseconds brick
    And this script has a Broadcast 'Print b and c from different scripts' brick
    And this script has a Wait 100 milliseconds brick
    And this script has a Print brick with 'd'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'adbc'

  Scenario: A BroadcastAndWait brick repeatedly triggers a WhenBroadcastReceived script which is restarted immediately.

    Given 'test object' has a Start script
    And this script has a Repeat 5 times brick
    And this script has a Broadcast 'Send the BroadcastAndWait message' brick
    And this script has a Wait 2 second brick
    And this script has a Repeat end brick
    Given 'test object' has a WhenBroadcastReceived 'Send the BroadcastAndWait message' script
    And this script has a BroadcastAndWait 'Print a, then b after 7 seconds' brick
    And this script has a Print brick with 'c'
    Given 'test object' has a WhenBroadcastReceived 'Print a, then b after 7 seconds' script
    And this script has a Print brick with 'a'
    And this script has a Wait 7 seconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aaaaabc'

  Scenario: A Broadcast brick repeatedly triggers a WhenBroadcastReceived script which is restarted immediately.

    Given 'test object' has a Start script
    And this script has a Repeat 5 times brick
    And this script has a Broadcast 'Send the Broadcast message' brick
    And this script has a Wait 2 seconds brick
    And this script has a Print brick with 'c'
    And this script has a Wait 1 second brick
    And this script has a Repeat end brick
    Given 'test object' has a WhenBroadcastReceived 'Send the Broadcast message' script
    And this script has a Broadcast 'Print b after 0.5 seconds, then d after another 7 seconds' brick
    And this script has a Print brick with 'a'
    Given 'test object' has a WhenBroadcastReceived 'Print b after 0.5 seconds, then d after another 7 seconds' script
    And this script has a Wait 500 milliseconds brick
    And this script has a Print brick with 'b'
    And this script has a Wait 7 seconds brick
    And this script has a Print brick with 'd'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcabcabcabcabcd'

  Scenario: A BroadcastAndWait brick is unblocked by a Broadcast brick. After that the BroadcastAndWait is triggered
    again.

    Given 'test object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a Broadcast 'Send the BroadcastAndWait message' brick
    And this script has a Wait 6 seconds brick
    And this script has a Repeat end brick
    Given 'test object' has a WhenBroadcastReceived 'Send the BroadcastAndWait message' script
    And this script has a BroadcastAndWait 'Print a after 1 second, then b after another 5 seconds' brick
    And this script has a Wait 2 seconds brick
    And this script has a Print brick with 'd'
    Given 'test object' has a Start script
    And this script has a Wait 3 seconds brick
    And this script has a Broadcast 'Print a after 1 second, then b after another 5 seconds' brick
    And this script has a Print brick with 'c'
    Given 'test object' has a WhenBroadcastReceived 'Print a after 1 second, then b after another 5 seconds' script
    And this script has a Wait 1 second brick
    And this script has a Print brick with 'a'
    And this script has a Wait 5 seconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'acadabd'

  Scenario: A BroadcastAndWait brick waits for two WhenBroadcastReceived scripts to finish and is unblocked by a
    Broadcast brick. After that the BroadcastAndWait is triggered again.

    Given 'test object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a BroadcastAndWait 'Print a and b from two different scripts' brick
    And this script has a Wait 6 seconds brick
    And this script has a Print brick with 'c'
    And this script has a Wait 1 second brick
    And this script has a Repeat end brick
    Given 'test object' has a Start script
    And this script has a Wait 2 seconds brick
    And this script has a Broadcast 'Print a and b from two different scripts' brick
    Given 'test object' has a WhenBroadcastReceived 'Print a and b from two different scripts' script
    And this script has a Print brick with 'a'
    Given 'test object' has a WhenBroadcastReceived 'Print a and b from two different scripts' script
    And this script has a Wait 3 seconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aabcabc'

  Scenario: Correct consecutive executions of one BroadcastAndWait brick.

    Given 'test object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a BroadcastAndWait 'Print a, then b after 1 second' brick
    And this script has a Print brick with 'c'
    And this script has a Repeat end brick
    Given 'test object' has a WhenBroadcastReceived 'Print a, then b after 1 second' script
    And this script has a Print brick with 'a'
    And this script has a Wait 1 second brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcabc'

  Scenario: A BroadcastAndWait brick waits for a short and a long WhenBroadcastReceived script. The same
    BroadcastAndWait is triggered again before the long one finishes.

    Given 'test object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a Broadcast 'go' brick
    And this script has a Wait 3 seconds brick
    And this script has a Repeat end brick
    Given 'test object' has a WhenBroadcastReceived 'go' script
    And this script has a BroadcastAndWait 'Print a immediately and b after 5 seconds' brick
    And this script has a Print brick with 'c'
    Given 'test object' has a WhenBroadcastReceived 'Print a immediately and b after 5 seconds' script
    And this script has a Print brick with 'a'
    Given 'test object' has a WhenBroadcastReceived 'Print a immediately and b after 5 seconds' script
    And this script has a Wait 5 seconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aabc'

  Scenario: A BroadcastAndWait brick sends a message and a different Object contains the corresponding
    WhenBroadcastReceived script.

    Given 'test object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a Print brick with 'a'
    And this script has a BroadcastAndWait 'Print b immediately and c after 3 seconds' brick
    And this script has a Print brick with 'd'
    And this script has a Repeat end brick
    Given this program has an Object '2nd test object'
    Given '2nd test object' has a WhenBroadcastReceived 'Print b immediately and c after 3 seconds' script
    And this script has a Print brick with 'b'
    And this script has a Wait 3 seconds brick
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcdabcd'

  Scenario: A BroadcastAndWait brick waits for two WhenBroadcastReceived scripts to finish.

    Given 'test object' has a Start script
    And this script has a Print brick with 'a'
    And this script has a BroadcastAndWait 'Print b and c from two different scripts' brick
    And this script has a Print brick with 'd'
    Given 'test object' has a WhenBroadcastReceived 'Print b and c from two different scripts' script
    And this script has a Print brick with 'b'
    Given 'test object' has a WhenBroadcastReceived 'Print b and c from two different scripts' script
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcd'

  Scenario: A BroadcastAndWait brick waits for two WhenBroadcastReceived scripts from two different objects to finish.

    Given 'test object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a Print brick with 'a'
    And this script has a BroadcastAndWait 'Print b and c from two different objects' brick
    And this script has a Print brick with 'd'
    And this script has a Repeat end brick
    Given this program has an Object '2nd test object'
    Given '2nd test object' has a WhenBroadcastReceived 'Print b and c from two different objects' script
    And this script has a Print brick with 'b'
    Given this program has an Object '3rd test object'
    Given '3rd test object' has a WhenBroadcastReceived 'Print b and c from two different objects' script
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcdabcd'

  Scenario: A Broadcast is sent after a BroadcastAndWait has finished.

    Given 'test object' has a Start script
    And this script has a Print brick with 'a'
    And this script has a BroadcastAndWait 'Print c' brick
    And this script has a Print brick with 'b'
    Given this program has an Object '2nd test object'
    Given '2nd test object' has a Start script
    And this script has a Wait 2 second brick
    And this script has a Print brick with 'd'
    And this script has a Broadcast 'Print c' brick
    Given this program has an Object '3rd test object'
    Given '3rd test object' has a WhenBroadcastReceived 'Print c' script
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'acbdc'

  Scenario: A BroadcastAndWait is sent after a Broadcast has finished.

    Given 'test object' has a Start script
    And this script has a Wait 2 second brick
    And this script has a Print brick with 'a'
    And this script has a BroadcastAndWait 'Print c' brick
    And this script has a Print brick with 'b'
    Given this program has an Object '2nd test object'
    Given '2nd test object' has a Start script
    And this script has a Print brick with 'd'
    And this script has a Broadcast 'Print c' brick
    Given this program has an Object '3rd test object'
    Given '3rd test object' has a WhenBroadcastReceived 'Print c' script
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'dcacb'
