from enum import Enum
from typing import Callable

DIGITS = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']
SYMBOLS = ['+', '-', '*', '/']


class Operator(Enum):
    Add = 0
    Subtract = 1
    Multiply = 2
    Divide = 3
    BracketAdd = 4
    BracketSubtract = 5
    BracketMultiply = 6
    BracketDivide = 7


def get_operator(char: str, in_bracket: bool) -> Operator:
    match char:
        case '+':
            return Operator.BracketAdd if in_bracket else Operator.Add
        case '-':
            return Operator.BracketSubtract if in_bracket else Operator.Subtract
        case '*':
            return Operator.BracketMultiply if in_bracket else Operator.Multiply
        case '/':
            return Operator.BracketDivide if in_bracket else Operator.Divide
        case _:
            raise ValueError(f'Unknown operator: {char}')


def eval_operation(operands: list[float], operators: list[Operator], index: int) -> None:
    operator = operators[index]
    operand1 = operands[index]
    operand2 = operands[index + 1]

    match operator:
        case Operator.Add | Operator.BracketAdd:
            operands[index] = operand1 + operand2
        case Operator.Subtract | Operator.BracketSubtract:
            operands[index] = operand1 - operand2
        case Operator.Multiply | Operator.BracketMultiply:
            operands[index] = operand1 * operand2
        case Operator.Divide | Operator.BracketDivide:
            operands[index] = operand1 / operand2 \
                if operand2 != 0 else float('nan')

    del operands[index + 1]
    del operators[index]


def evaluate(expression: str) -> float:
    in_bracket = False
    operands: list[float] = []
    operators: list[Operator] = []

    for char in expression:
        if char in DIGITS:
            operands.append(int(char))
        elif char in SYMBOLS:
            operators.append(get_operator(char, in_bracket))
        elif char == '(':
            in_bracket = True
        elif char == ')':
            in_bracket = False

    while len(operators) > 0:
        index = next(
            (i for i in range(len(operators)) if operators[i] in [
             Operator.BracketMultiply, Operator.BracketDivide]),
            None
        )
        if index is None:
            index = next(
                (i for i in range(len(operators)) if operators[i] in [
                 Operator.BracketAdd, Operator.BracketSubtract]),
                None
            )
        if index is None:
            index = next(
                (i for i in range(len(operators)) if operators[i] in [
                 Operator.Multiply, Operator.Divide]),
                None
            )
        if index is None:
            index = next(
                (i for i in range(len(operators)) if operators[i] in [
                 Operator.Add, Operator.Subtract]),
                None
            )

        if index is not None:
            eval_operation(operands, operators, index)
        else:
            raise ValueError('Invalid expression')

    return operands[0]


class Solution:

    def __init__(self):
        self.numbers: list[str] = []
        self.operators: list[str] = []
        self.brackets: tuple[int, int] = (0, 0)

    def to_expression(self) -> str:
        expression = ''

        for i in range(len(self.numbers)):
            if self.brackets[0] == i:
                expression += '('
            expression += self.numbers[i]
            if self.brackets[1] == i:
                expression += ')'
            if i < len(self.operators):
                expression += self.operators[i]

        return expression

    def calculate(self) -> float:
        return evaluate(self.to_expression())


IteratorBody = Callable[[Solution], bool]


def for_each_num(base_solution: Solution, callback: IteratorBody) -> bool:
    for i in range(len(DIGITS)):
        for j in range(i, len(DIGITS)):
            for k in range(j, len(DIGITS)):
                for m in range(k, len(DIGITS)):
                    base_solution.numbers = [
                        DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[m]]
                    if callback(base_solution):
                        return True

    return False


def for_each_distinct_num(base_solution: Solution, callback: IteratorBody) -> bool:
    for i in range(len(DIGITS)):
        for j in range(i + 1, len(DIGITS)):
            for k in range(j + 1, len(DIGITS)):
                for m in range(k + 1, len(DIGITS)):
                    base_solution.numbers = [
                        DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[m]]
                    if callback(base_solution):
                        return True

    return False


def for_each_num_shuffle(base_solution: Solution, callback: IteratorBody) -> bool:
    numbers = base_solution.numbers.copy()
    for i in range(len(numbers)):
        remaining1 = numbers.copy()
        del remaining1[i]

        for j in range(len(remaining1)):
            remaining2 = remaining1.copy()
            del remaining2[j]

            for k in range(len(remaining2)):
                remaining3 = remaining2.copy()
                del remaining3[k]

                base_solution.numbers = [
                    numbers[i],
                    remaining1[j],
                    remaining2[k],
                    remaining3[0]
                ]
                if callback(base_solution):
                    return True

    return False


def for_each_operator(base_solution: Solution, callback: IteratorBody) -> bool:
    for op1 in SYMBOLS:
        for op2 in SYMBOLS:
            for op3 in SYMBOLS:
                base_solution.operators = [op1, op2, op3]
                if callback(base_solution):
                    return True

    return False


def for_each_bracket(base_solution: Solution, callback: IteratorBody) -> bool:
    for i in range(4):
        for j in range(i + 1, 4):
            base_solution.brackets = (i, j)
            if callback(base_solution):
                return True

    return False


def search(title: str, num_iterator: Callable[[Solution, IteratorBody], bool]) -> None:
    solution = Solution()

    impossible_sets: list[list[str]] = []

    def iteration(solution1: Solution) -> bool:
        is_possible = for_each_num_shuffle(
            solution1,
            lambda solution2: for_each_operator(
                solution2,
                lambda solution3: for_each_bracket(
                    solution3,
                    lambda solution4: solution4.calculate() == 10
                )
            )
        )

        if not is_possible:
            impossible_sets.append(sorted(solution1.numbers))

        return False

    num_iterator(solution, iteration)

    print(f'Impossible combinations for {title}')

    for nums in impossible_sets:
        print(nums)

    print(f'Total: {len(impossible_sets)}')


def main():
    search('distinct numbers', for_each_distinct_num)
    search('non-distinct numbers', for_each_num)


if __name__ == '__main__':
    main()
