package io.github.hlysine;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class App {
    static final Character[] DIGITS = new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    static final Character[] SYMBOLS = new Character[]{'+', '-', '*', '/'};

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

    private static Operator getOperator(char ch, boolean inBracket) {
        switch (ch) {
            case '+':
                return inBracket ? Operator.BracketAdd : Operator.Add;
            case '-':
                return inBracket ? Operator.BracketSubtract : Operator.Subtract;
            case '*':
                return inBracket ? Operator.BracketMultiply : Operator.Multiply;
            case '/':
                return inBracket ? Operator.BracketDivide : Operator.Divide;
            default:
                throw new IllegalArgumentException("Unknown operator: " + ch);
        }
    }

    private static void evaluateOperation(List<Double> operands, List<Operator> operators, int index) {
        Operator operator = operators.get(index);
        double operand1 = operands.get(index);
        double operand2 = operands.get(index + 1);

        switch (operator) {
            case Add:
            case BracketAdd:
                operands.set(index, operand1 + operand2);
                break;
            case Subtract:
            case BracketSubtract:
                operands.set(index, operand1 - operand2);
                break;
            case Multiply:
            case BracketMultiply:
                operands.set(index, operand1 * operand2);
                break;
            case Divide:
            case BracketDivide:
                operands.set(index, operand1 / operand2);
                break;
        }

        operands.remove(index + 1);
        operators.remove(index);
    }

    private static double evaluate(String expression) {
        boolean inBracket = false;
        List<Double> operands = new ArrayList<>();
        List<Operator> operators = new ArrayList<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Arrays.stream(DIGITS).anyMatch(x -> x == c)) {
                operands.add(Double.parseDouble(String.valueOf(c)));
            } else if (Arrays.stream(SYMBOLS).anyMatch(x -> x == c)) {
                operators.add(getOperator(c, inBracket));
            } else if (c == '(') {
                inBracket = true;
            } else if (c == ')') {
                inBracket = false;
            }
        }

        while (operators.size() > 0) {
            OptionalInt index = IntStream.range(0, operators.size())
                    .filter(i -> operators.get(i) == Operator.BracketMultiply || operators.get(i) == Operator.BracketDivide)
                    .findFirst();
            if (index.isEmpty())
                index = IntStream.range(0, operators.size())
                        .filter(i -> operators.get(i) == Operator.BracketAdd || operators.get(i) == Operator.BracketSubtract)
                        .findFirst();
            if (index.isEmpty())
                index = IntStream.range(0, operators.size())
                        .filter(i -> operators.get(i) == Operator.Multiply || operators.get(i) == Operator.Divide)
                        .findFirst();
            if (index.isEmpty())
                index = IntStream.range(0, operators.size())
                        .filter(i -> operators.get(i) == Operator.Add || operators.get(i) == Operator.Subtract)
                        .findFirst();

            if (index.isPresent()) {
                evaluateOperation(operands, operators, index.getAsInt());
            } else {
                throw new IllegalArgumentException("Invalid expression");
            }
        }

        return operands.get(0);
    }

    public static class Solution {
        public Character[] Numbers;
        public Character[] Operators;
        public Pair<Integer, Integer> Brackets;

        public String ToExpression() {
            StringBuilder expression = new StringBuilder();
            for (int i = 0; i < Numbers.length; i++) {
                if (Brackets.getValue0() == i) {
                    expression.append("(");
                }
                expression.append(Numbers[i]);
                if (Brackets.getValue1() == i) {
                    expression.append(")");
                }
                if (i < Operators.length) {
                    expression.append(Operators[i]);
                }
            }
            return expression.toString();
        }

        public double Calculate() {
            return evaluate(ToExpression());
        }
    }

    static boolean forEachNumber(Solution baseSolution, Predicate<Solution> callback) {
        for (int i = 0; i < DIGITS.length; i++) {
            for (int j = i; j < DIGITS.length; j++) {
                for (int k = j; k < DIGITS.length; k++) {
                    for (int l = k; l < DIGITS.length; l++) {
                        baseSolution.Numbers = new Character[]{DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l]};
                        if (callback.test(baseSolution)) return true;
                    }
                }
            }
        }
        return false;
    }

    static boolean forEachDistinctNumber(Solution baseSolution, Predicate<Solution> callback) {
        for (int i = 0; i < DIGITS.length; i++) {
            for (int j = i + 1; j < DIGITS.length; j++) {
                for (int k = j + 1; k < DIGITS.length; k++) {
                    for (int l = k + 1; l < DIGITS.length; l++) {
                        baseSolution.Numbers = new Character[]{DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l]};
                        if (callback.test(baseSolution)) return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    static boolean forEachNumberShuffle(Solution baseSolution, Predicate<Solution> callback) {
        List<Character> numbers = List.of(baseSolution.Numbers);
        for (int i = 0; i < numbers.size(); i++) {
            List<Character> remaining1 = new ArrayList<>(numbers);
            remaining1.remove(i);
            for (int j = 0; j < remaining1.size(); j++) {
                List<Character> remaining2 = new ArrayList<>(remaining1);
                remaining2.remove(j);
                for (int k = 0; k < remaining2.size(); k++) {
                    List<Character> remaining3 = new ArrayList<>(remaining2);
                    remaining3.remove(k);
                    baseSolution.Numbers = new Character[]{numbers.get(i), remaining1.get(j), remaining2.get(k), remaining3.get(0)};
                    if (callback.test(baseSolution)) return true;
                }
            }
        }
        return false;
    }

    static boolean forEachOperator(Solution baseSolution, Predicate<Solution> callback) {
        for (char operator1 : SYMBOLS) {
            for (char operator2 : SYMBOLS) {
                for (char operator3 : SYMBOLS) {
                    baseSolution.Operators = new Character[]{operator1, operator2, operator3};
                    if (callback.test(baseSolution)) return true;
                }
            }
        }
        return false;
    }

    static boolean forEachBracket(Solution baseSolution, Predicate<Solution> callback) {
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                baseSolution.Brackets = new Pair<>(i, j);
                if (callback.test(baseSolution)) return true;
            }
        }
        return false;
    }

    static void search(String title, BiFunction<Solution, Predicate<Solution>, Boolean> numberIterator) {
        Solution solution = new Solution();

        List<Character[]> impossibleCombinations = new ArrayList<>();

        numberIterator.apply(solution, solution1 -> {
            boolean isPossible = forEachNumberShuffle(solution1, solution2 -> {
                return forEachOperator(solution2, solution3 -> {
                    return forEachBracket(solution3, solution4 -> {
                        return Math.abs(solution4.Calculate() - 10) < 0.0000001;
                    });
                });
            });

            if (!isPossible) {
                impossibleCombinations.add(Arrays.stream(solution1.Numbers).sorted().toArray(Character[]::new));
            }

            return false;
        });

        System.out.println("Impossible combinations for " + title + ":");

        for (Character[] strings : impossibleCombinations) {
            System.out.println(Arrays.toString(strings));
        }

        System.out.println("Total: " + impossibleCombinations.size());
    }


    public static void main(String[] args) {
        search("distinct numbers", App::forEachDistinctNumber);
        search("non-distinct numbers", App::forEachNumber);
    }
}
