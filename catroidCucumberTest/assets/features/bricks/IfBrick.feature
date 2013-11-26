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
Feature: If brick

  An If brick decides which path of execution to follow depending on a condition.

  Background:
    Given I have a Program
    And this program has an Object 'Object'

  Scenario: Follow 'true' path
    Given 'Object' has a Start script
    And this script has an If 'TRUE' brick
    And this script has a Print brick with 'if path'
    And this script has an Else brick
    And this script has a Print brick with 'else path'
    And this script has an End If brick
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'if path'

  Scenario: Follow 'false' path
    Given 'Object' has a Start script
    And this script has an If 'FALSE' brick
    And this script has a Print brick with 'if path'
    And this script has an Else brick
    And this script has a Print brick with 'else path'
    And this script has an End If brick
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output 'else path'

  Scenario Outline: If with numeric comparison
    Given 'Object' has a Start script
    And this script has an If '<condition>' brick
    And this script has a Print brick with 'if path'
    And this script has an Else brick
    And this script has a Print brick with 'else path'
    And this script has an End If brick
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output '<path>'

    Examples:
      | condition | path      |
      | 2.0 > 1.9 | if path   |
      | 2 < 1.0   | else path |
      | 2.0 = 2   | if path   |
      | 2 = 1     | else path |

  Scenario Outline: If with user variable
    Given 'Object' has a Start script
    And 'Object' has an user variable '<variable>' with '<value>'
    And this script has an If user variable '<condition>' brick
    And this script has a Print brick with 'if path'
    And this script has an Else brick
    And this script has a Print brick with 'else path'
    And this script has an End If brick
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output '<path>'

    Examples:
      | variable     | value | condition          | path      |
      | myVariable   | 5.3   | myVariable > 5.3   | else path |
      | yourVariable | 2     | yourVariable = 2.0 | if path   |
      | ourVariable  | 4.3   | ourVariable < 1    | else path |

  Scenario Outline: Nested if block
    Given 'Object' has a Start script
    And this script has an If '<outer condition>' brick
    And this script has a Print brick with 'outer if path-'
    And this script has an If '<inner condition 1>' brick
    And this script has a Print brick with 'inner if path 1'
    And this script has an Else brick
    And this script has a Print brick with 'inner else path 1'
    And this script has an End If brick
    And this script has an Else brick
    And this script has a Print brick with 'outer else path-'
    And this script has an If '<inner condition 2>' brick
    And this script has a Print brick with 'inner if path 2'
    And this script has an Else brick
    And this script has a Print brick with 'inner else path 2'
    And this script has an End If brick
    And this script has an End If brick
    When I start the program
    And I wait until the program has stopped
    Then I should see the printed output '<path>'

    Examples:
      | outer condition | inner condition 1 | inner condition 2 | path                              |
      | TRUE            | TRUE              | FALSE             | outer if path-inner if path 1     |
      | TRUE            | FALSE             | FALSE             | outer if path-inner else path 1   |
      | FALSE           | FALSE             | TRUE              | outer else path-inner if path 2   |
      | FALSE           | FALSE             | FALSE             | outer else path-inner else path 2 |
