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
Feature: WhenBroadcastReceived Blocking Behavior (like in Scratch)

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

    Given Object 'test object' has the following scripts
      When program started
        Given broadcast 'This message does not matter as there is no receiver script' and wait
        And print 'a'
      When program started
        Given wait 0.1 seconds
        And print 'b'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'ab'

  Scenario: A waiting BroadcastAndWait brick is unblocked when the broadcast message is sent again.

    Given Object 'test object' has the following scripts
      When program started
        Given broadcast 'Print a after 0.1 seconds, and then b after another 0.3 seconds' and wait
        And print 'c'
      When program started
        Given wait 0.2 seconds
        And broadcast 'Print a after 0.1 seconds, and then b after another 0.3 seconds'
      When I receive 'Print a after 0.1 seconds, and then b after another 0.3 seconds'
        Given wait 0.1 seconds
        And print 'a'
        And wait 0.3 seconds
        And print 'b'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'acab'

  Scenario: A waiting BroadcastAndWait brick is unblocked via another BroadcastAndWait brick.

    Given Object 'test object' has the following scripts
      When program started
        Given broadcast 'Print b after 0.2 seconds' and wait
        And print 'a'
      When program started
        Given wait 0.1 seconds
        And broadcast 'Print b after 0.2 seconds' and wait
        And print 'c'
      When I receive 'Print b after 0.2 seconds'
        Given wait 0.2 seconds
        And print 'b'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abc'

  Scenario: A waiting BroadcastAndWait brick is unblocked when the same broadcast message is sent again and there are
    two WhenBroadcastReceived scripts responding to the same message.

    Given Object 'test object' has the following scripts
      When program started
        Given broadcast 'Print b and c from different scripts' and wait
        And print 'a'
      When I receive 'Print b and c from different scripts'
        Given wait 0.3 seconds
        And print 'b'
      When I receive 'Print b and c from different scripts'
        Given wait 0.4 seconds
        And print 'c'
      When program started
        Given wait 0.1 seconds
        And broadcast 'Print b and c from different scripts'
        And wait 0.2 seconds
        And print 'd'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'adbc'

  Scenario: A BroadcastAndWait brick repeatedly triggers a WhenBroadcastReceived script which is restarted immediately.

    Given Object 'test object' has the following scripts
      When program started
        Given repeat 3 times
          And broadcast 'Send the BroadcastAndWait message'
        And end of loop
      When I receive 'Send the BroadcastAndWait message'
        Given broadcast 'Print a, then b after 0.1 seconds' and wait
        And print 'c'
      When I receive 'Print a, then b after 0.1 seconds'
        Given print 'a'
        And wait 0.1 seconds
        And print 'b'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aaabc'

  Scenario: A Broadcast brick repeatedly triggers a WhenBroadcastReceived script which is restarted immediately.

    Given Object 'test object' has the following scripts
      When program started
        Given repeat 3 times
          And broadcast 'Send the second broadcast message'
          And wait 0.4 seconds
          And print 'c'
        And end of loop
      When I receive 'Send the second broadcast message'
        Given broadcast 'Print b after 0.1 seconds, then d after another 0.5 seconds'
        And print 'a'
      When I receive 'Print b after 0.1 seconds, then d after another 0.5 seconds'
        Given wait 0.1 seconds
        And print 'b'
        And wait 0.5 seconds
        And print 'd'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcabcabcd'

  Scenario: A BroadcastAndWait brick is unblocked by a Broadcast brick. After that the BroadcastAndWait is triggered
    again.

    Given Object 'test object' has the following scripts
      When program started
        Given repeat 2 times
          And broadcast 'Send the BroadcastAndWait message'
          And wait 0.6 seconds
        And end of loop
      When I receive 'Send the BroadcastAndWait message'
        Given broadcast 'Print a after 0.1 seconds, then b after another 0.2 seconds' and wait
        And wait 0.2 seconds
        And print 'd'
      When program started
        Given wait 0.3 seconds
        And broadcast 'Print a after 0.1 seconds, then b after another 0.2 seconds'
        And print 'c'
      When I receive 'Print a after 0.1 seconds, then b after another 0.2 seconds'
        Given wait 0.1 seconds
        And print 'a'
        And wait 0.4 seconds
        And print 'b'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'acadabd'

  Scenario: A BroadcastAndWait brick waits for two WhenBroadcastReceived scripts to finish and is unblocked by a
    Broadcast brick. After that the BroadcastAndWait is triggered again.

    Given Object 'test object' has the following scripts
      When program started
        Given repeat 2 times
          And broadcast 'Print a and b from two different scripts' and wait
          And wait 0.3 seconds
          And print 'c'
        And end of loop
      When program started
        Given wait 0.1 seconds
        And broadcast 'Print a and b from two different scripts'
      When I receive 'Print a and b from two different scripts'
        Given print 'a'
      When I receive 'Print a and b from two different scripts'
        Given wait 0.2 seconds
        And print 'b'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aabcabc'

  Scenario: Correct consecutive executions of one BroadcastAndWait brick.

    Given Object 'test object' has the following scripts
      When program started
        Given repeat 2 times
          And broadcast 'Print a immediately' and wait
          And print 'b'
        And end of loop
      When I receive 'Print a immediately'
        Given print 'a'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abab'

  Scenario: A BroadcastAndWait brick waits for a short and a long WhenBroadcastReceived script. The same
    BroadcastAndWait is triggered again before the long one finishes.

    Given Object 'test object' has the following scripts
      When program started
        Given repeat 2 times
          And broadcast 'Send the BroadcastAndWait message'
          And wait 0.2 seconds
        And end of loop
      When I receive 'Send the BroadcastAndWait message'
        Given broadcast 'Print a immediately and b after 0.3 seconds' and wait
        And print 'c'
      When I receive 'Print a immediately and b after 0.3 seconds'
        Given print 'a'
      When I receive 'Print a immediately and b after 0.3 seconds'
        Given wait 0.3 seconds
        And print 'b'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'aabc'

  Scenario: A BroadcastAndWait brick sends a message and a different Object contains the corresponding
    WhenBroadcastReceived script.

    Given Object 'test object' has the following script
      When program started
        Given repeat 2 times
          And print 'a'
          And broadcast 'Print b immediately' and wait
          And print 'c'
        And end of loop

    Given this program has an Object '2nd test object'
    Given Object '2nd test object' has the following script
      When I receive 'Print b immediately'
        And print 'b'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcabc'

  Scenario: A BroadcastAndWait brick waits for two WhenBroadcastReceived scripts to finish.

    Given Object 'test object' has the following scripts
      When program started
        Given print 'a'
        And broadcast 'Print b and c from two different scripts' and wait
        And print 'd'
      When I receive 'Print b and c from two different scripts'
        Given print 'b'
      When I receive 'Print b and c from two different scripts'
        Given wait 0.1 seconds
        And print 'c'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcd'

  Scenario: A BroadcastAndWait brick waits for two WhenBroadcastReceived scripts from two different objects to finish.

    Given Object 'test object' has the following script
      When program started
        Given print 'a'
        And broadcast 'Print b and c from two different objects' and wait
        And print 'd'

    Given this program has an Object '2nd test object'
    Given Object '2nd test object' has the following script
      When I receive 'Print b and c from two different objects'
        Given print 'b'

    Given this program has an Object '3rd test object'
    Given Object '3rd test object' has the following script
      When I receive 'Print b and c from two different objects'
        Given wait 0.1 seconds
        And print 'c'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'abcd'

  Scenario: A Broadcast is sent after a BroadcastAndWait has finished.

    Given Object 'test object' has the following script
      When program started
        Given print 'a'
        And broadcast 'Print c immediately' and wait
        And print 'b'

    Given this program has an Object '2nd test object'
    Given Object '2nd test object' has the following script
      When program started
        Given wait 0.3 seconds
        And print 'd'
        And broadcast 'Print c immediately'

    Given this program has an Object '3rd test object'
    Given Object '3rd test object' has the following script
      When I receive 'Print c immediately'
        Given print 'c'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'acbdc'

  Scenario: A BroadcastAndWait is sent after a Broadcast has finished.

    Given Object 'test object' has the following script
      When program started
        Given wait 0.2 seconds
        And print 'a'
        And broadcast 'Print c immediately' and wait
        And print 'b'

    Given this program has an Object '2nd test object'
    Given Object '2nd test object' has the following script
      When program started
        Given print 'd'
        And broadcast 'Print c immediately'

    Given this program has an Object '3rd test object'
    Given Object '3rd test object' has the following script
      When I receive 'Print c immediately'
        Given print 'c'

    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'dcacb'
