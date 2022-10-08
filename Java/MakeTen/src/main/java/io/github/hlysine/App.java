package io.github.hlysine;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Hello world!
 */
public class App {
    static final String[] DIGITS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    static final String[] SYMBOLS = new String[]{"+", "-", "*", "/"};

    public static class Solution {
        public static final DoubleEvaluator evaluator = new DoubleEvaluator();
        public String[] Numbers;
        public String[] Operators;
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
            return evaluator.evaluate(ToExpression());
        }
    }

    static boolean forEachNumber(Solution baseSolution, Predicate<Solution> callback) {
        for (int i = 0; i < DIGITS.length; i++) {
            for (int j = i; j < DIGITS.length; j++) {
                for (int k = j; k < DIGITS.length; k++) {
                    for (int l = k; l < DIGITS.length; l++) {
                        baseSolution.Numbers = new String[]{DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l]};
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
                        baseSolution.Numbers = new String[]{DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l]};
                        if (callback.test(baseSolution)) return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    static boolean forEachNumberShuffle(Solution baseSolution, Predicate<Solution> callback) {
        List<String> numbers = List.of(baseSolution.Numbers);
        for (int i = 0; i < numbers.size(); i++) {
            List<String> remaining1 = new ArrayList<>(numbers);
            remaining1.remove(i);
            for (int j = 0; j < remaining1.size(); j++) {
                List<String> remaining2 = new ArrayList<>(remaining1);
                remaining2.remove(j);
                for (int k = 0; k < remaining2.size(); k++) {
                    List<String> remaining3 = new ArrayList<>(remaining2);
                    remaining3.remove(k);
                    baseSolution.Numbers = new String[]{numbers.get(i), remaining1.get(j), remaining2.get(k), remaining3.get(0)};
                    if (callback.test(baseSolution)) return true;
                }
            }
        }
        return false;
    }

    static boolean forEachOperator(Solution baseSolution, Predicate<Solution> callback) {
        for (String operator1 : SYMBOLS) {
            for (String operator2 : SYMBOLS) {
                for (String operator3 : SYMBOLS) {
                    baseSolution.Operators = new String[]{operator1, operator2, operator3};
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

        List<String[]> impossibleCombinations = new ArrayList<>();

        numberIterator.apply(solution, solution1 -> {
            boolean isPossible = forEachNumberShuffle(solution1, solution2 -> {
                return forEachOperator(solution2, solution3 -> {
                    return forEachBracket(solution3, solution4 -> {
                        return solution4.Calculate() == 10;
                    });
                });
            });

            if (!isPossible) {
                impossibleCombinations.add(Arrays.stream(solution1.Numbers).sorted().toArray(String[]::new));
            }

            return false;
        });

        System.out.println("Impossible combinations for " + title + ":");

        for (String[] strings : impossibleCombinations) {
            System.out.println(Arrays.toString(strings));
        }

        System.out.println("Total: " + impossibleCombinations.size());
    }


    public static void main(String[] args) {
        search("distinct numbers", App::forEachDistinctNumber);
        search("non-distinct numbers", App::forEachNumber);
    }
}
