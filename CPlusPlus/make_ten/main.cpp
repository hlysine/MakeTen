#include <iostream>
#include <vector>
#include <functional>
#include "lib/tinyexpr.h"

using namespace std;

vector<string> DIGITS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
vector<string> OPERATORS = {"+", "-", "*", "/"};

class Solution {
public:
    vector<string> numbers;
    vector<string> operators;
    pair<int, int> brackets;

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

    [[nodiscard]] double calculate() const {
        return te_interp(to_expr().c_str(), nullptr);
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
    for (const auto& op1: OPERATORS) {
        for (const auto& op2: OPERATORS) {
            for (const auto& op3: OPERATORS) {
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

    vector<vector<string>> impossible_nums;

    num_iter(solution, [&impossible_nums](Solution* sol1) {
        bool possible = for_each_num_shuffle(sol1, [](Solution* sol2) -> bool {
            return for_each_op(sol2, [](Solution* sol3) -> bool {
                return for_each_bracket(sol3, [](Solution* sol4) -> bool {
                    return sol4->calculate() == 10;
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
