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
Feature: Broadcast & Wait Blocking Behavior (like in Scratch)

  If a broadcast is sent while a Broadcast Wait brick is waiting for the same message, the
  responding When scripts should be restarted and the Broadcast Wait brick should stop waiting
  and immediately continue executing the rest of the script.

  Background:
    I have a Program
    'Object'

  Scenario: A BroadcastWait with no When script
    when program started
      broadcast and wait "hello"
      print "a"
    when program started
      wait (0.2) seconds
      print "b"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'ab'

  Scenario: A waiting BroadcastWait brick is unblocked when the same broadcast message is present
    when program started
      broadcast and wait "hello"
      print "-S1-"
    when program started
      wait (0.6) seconds
      broadcast "hello"
    when I receive "hello"
      wait (0.3) seconds
      print "-W1-"
      wait (0.5) seconds
      print "-W2-"
    I start the program
    I wait until the program has stopped
    I should see the printed output '-W1--S1--W1--W2-'

  Scenario: A waiting BroadcastWait brick is unblocked via another BroadcastWait brick
    when program started
      broadcast and wait "hello"
      wait (1.0) seconds
      print "-S1-"
    when program started
      wait (2.0) seconds
      broadcast and wait "hello"
      print "-S2-"
    when I receive "hello"
      wait (5.0) seconds
      print "-W1-"
    I start the program
    I wait until the program has stopped
    I should see the printed output '-S1--W1--S2-'

  Scenario: A waiting BroadcastWait brick is unblocked when the same broadcast message is present and there are two When scripts
    when program started
      broadcast and wait "hello"
      print "-S1-"
    when I receive "hello"
      wait (0.4) seconds
      print "-W1-"
    when I receive "hello"
      wait (0.5) seconds
      print "-W2-"
    when program started
      wait (0.1) seconds
      broadcast "hello"
      wait (0.1) seconds
      print "-S2-"
    I start the program
    I wait until the program has stopped
    I should see the printed output '-S1--S2--W1--W2-'

  Scenario: A BroadcastWait brick restarts When scripts
    when program started
      repeat (5)
        broadcast "go"
        wait (2.0) seconds
      end of loop
    when I receive "go"
      broadcast and wait "hello"
      print "c"
    when I receive "hello"
      print "a"
      wait (7.0) seconds
      print "b"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'aaaaabc'

  Scenario: A Broadcast brick restarts When scripts
    when program started
      repeat (5)
        broadcast "go"
        wait (2.0) seconds
        print "c"
        wait (1.0) seconds
      end of loop
    when I receive "go"
      broadcast "hello"
      print "a"
    when I receive "hello"
      wait (0.5) seconds
      print "b"
      wait (7.0) seconds
      print "d"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'abcabcabcabcabcd'

  Scenario: A BroadcastWait brick is resumed by a Broadcast brick
    when program started
      repeat (2)
        broadcast "go"
        wait (6.0) seconds
      end of loop
    when I receive "go"
      broadcast and wait "hello"
      wait (2.0) seconds
      print "d"
    when program started
      wait (3.0) seconds
      broadcast "hello"
      print "c"
    when I receive "hello"
      wait (1.0) seconds
      print "a"
      wait (5.0) seconds
      print "b"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'acadabd'

  Scenario: A BroadcastWait waits for two When scripts and gets interrupted by a Broadcast
    when program started
      repeat (2)
        broadcast and wait "hello"
        wait (6.0) seconds
        print "c"
        wait (1.0) seconds
      end of loop
    when program started
      wait (2.0) seconds
      broadcast "hello"
    when I receive "hello"
      print "a"
    when I receive "hello"
      wait (3.0) seconds
      print "b"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'aabcabc'

  Scenario: Correct consecutive executions of one BroadcastWait brick
    when program started
      repeat (2)
        broadcast and wait "hello"
        print "c"
      end of loop
    when I receive "hello"
      print "a"
      wait (1.0) seconds
      print "b"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'abcabc'

  Scenario: A BroadcastWait brick waits for a short and a long When script and then is executed again
    when program started
      repeat (2)
        broadcast "go"
        wait (3.0) seconds
      end of loop
    when I receive "go"
      broadcast and wait "hello"
      print "c"
    when I receive "hello"
      print "a"
    when I receive "hello"
      wait (5.0) seconds
      print "b"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'aabc'

  Scenario: A BroadcastWait brick sends a message and a different Object contains the corresponding When script
    when program started
      repeat (2)
        print "a"
        broadcast and wait "hello"
        print "d"
      end of loop

    when I receive "hello"
      print "b"
      wait (3.0) seconds
      print "c"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'abcdabcd'

  Scenario: A BroadcastWait brick waits for two When scripts
    when program started
      print "a"
      broadcast and wait "hello"
      print "d"
    when I receive "hello"
      print "b"
    when I receive "hello"
      wait (0.3) seconds
      print "c"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'abcd'

  Scenario: A Broadcast Wait brick waits for two when scripts from two different objects
    when program started
      repeat (2)
        print "a"
        broadcast and wait "hello"
        print "d"
      end of loop
    'Object2'
    when I receive "hello"
      print "b"
    'Object3'
    when I receive "hello"
      wait (0.3) seconds
      print "c"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'abcdabcd'

  Scenario: A Broadcast is sent after a BroadcastWait has finished
    when program started
      print "a"
      broadcast and wait "hello"
      print "b"
    'Object2'
    when program started
      wait (2.0) seconds
      print "d"
      broadcast "hello"
    'Object3'
    when I receive "hello"
      print "c"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'acbdc'

  Scenario: A BroadcastWait is sent after a Broadcast has finished
    when program started
      wait (2.0) seconds
      print "a"
      broadcast and wait "hello"
      print "b"
    'Object2'
    when program started
      print "d"
      broadcast "hello"
    'Object3'
    when I receive "hello"
      print "c"
    I start the program
    I wait until the program has stopped
    I should see the printed output 'dcacb'
