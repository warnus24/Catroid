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
    I have a Program
    this program has an Object 'test object'

  Scenario: A BroadcastAndWait brick without a corresponding WhenBroadcastReceived script should *not* wait for
    anything.

    Object 'test object' has the following scripts
      when program started
        broadcast 'This message does not matter as there is no receiver script' and wait
        print 'a'
      when program started
        wait 0.1 seconds
        print 'b'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'ab'

  Scenario: A waiting BroadcastAndWait brick is unblocked when the broadcast message is sent again.

    Object 'test object' has the following scripts
      when program started
        broadcast 'Print a after 0.1 seconds, and then b after another 0.3 seconds' and wait
        print 'c'
      when program started
        wait 0.2 seconds
        broadcast 'Print a after 0.1 seconds, and then b after another 0.3 seconds'
      when I receive 'Print a after 0.1 seconds, and then b after another 0.3 seconds'
        wait 0.1 seconds
        print 'a'
        wait 0.3 seconds
        print 'b'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'acab'

  Scenario: A waiting BroadcastAndWait brick is unblocked via another BroadcastAndWait brick.

    Object 'test object' has the following scripts
      when program started
        broadcast 'Print b after 0.2 seconds' and wait
        print 'a'
      when program started
        wait 0.1 seconds
        broadcast 'Print b after 0.2 seconds' and wait
        print 'c'
      when I receive 'Print b after 0.2 seconds'
        wait 0.2 seconds
        print 'b'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'abc'

  Scenario: A waiting BroadcastAndWait brick is unblocked when the same broadcast message is sent again and there are
    two WhenBroadcastReceived scripts responding to the same message.

    Object 'test object' has the following scripts
      when program started
        broadcast 'Print b and c from different scripts' and wait
        print 'a'
      when I receive 'Print b and c from different scripts'
        wait 0.3 seconds
        print 'b'
      when I receive 'Print b and c from different scripts'
        wait 0.4 seconds
        print 'c'
      when program started
        wait 0.1 seconds
        broadcast 'Print b and c from different scripts'
        wait 0.2 seconds
        print 'd'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'adbc'

  Scenario: A BroadcastAndWait brick repeatedly triggers a WhenBroadcastReceived script which is restarted immediately.

    Object 'test object' has the following scripts
      when program started
        repeat 3 times
          broadcast 'Send the BroadcastAndWait message'
        end of loop
      when I receive 'Send the BroadcastAndWait message'
        broadcast 'Print a, then b after 0.1 seconds' and wait
        print 'c'
      when I receive 'Print a, then b after 0.1 seconds'
        print 'a'
        wait 0.1 seconds
        print 'b'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'aaabc'

  Scenario: A Broadcast brick repeatedly triggers a WhenBroadcastReceived script which is restarted immediately.

    Object 'test object' has the following scripts
      when program started
        repeat 3 times
          broadcast 'Send the second broadcast message'
          wait 0.2 seconds
          print 'c'
        end of loop
      when I receive 'Send the second broadcast message'
        broadcast 'Print b after 0.1 seconds, then d after another 0.3 seconds'
        print 'a'
      when I receive 'Print b after 0.1 seconds, then d after another 0.3 seconds'
        wait 0.1 seconds
        print 'b'
        wait 0.3 seconds
        print 'd'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'abcabcabcd'

  Scenario: A BroadcastAndWait brick is unblocked by a Broadcast brick. After that the BroadcastAndWait is triggered
    again.

    Object 'test object' has the following scripts
      when program started
        repeat 2 times
          broadcast 'Send the BroadcastAndWait message'
          wait 0.6 seconds
        end of loop
      when I receive 'Send the BroadcastAndWait message'
        broadcast 'Print a after 0.1 seconds, then b after another 0.2 seconds' and wait
        wait 0.2 seconds
        print 'd'
      when program started
        wait 0.3 seconds
        broadcast 'Print a after 0.1 seconds, then b after another 0.2 seconds'
        print 'c'
      when I receive 'Print a after 0.1 seconds, then b after another 0.2 seconds'
        wait 0.1 seconds
        print 'a'
        wait 0.4 seconds
        print 'b'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'acadabd'

  Scenario: A BroadcastAndWait brick waits for two WhenBroadcastReceived scripts to finish and is unblocked by a
    Broadcast brick. After that the BroadcastAndWait is triggered again.

    Object 'test object' has the following scripts
      when program started
        repeat 2 times
          broadcast 'Print a and b from two different scripts' and wait
          wait 0.3 seconds
          print 'c'
        end of loop
      when program started
        wait 0.1 seconds
        broadcast 'Print a and b from two different scripts'
      when I receive 'Print a and b from two different scripts'
        print 'a'
      when I receive 'Print a and b from two different scripts'
        wait 0.2 seconds
        print 'b'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'aabcabc'

  Scenario: Correct consecutive executions of one BroadcastAndWait brick.

    Object 'test object' has the following scripts
      when program started
        repeat 2 times
          broadcast 'Print a immediately' and wait
          print 'b'
        end of loop
      when I receive 'Print a immediately'
        print 'a'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'abab'

  Scenario: A BroadcastAndWait brick waits for a short and a long WhenBroadcastReceived script. The same
    BroadcastAndWait is triggered again before the long one finishes.

    Object 'test object' has the following scripts
      when program started
        repeat 2 times
          broadcast 'Send the BroadcastAndWait message'
          wait 0.2 seconds
        end of loop
      when I receive 'Send the BroadcastAndWait message'
        broadcast 'Print a immediately and b after 0.3 seconds' and wait
        print 'c'
      when I receive 'Print a immediately and b after 0.3 seconds'
        print 'a'
      when I receive 'Print a immediately and b after 0.3 seconds'
        wait 0.3 seconds
        print 'b'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'aabc'

  Scenario: A BroadcastAndWait brick sends a message and a different Object contains the corresponding
    WhenBroadcastReceived script.

    Object 'test object' has the following script
      when program started
        repeat 2 times
          print 'a'
          broadcast 'Print b immediately' and wait
          print 'c'
        end of loop

    this program has an Object '2nd test object'
    Object '2nd test object' has the following script
      when I receive 'Print b immediately'
        print 'b'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'abcabc'

  Scenario: A BroadcastAndWait brick waits for two WhenBroadcastReceived scripts to finish.

    Object 'test object' has the following scripts
      when program started
        print 'a'
        broadcast 'Print b and c from two different scripts' and wait
        print 'd'
      when I receive 'Print b and c from two different scripts'
        print 'b'
      when I receive 'Print b and c from two different scripts'
        wait 0.1 seconds
        print 'c'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'abcd'

  Scenario: A BroadcastAndWait brick waits for two WhenBroadcastReceived scripts from two different objects to finish.

    Object 'test object' has the following script
      when program started
        print 'a'
        broadcast 'Print b and c from two different objects' and wait
        print 'd'

    this program has an Object '2nd test object'
    Object '2nd test object' has the following script
      when I receive 'Print b and c from two different objects'
        print 'b'

    this program has an Object '3rd test object'
    Object '3rd test object' has the following script
      when I receive 'Print b and c from two different objects'
        wait 0.1 seconds
        print 'c'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'abcd'

  Scenario: A Broadcast is sent after a BroadcastAndWait has finished.

    Object 'test object' has the following script
      when program started
        print 'a'
        broadcast 'Print c immediately' and wait
        print 'b'

    this program has an Object '2nd test object'
    Object '2nd test object' has the following script
      when program started
        wait 0.3 seconds
        print 'd'
        broadcast 'Print c immediately'

    this program has an Object '3rd test object'
    Object '3rd test object' has the following script
      when I receive 'Print c immediately'
        print 'c'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'acbdc'

  Scenario: A BroadcastAndWait is sent after a Broadcast has finished.

    Object 'test object' has the following script
      when program started
        wait 0.2 seconds
        print 'a'
        broadcast 'Print c immediately' and wait
        print 'b'

    this program has an Object '2nd test object'
    Object '2nd test object' has the following script
      when program started
        print 'd'
        broadcast 'Print c immediately'

    this program has an Object '3rd test object'
    Object '3rd test object' has the following script
      when I receive 'Print c immediately'
        print 'c'

    I start the program
    I wait until the program has stopped
    I should see the printed output 'dcacb'
