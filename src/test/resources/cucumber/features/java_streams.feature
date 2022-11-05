Feature: Java Streams Test
  Background:
    Given a stream of integers [4,1,13,90,44,2,0]

  Scenario: test java streams for Integer
    Then the minimum is 0
  Scenario: test java streams for 3 distinct numbers
    Then the sorted three distinct numbers are [0,1,2]
