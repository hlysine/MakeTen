#include <iostream>
#include <vector>
#include <functional>

using namespace std;

const vector<char> DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
const vector<char> SYMBOLS = {'+', '-', '*', '/'};

enum Operator {
    Add,
    Subtract,
    Multiply,
    Divide,
    BracketAdd,
    BracketSubtract,
    BracketMultiply,
    BracketDivide,
};

Operator get_op(char ch, bool in_bracket) {
    switch (ch) {
        case '+':
            return in_bracket ? BracketAdd : Add;
        case '-':
            return in_bracket ? BracketSubtract : Subtract;
        case '*':
            return in_bracket ? BracketMultiply : Multiply;
        case '/':
            return in_bracket ? BracketDivide : Divide;
        default:
            auto msg = string("Unknown operator: ");
            msg.push_back(ch);
            throw invalid_argument(msg);
    }
}

void evaluate_op(vector<double>* operands, vector<Operator>* operators, long long index) {
    auto op = (*operators)[index];
    auto operand1 = (*operands)[index];
    auto operand2 = (*operands)[index + 1];

    switch (op) {
        case Add:
        case BracketAdd:
            (*operands)[index] = operand1 + operand2;
            break;
        case Subtract:
        case BracketSubtract:
            (*operands)[index] = operand1 - operand2;
            break;
        case Multiply:
        case BracketMultiply:
            (*operands)[index] = operand1 * operand2;
            break;
        case Divide:
        case BracketDivide:
            (*operands)[index] = operand1 / operand2;
            break;
    }

    operands->erase(operands->begin() + index + 1);
    operators->erase(operators->begin() + index);
}

double evaluate(const string& expr) {
    auto in_bracket = false;
    vector<double> operands;
    vector<Operator> operators;

    for (char ch: expr) {
        if (std::find(DIGITS.begin(), DIGITS.end(), ch) != DIGITS.end()) {
            operands.emplace_back(ch - '0');
        } else if (std::find(SYMBOLS.begin(), SYMBOLS.end(), ch) != SYMBOLS.end()) {
            operators.emplace_back(get_op(ch, in_bracket));
        } else if (ch == '(') {
            in_bracket = true;
        } else if (ch == ')') {
            in_bracket = false;
        }
    }

    while (!operators.empty()) {
        auto index = min(
                std::find(operators.begin(), operators.end(), BracketMultiply) - operators.begin(),
                std::find(operators.begin(), operators.end(), BracketDivide) - operators.begin()
        );
        if (index == operators.size())
            index = min(
                    std::find(operators.begin(), operators.end(), BracketAdd) - operators.begin(),
                    std::find(operators.begin(), operators.end(), BracketSubtract) - operators.begin()
            );
        if (index == operators.size())
            index = min(
                    std::find(operators.begin(), operators.end(), Multiply) - operators.begin(),
                    std::find(operators.begin(), operators.end(), Divide) - operators.begin()
            );
        if (index == operators.size())
            index = min(
                    std::find(operators.begin(), operators.end(), Add) - operators.begin(),
                    std::find(operators.begin(), operators.end(), Subtract) - operators.begin()
            );

        if (index != operators.size()) {
            evaluate_op(&operands, &operators, index);
        } else {
            throw invalid_argument("Invalid expression");
        }
    }

    return operands[0];
}

class Solution {
private:
    [[nodiscard]] string to_expr() const {
        string expr;
        for (int i = 0; i < numbers.size(); i++) {
            if (brackets.first == i) {
                expr += '(';
            }
            expr += numbers[i];
            if (brackets.second == i) {
                expr += ')';
            }
            if (i < operators.size()) {
                expr += operators[i];
            }
        }
        return expr;
    }

public:
    vector<char> numbers;
    vector<char> operators;
    pair<int, int> brackets;

    [[nodiscard]] double calculate() const {
        return evaluate(to_expr());
    }
};

bool for_each_num(Solution* base_sol, const function<bool(Solution*)>& callback) {
    for (int i = 0; i < DIGITS.size(); i++) {
        for (int j = i; j < DIGITS.size(); j++) {
            for (int k = j; k < DIGITS.size(); k++) {
                for (int l = k; l < DIGITS.size(); l++) {
                    base_sol->numbers = {DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l]};
                    if (callback(base_sol)) return true;
                }
            }
        }
    }
    return false;
}

bool for_each_dist_num(Solution* base_sol, const function<bool(Solution*)>& callback) {
    for (int i = 0; i < DIGITS.size(); i++) {
        for (int j = i + 1; j < DIGITS.size(); j++) {
            for (int k = j + 1; k < DIGITS.size(); k++) {
                for (int l = k + 1; l < DIGITS.size(); l++) {
                    base_sol->numbers = {DIGITS[i], DIGITS[j], DIGITS[k], DIGITS[l]};
                    if (callback(base_sol)) return true;
                }
            }
        }
    }
    return false;
}

bool for_each_num_shuffle(Solution* base_sol, const function<bool(Solution*)>& callback) {
    auto numbers = vector(base_sol->numbers);
    for (int i = 0; i < numbers.size(); i++) {
        auto remaining1 = vector(numbers);
        remaining1.erase(remaining1.begin() + i);

        for (int j = 0; j < remaining1.size(); j++) {
            auto remaining2 = vector(remaining1);
            remaining2.erase(remaining2.begin() + j);

            for (int k = 0; k < remaining2.size(); k++) {
                auto remaining3 = vector(remaining2);
                remaining3.erase(remaining3.begin() + k);

                base_sol->numbers = {numbers[i], remaining1[j], remaining2[k], remaining3[0]};
                if (callback(base_sol)) return true;
            }
        }
    }
    return false;
}

bool for_each_op(Solution* base_sol, const function<bool(Solution*)>& callback) {
    for (const auto& op1: SYMBOLS) {
        for (const auto& op2: SYMBOLS) {
            for (const auto& op3: SYMBOLS) {
                base_sol->operators = {op1, op2, op3};
                if (callback(base_sol)) return true;
            }
        }
    }
    return false;
}

bool for_each_bracket(Solution* base_sol, const function<bool(Solution*)>& callback) {
    for (int i = 0; i < 4; i++) {
        for (int j = i + 1; j < 4; j++) {
            base_sol->brackets = pair(i, j);
            if (callback(base_sol)) return true;
        }
    }
    return false;
}

void search(const string& title, bool (* num_iter)(Solution*, const function<bool(Solution*)>&)) {
    auto* solution = new Solution();

    vector<vector<char>> impossible_nums;

    num_iter(solution, [&impossible_nums](Solution* sol1) {
        bool possible = for_each_num_shuffle(sol1, [](Solution* sol2) -> bool {
            return for_each_op(sol2, [](Solution* sol3) -> bool {
                return for_each_bracket(sol3, [](Solution* sol4) -> bool {
                    return abs(sol4->calculate() - 10) < 0.000001;
                });
            });
        });

        if (!possible) {
            impossible_nums.emplace_back(sol1->numbers);
        }

        return false;
    });

    delete solution;

    cout << "Impossible combinations for " << title << endl;

    for (const auto& nums: impossible_nums) {
        for (const auto& num: nums) {
            cout << num << " ";
        }
        cout << endl;
    }

    cout << "Total: " << impossible_nums.size() << endl;
}

int main() {
    search("distinct numbers", for_each_dist_num);
    search("non-distinct numbers", for_each_num);
    return 0;
}
