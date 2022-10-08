const DIGITS = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
const OPERATORS = ['+', '-', '*', '/'];

enum Operator {
  Add,
  Subtract,
  Multiply,
  Divide,
  BracketAdd,
  BracketSubtract,
  BracketMultiply,
  BracketDivide,
}

function getOperator(char: string, inBracket: boolean): Operator {
  switch (char) {
    case '+':
      return inBracket ? Operator.BracketAdd : Operator.Add;
    case '-':
      return inBracket ? Operator.BracketSubtract : Operator.Subtract;
    case '*':
      return inBracket ? Operator.BracketMultiply : Operator.Multiply;
    case '/':
      return inBracket ? Operator.BracketDivide : Operator.Divide;
    default:
      throw new Error(`Unknown operator: ${char}`);
  }
}

function evaluateOperation(
  operands: number[],
  operators: Operator[],
  index: number
): void {
  const operator = operators[index];
  const operand1 = operands[index];
  const operand2 = operands[index + 1];

  switch (operator) {
    case Operator.Add:
    case Operator.BracketAdd:
      operands[index] = operand1 + operand2;
      break;
    case Operator.Subtract:
    case Operator.BracketSubtract:
      operands[index] = operand1 - operand2;
      break;
    case Operator.Multiply:
    case Operator.BracketMultiply:
      operands[index] = operand1 * operand2;
      break;
    case Operator.Divide:
    case Operator.BracketDivide:
      operands[index] = operand1 / operand2;
      break;
  }

  operands.splice(index + 1, 1);
  operators.splice(index, 1);
}

function evaluate(expression: string): number {
  let inBracket = false;
  const operands: number[] = [];
  const operators: Operator[] = [];

  for (const char of expression) {
    if (DIGITS.includes(char)) {
      operands.push(Number(char));
    } else if (OPERATORS.includes(char)) {
      operators.push(getOperator(char, inBracket));
    } else if (char === '(') {
      inBracket = true;
    } else if (char === ')') {
      inBracket = false;
    }
  }

  while (operators.length > 0) {
    let index = operators.findIndex(
      operator =>
        operator === Operator.BracketMultiply ||
        operator === Operator.BracketDivide
    );
    if (index === -1)
      index = operators.findIndex(
        operator =>
          operator === Operator.BracketAdd ||
          operator === Operator.BracketSubtract
      );
    if (index === -1)
      index = operators.findIndex(
        operator =>
          operator === Operator.Multiply || operator === Operator.Divide
      );
    if (index === -1)
      index = operators.findIndex(
        operator => operator === Operator.Add || operator === Operator.Subtract
      );

    if (index !== -1) {
      evaluateOperation(operands, operators, index);
    } else {
      throw new Error('Invalid expression');
    }
  }

  return operands[0];
}

class Solution {
  numbers: string[];
  operators: string[];
  brackets: [number, number];

  constructor() {
    this.numbers = [];
    this.operators = [];
    this.brackets = [0, 0];
  }

  toExpression(): string {
    let expression = '';
    for (let i = 0; i < this.numbers.length; i++) {
      if (this.brackets[0] === i) expression += '(';
      expression += this.numbers[i];
      if (this.brackets[1] === i) expression += ')';
      if (i < this.operators.length) expression += this.operators[i];
    }
    return expression;
  }

  calculate(): number {
    return evaluate(this.toExpression());
  }
}

type IteratorBody = (solution: Solution) => boolean;

function forEachNumber(
  baseSolution: Solution,
  callback: IteratorBody
): boolean {
  for (let i = 0; i < DIGITS.length; i++) {
    for (let j = i; j < DIGITS.length; j++) {
      for (let k = j; k < DIGITS.length; k++) {
        for (let l = k; l < DIGITS.length; l++) {
          baseSolution.numbers = [DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l]];
          if (callback(baseSolution)) return true;
        }
      }
    }
  }

  return false;
}

function forEachDistinctNumber(
  baseSolution: Solution,
  callback: IteratorBody
): boolean {
  for (let i = 0; i < DIGITS.length; i++) {
    for (let j = i + 1; j < DIGITS.length; j++) {
      for (let k = j + 1; k < DIGITS.length; k++) {
        for (let l = k + 1; l < DIGITS.length; l++) {
          baseSolution.numbers = [DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l]];
          if (callback(baseSolution)) return true;
        }
      }
    }
  }

  return false;
}

function forEachNumberShuffle(
  baseSolution: Solution,
  callback: IteratorBody
): boolean {
  const numbers = [...baseSolution.numbers];
  for (let i = 0; i < numbers.length; i++) {
    const remaining1 = [...numbers];
    remaining1.splice(i, 1);
    for (let j = 0; j < remaining1.length; j++) {
      const remaining2 = [...remaining1];
      remaining2.splice(j, 1);
      for (let k = 0; k < remaining2.length; k++) {
        const remaining3 = [...remaining2];
        remaining3.splice(k, 1);
        baseSolution.numbers = [
          numbers[i],
          remaining1[j],
          remaining2[k],
          remaining3[0],
        ];
        if (callback(baseSolution)) return true;
      }
    }
  }

  return false;
}

function forEachOperator(
  baseSolution: Solution,
  callback: IteratorBody
): boolean {
  for (const operator1 of OPERATORS) {
    for (const operator2 of OPERATORS) {
      for (const operator3 of OPERATORS) {
        baseSolution.operators = [operator1, operator2, operator3];
        if (callback(baseSolution)) return true;
      }
    }
  }

  return false;
}

function forEachBracket(
  baseSolution: Solution,
  callback: IteratorBody
): boolean {
  for (let i = 0; i < 4; i++) {
    for (let j = i + 1; j < 4; j++) {
      baseSolution.brackets = [i, j];
      if (callback(baseSolution)) return true;
    }
  }

  return false;
}

function search(
  title: string,
  numberIterator: (solution: Solution, callback: IteratorBody) => boolean
): void {
  const solution = new Solution();

  const impossibleCombinations: string[][] = [];

  numberIterator(solution, solution1 => {
    const isPossible = forEachNumberShuffle(solution1, solution2 => {
      return forEachOperator(solution2, solution3 => {
        return forEachBracket(solution3, solution4 => {
          return solution4.calculate() === 10;
        });
      });
    });

    if (!isPossible) {
      impossibleCombinations.push(solution1.numbers.sort());
    }

    return false;
  });

  console.log(`Impossible combinations for ${title}:`);

  impossibleCombinations.forEach(numbers => console.log(numbers.join(' ')));

  console.log(`Total: ${impossibleCombinations.length}`);
}

search('distinct numbers', forEachDistinctNumber);
search('non-distinct numbers', forEachNumber);
