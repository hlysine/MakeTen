# Make Ten

Make Ten is a simple math puzzle requiring you to create an expression that evaluates to 10, given 4 digits and basic operators. The detailed rules are as follow:

- You are given 4 integer numbers, each one is in the range [0,9].
- Your goal is to create a math expression that evaluates to 10 using these 4 numbers.
- You may add  + - * /  in the spaces between numbers.
- You can use each operator multiple times or not use them at all.
- You may NOT use + - as unary operators (so you cannot make negative numbers by putting a - in front).
- You may NOT put two numbers together and treat them as one (so 1 0 is invalid and cannot be treated as 10).
- You may change the order of the numbers.
- You must use all numbers.
- You can also add at most one pair of ( ) in any place you like.
- The result must be exactly 10, no decimals or divide by zero allowed.

All solvers in this repository, implemented in various languages, have the same logic and list out all sets of digits that make this game impossible.

There should be 24 impossible sets of numbers if the numbers are distinct, and 166 sets if duplicate numbers are allowed in a set.
