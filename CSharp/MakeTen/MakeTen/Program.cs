using NCalc;

string[] DIGITS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
string[] OPERATORS = { "+", "-", "*", "/" };

bool forEachNumber(Solution baseSolution, Func<Solution, bool> callback)
{
    for (int i = 0; i < DIGITS.Length; i++)
    {
        for (int j = i; j < DIGITS.Length; j++)
        {
            for (int k = j; k < DIGITS.Length; k++)
            {
                for (int l = k; l < DIGITS.Length; l++)
                {
                    baseSolution.Numbers = new[] { DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l] };
                    if (callback(baseSolution)) return true;
                }
            }
        }
    }

    return false;
}

bool forEachDistinctNumber(Solution baseSolution, Func<Solution, bool> callback)
{
    for (int i = 0; i < DIGITS.Length; i++)
    {
        for (int j = i + 1; j < DIGITS.Length; j++)
        {
            for (int k = j + 1; k < DIGITS.Length; k++)
            {
                for (int l = k + 1; l < DIGITS.Length; l++)
                {
                    baseSolution.Numbers = new[] { DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l] };
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
    for (int i = 0; i < numbers.Count; i++)
    {
        var remaining1 = numbers.ToList();
        remaining1.RemoveAt(i);
        for (int j = 0; j < remaining1.Count; j++)
        {
            var remaining2 = remaining1.ToList();
            remaining2.RemoveAt(j);
            for (int k = 0; k < remaining2.Count; k++)
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
    foreach (var operator1 in OPERATORS)
    {
        foreach (var operator2 in OPERATORS)
        {
            foreach (var operator3 in OPERATORS)
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
    for (int i = 0; i < 4; i++)
    {
        for (int j = i + 1; j < 4; j++)
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

    var impossibleCombinations = new List<string[]>();

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

    impossibleCombinations.ForEach(numbers => Console.WriteLine(numbers.Aggregate((a, b) => a + " " + b)));

    Console.WriteLine($"Total: {impossibleCombinations.Count}");
}

search("distinct numbers", forEachDistinctNumber);
search("non-distinct numbers", forEachNumber);

struct Solution
{
    public string[] Numbers;
    public string[] Operators;
    public (int, int) Brackets;

    public string ToExpression()
    {
        var expression = "";
        for (int i = 0; i < Numbers.Length; i++)
        {
            if (Brackets.Item1 == i) expression += '(';
            expression += Numbers[i];
            if (Brackets.Item2 == i) expression += ')';
            if (i < Operators.Length) expression += Operators[i];
        }

        return expression;
    }

    public double Calculate()
    {
        return Convert.ToDouble(new Expression(ToExpression()).Evaluate());
    }
}