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

  Scenario: A BroadcastWait with no When script
    Given 'Object' has a Start script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with 'a'
    Given 'Object' has a Start script
    And this script has a Wait 200 milliseconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'ab'

  Scenario: A waiting BroadcastWait brick is unblocked when the same broadcast message is present
    Given 'Object' has a Start script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with '-S1-'
    Given 'Object' has a Start script
    And this script has a Wait 600 milliseconds brick
    And this script has a Broadcast 'hello' brick
    Given 'Object' has a When 'hello' script
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with '-W1-'
    And this script has a Wait 500 milliseconds brick
    And this script has a Print brick with '-W2-'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output '-W1--S1--W1--W2-'

  Scenario: A waiting BroadcastWait brick is unblocked via another BroadcastWait brick
    Given 'Object' has a Start script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Wait 1 second brick
    And this script has a Print brick with '-S1-'
    Given 'Object' has a Start script
    And this script has a Wait 2 seconds brick
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with '-S2-'
    Given 'Object' has a When 'hello' script
    And this script has a Wait 5 seconds brick
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

  Scenario: A BroadcastWait brick restarts When scripts
    Given 'Object' has a Start script
    And this script has a Repeat 5 times brick
    And this script has a Broadcast 'go' brick
    And this script has a Wait 2 second brick
    And this script has a Repeat end brick
    Given 'Object' has a When 'go' script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with 'c'
    Given 'Object' has a When 'hello' script
    And this script has a Print brick with 'a'
    And this script has a Wait 7 seconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aaaaabc'

  Scenario: A Broadcast brick restarts When scripts
    Given 'Object' has a Start script
    And this script has a Repeat 5 times brick
    And this script has a Broadcast 'go' brick
    And this script has a Wait 2 seconds brick
    And this script has a Print brick with 'c'
    And this script has a Wait 1 second brick
    And this script has a Repeat end brick
    Given 'Object' has a When 'go' script
    And this script has a Broadcast 'hello' brick
    And this script has a Print brick with 'a'
    Given 'Object' has a When 'hello' script
    And this script has a Wait 500 milliseconds brick
    And this script has a Print brick with 'b'
    And this script has a Wait 7 seconds brick
    And this script has a Print brick with 'd'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcabcabcabcabcd'

  Scenario: A BroadcastWait brick is resumed by a Broadcast brick
    Given 'Object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a Broadcast 'go' brick
    And this script has a Wait 6 seconds brick
    And this script has a Repeat end brick
    Given 'Object' has a When 'go' script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Wait 2 seconds brick
    And this script has a Print brick with 'd'
    Given 'Object' has a Start script
    And this script has a Wait 3 seconds brick
    And this script has a Broadcast 'hello' brick
    And this script has a Print brick with 'c'
    Given 'Object' has a When 'hello' script
    And this script has a Wait 1 second brick
    And this script has a Print brick with 'a'
    And this script has a Wait 5 seconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'acadabd'

  Scenario: A BroadcastWait waits for two When scripts and gets interrupted by a Broadcast
    Given 'Object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a BroadcastWait 'hello' brick
    And this script has a Wait 6 seconds brick
    And this script has a Print brick with 'c'
    And this script has a Wait 1 second brick
    And this script has a Repeat end brick
    Given 'Object' has a Start script
    And this script has a Wait 2 seconds brick
    And this script has a Broadcast 'hello' brick
    Given 'Object' has a When 'hello' script
    And this script has a Print brick with 'a'
    Given 'Object' has a When 'hello' script
    And this script has a Wait 3 seconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aabcabc'

  Scenario: Correct consecutive executions of one BroadcastWait brick
    Given 'Object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with 'c'
    And this script has a Repeat end brick
    Given 'Object' has a When 'hello' script
    And this script has a Print brick with 'a'
    And this script has a Wait 1 second brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcabc'

  Scenario: A BroadcastWait brick waits for a short and a long When script and then is executed again
    Given 'Object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a Broadcast 'go' brick
    And this script has a Wait 3 seconds brick
    And this script has a Repeat end brick
    Given 'Object' has a When 'go' script
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with 'c'
    Given 'Object' has a When 'hello' script
    And this script has a Print brick with 'a'
    Given 'Object' has a When 'hello' script
    And this script has a Wait 5 seconds brick
    And this script has a Print brick with 'b'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aabc'

  Scenario: A BroadcastWait brick sends a message and a different Object contains the corresponding When script
    Given 'Object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a Print brick with 'a'
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with 'd'
    And this script has a Repeat end brick
    Given this program has an Object 'Object2'
    Given 'Object2' has a When 'hello' script
    And this script has a Print brick with 'b'
    And this script has a Wait 3 seconds brick
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcdabcd'

  Scenario: A BroadcastWait brick waits for two When scripts
    Given 'Object' has a Start script
    And this script has a Print brick with 'a'
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with 'd'
    Given 'Object' has a When 'hello' script
    And this script has a Print brick with 'b'
    Given 'Object' has a When 'hello' script
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcd'

  Scenario: A Broadcast Wait brick waits for two when scripts from two different objects
    Given 'Object' has a Start script
    And this script has a Repeat 2 times brick
    And this script has a Print brick with 'a'
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with 'd'
    And this script has a Repeat end brick
    Given this program has an Object 'Object2'
    Given 'Object2' has a When 'hello' script
    And this script has a Print brick with 'b'
    Given this program has an Object 'Object3'
    Given 'Object3' has a When 'hello' script
    And this script has a Wait 300 milliseconds brick
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcdabcd'

  Scenario: A Broadcast is sent after a BroadcastWait has finished
    Given 'Object' has a Start script
    And this script has a Print brick with 'a'
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with 'b'
    Given this program has an Object 'Object2'
    Given 'Object2' has a Start script
    And this script has a Wait 2 second brick
    And this script has a Print brick with 'd'
    And this script has a Broadcast 'hello' brick
    Given this program has an Object 'Object3'
    Given 'Object3' has a When 'hello' script
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'acbdc'

  Scenario: A BroadcastWait is sent after a Broadcast has finished
    Given 'Object' has a Start script
    And this script has a Wait 2 second brick
    And this script has a Print brick with 'a'
    And this script has a BroadcastWait 'hello' brick
    And this script has a Print brick with 'b'
    Given this program has an Object 'Object2'
    Given 'Object2' has a Start script
    And this script has a Print brick with 'd'
    And this script has a Broadcast 'hello' brick
    Given this program has an Object 'Object3'
    Given 'Object3' has a When 'hello' script
    And this script has a Print brick with 'c'
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'dcacb'
