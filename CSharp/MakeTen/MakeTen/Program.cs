using System.Text;
using static Solution;

bool forEachNumber(Solution baseSolution, Func<Solution, bool> callback)
{
    for (var i = 0; i < Digits.Length; i++)
    {
        for (var j = i; j < Digits.Length; j++)
        {
            for (var k = j; k < Digits.Length; k++)
            {
                for (var l = k; l < Digits.Length; l++)
                {
                    baseSolution.Numbers = new[] { Digits[i], Digits[j], Digits[k], Digits[l] };
                    if (callback(baseSolution)) return true;
                }
            }
        }
    }

    return false;
}

bool forEachDistinctNumber(Solution baseSolution, Func<Solution, bool> callback)
{
    for (var i = 0; i < Digits.Length; i++)
    {
        for (var j = i + 1; j < Digits.Length; j++)
        {
            for (var k = j + 1; k < Digits.Length; k++)
            {
                for (var l = k + 1; l < Digits.Length; l++)
                {
                    baseSolution.Numbers = new[] { Digits[i], Digits[j], Digits[k], Digits[l] };
                    if (callback(baseSolution)) return true;
                }
            }
        }
    }

    return false;
}

bool forEachNumberShuffle(Solution baseSolution, Func<Solution, bool> callback)
{
    var numbers = baseSolution.Numbers.ToList();
    for (var i = 0; i < numbers.Count; i++)
    {
        var remaining1 = numbers.ToList();
        remaining1.RemoveAt(i);
        for (var j = 0; j < remaining1.Count; j++)
        {
            var remaining2 = remaining1.ToList();
            remaining2.RemoveAt(j);
            for (var k = 0; k < remaining2.Count; k++)
            {
                var remaining3 = remaining2.ToList();
                remaining3.RemoveAt(k);
                baseSolution.Numbers = new[]
                {
                    numbers[i],
                    remaining1[j],
                    remaining2[k],
                    remaining3[0],
                };
                if (callback(baseSolution)) return true;
            }
        }
    }

    return false;
}

bool forEachOperator(Solution baseSolution, Func<Solution, bool> callback)
{
    foreach (var operator1 in Symbols)
    {
        foreach (var operator2 in Symbols)
        {
            foreach (var operator3 in Symbols)
            {
                baseSolution.Operators = new[] { operator1, operator2, operator3 };
                if (callback(baseSolution)) return true;
            }
        }
    }

    return false;
}

bool forEachBracket(Solution baseSolution, Func<Solution, bool> callback)
{
    for (var i = 0; i < 4; i++)
    {
        for (var j = i + 1; j < 4; j++)
        {
            baseSolution.Brackets = (i, j);
            if (callback(baseSolution)) return true;
        }
    }

    return false;
}

void search(string title, Func<Solution, Func<Solution, bool>, bool> numberIterator)
{
    var solution = new Solution();

    var impossibleCombinations = new List<char[]>();

    numberIterator(solution, solution1 =>
    {
        var isPossible = forEachNumberShuffle(solution1, solution2 =>
        {
            return forEachOperator(solution2, solution3 =>
            {
                return forEachBracket(solution3, solution4 => Math.Abs(solution4.Calculate() - 10) < double.Epsilon);
            });
        });

        if (!isPossible)
        {
            impossibleCombinations.Add(solution1.Numbers.OrderBy(x => x).ToArray());
        }

        return false;
    });

    Console.WriteLine($"Impossible combinations for {title}:");

    foreach (var numbers in impossibleCombinations)
        Console.WriteLine(string.Join(" ", numbers));

    Console.WriteLine($"Total: {impossibleCombinations.Count}");
}

search("distinct numbers", forEachDistinctNumber);
search("non-distinct numbers", forEachNumber);

public enum Operator
{
    Add,
    Subtract,
    Multiply,
    Divide,
    BracketAdd,
    BracketSubtract,
    BracketMultiply,
    BracketDivide,
}

public struct Solution
{
    public char[] Numbers;
    public char[] Operators;
    public (int, int) Brackets;

    public static readonly char[] Digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public static readonly char[] Symbols = { '+', '-', '*', '/' };


    private static Operator GetOperator(char ch, bool inBracket)
    {
        return ch switch
        {
            '+' => inBracket ? Operator.BracketAdd : Operator.Add,
            '-' => inBracket ? Operator.BracketSubtract : Operator.Subtract,
            '*' => inBracket ? Operator.BracketMultiply : Operator.Multiply,
            '/' => inBracket ? Operator.BracketDivide : Operator.Divide,
            _ => throw new ArgumentException("Unknown operator: " + ch)
        };
    }

    private static void EvaluateOperation(IList<double> operands, IList<Operator> operators, int index)
    {
        var op = operators[index];
        var operand1 = operands[index];
        var operand2 = operands[index + 1];

        switch (op)
        {
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

        operands.RemoveAt(index + 1);
        operators.RemoveAt(index);
    }

    private static double Evaluate(string expression)
    {
        var inBracket = false;
        var operands = new List<double>();
        var operators = new List<Operator>();

        foreach (var c in expression)
        {
            if (Digits.Contains(c))
            {
                operands.Add(double.Parse(c.ToString()));
            }
            else if (Symbols.Contains(c))
            {
                operators.Add(GetOperator(c, inBracket));
            }
            else if (c == '(')
            {
                inBracket = true;
            }
            else if (c == ')')
            {
                inBracket = false;
            }
        }

        while (operators.Count > 0)
        {
            var index = operators.FindIndex(
                op =>
                    op is Operator.BracketMultiply or Operator.BracketDivide
            );
            if (index == -1)
                index = operators.FindIndex(
                    op =>
                        op is Operator.BracketAdd or Operator.BracketSubtract
                );
            if (index == -1)
                index = operators.FindIndex(
                    op =>
                        op is Operator.Multiply or Operator.Divide
                );
            if (index == -1)
                index = operators.FindIndex(
                    op =>
                        op is Operator.Add or Operator.Subtract
                );

            if (index != -1)
            {
                EvaluateOperation(operands, operators, index);
            }
            else
            {
                throw new ArgumentException("Invalid expression");
            }
        }

        return operands[0];
    }

    private string ToExpression()
    {
        var expression = new StringBuilder();
        for (var i = 0; i < Numbers.Length; i++)
        {
            if (Brackets.Item1 == i) expression.Append('(');
            expression.Append(Numbers[i]);
            if (Brackets.Item2 == i) expression.Append(')');
            if (i < Operators.Length) expression.Append(Operators[i]);
        }

        return expression.ToString();
    }

    public double Calculate()
    {
        return Evaluate(ToExpression());
    }
}