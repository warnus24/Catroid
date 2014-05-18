Feature: Broadcast brick and Broadcast Wait brick performance

  Broadcast bricks should have an acceptable performance during execution, especially when projects have many objects, without unnecessary delays.

  Background:
    Given I have a Program
    And this program has an Object 'Object1'
    And this program has an Object 'Object2'
    And this program has an Object 'Object3'
    And this program has an Object 'Object4'
    And this program has an Object 'Object5'

  Scenario: No more than 100 iterations in 2 seconds

    Given 'Object1' has a Start script
    And this script has a BroadcastWait 'message1' brick
    And this script has a BroadcastWait 'message2' brick

    Given 'Object2' has a WhenIReceive 'message1' script
    And this script has a Forever brick
    And this script has a Glide 0.01 seconds to X: random(0,100) Y: random(0,100) brick
    And this script has a BroadcastWait 'message2' brick
    And this script has a EndOfLoop brick

    Given 'Object3' has a WhenIReceive 'message2' script
    And this script has a Forever brick
    And this script has a Glide 0.01 seconds to X: random(0,100) Y: random(0,100) brick
    And this script has a BroadcastWait 'message1' brick
    And this script has a EndOfLoop brick

    Given 'Object4' has a WhenIReceive 'message1' script
    And this script has a Forever brick
    And this script has a Glide 0.01 seconds to X: random(0,100) Y: random(0,100) brick
    And this script has a BroadcastWait 'message2' brick
    And this script has a EndOfLoop brick

    Given 'Object5' has a WhenIReceive 'message2' script
    And this script has a Forever brick
    And this script has a Glide 0.01 seconds to X: random(0,100) Y: random(0,100) brick
    And this script has a BroadcastWait 'message1' brick
    And this script has a EndOfLoop brick

    When I start the program
    Then the program should run forever
    And the four objects should continuously change positions randomly
    And the changes should not take more than 100 milliseconds