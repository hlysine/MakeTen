const DIGITS = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
const OPERATORS = ['+', '-', '*', '/'];

interface Solution {
  numbers: string[];
  operators: string[];
  brackets: [number, number];
}

type IteratorBody = (solution: Solution) => boolean;

function toExpression(solution: Solution): string {
  let expression = '';
  for (let i = 0; i < solution.numbers.length; i++) {
    if (solution.brackets[0] === i) expression += '(';
    expression += solution.numbers[i];
    if (solution.brackets[1] === i) expression += ')';
    if (i < solution.operators.length) expression += solution.operators[i];
  }
  return expression;
}

function calculate(solution: Solution): number {
  // eslint-disable-next-line no-eval
  return eval(toExpression(solution));
}

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
  const solution: Solution = {
    numbers: [],
    operators: [],
    brackets: [0, 0],
  };

  const impossibleCombinations: string[][] = [];

  numberIterator(solution, solution1 => {
    const isPossible = forEachNumberShuffle(solution1, solution2 => {
      return forEachOperator(solution2, solution3 => {
        return forEachBracket(solution3, solution4 => {
          const result = calculate(solution4);
          return result === 10;
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
