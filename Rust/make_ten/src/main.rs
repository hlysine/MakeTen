const DIGITS: &'static [char] = &['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
const SYMBOLS: &'static [char] = &['+', '-', '*', '/'];

#[derive(Clone, Copy, PartialEq, Eq)]
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

fn get_op(ch: char, in_bracket: bool) -> Operator {
    match ch {
        '+' => if in_bracket { Operator::BracketAdd } else { Operator::Add },
        '-' => if in_bracket { Operator::BracketSubtract } else { Operator::Subtract },
        '*' => if in_bracket { Operator::BracketMultiply } else { Operator::Multiply },
        '/' => if in_bracket { Operator::BracketDivide } else { Operator::Divide },
        _ => panic!("Unknown operator: {}", ch)
    }
}

fn eval_op(operands: &mut Vec<f64>, operators: &mut Vec<Operator>, idx: usize) {
    let operator = operators[idx];
    let operand1 = operands[idx];
    let operand2 = operands[idx + 1];

    match operator {
        Operator::Add | Operator::BracketAdd => operands[idx] = operand1 + operand2,
        Operator::Subtract | Operator::BracketSubtract => operands[idx] = operand1 - operand2,
        Operator::Multiply | Operator::BracketMultiply => operands[idx] = operand1 * operand2,
        Operator::Divide | Operator::BracketDivide => operands[idx] = operand1 / operand2,
    }

    operands.remove(idx + 1);
    operators.remove(idx);
}

fn eval(expr: &String) -> f64 {
    let mut in_bracket = false;
    let mut operands = Vec::new();
    let mut operators = Vec::new();

    for c in expr.chars() {
        if DIGITS.contains(&c) {
            operands.push(c.to_digit(10).unwrap() as f64);
        } else if SYMBOLS.contains(&c) {
            operators.push(get_op(c, in_bracket));
        } else if c == '(' {
            in_bracket = true;
        } else if c == ')' {
            in_bracket = false;
        }
    }

    while !operators.is_empty() {
        let mut idx = operators.iter()
            .position(|&o| o == Operator::BracketMultiply || o == Operator::BracketDivide);
        if idx == None {
            idx = operators.iter()
                .position(|&o| o == Operator::BracketAdd || o == Operator::BracketSubtract);
        }
        if idx == None {
            idx = operators.iter()
                .position(|&o| o == Operator::Multiply || o == Operator::Divide);
        }
        if idx == None {
            idx = operators.iter()
                .position(|&o| o == Operator::Add || o == Operator::Subtract);
        }

        eval_op(&mut operands, &mut operators, idx.expect("Valid expression"));
    }

    operands[0]
}

#[derive(Clone)]
struct Solution {
    numbers: Vec<char>,
    operators: Vec<char>,
    brackets: (usize, usize),
}

impl Solution {
    fn to_expr(&self) -> String {
        let mut expr = String::new();
        for i in 0..self.numbers.len() {
            if self.brackets.0 == i {
                expr.push('(');
            }
            expr.push(self.numbers[i]);
            if self.brackets.1 == i {
                expr.push(')');
            }
            if i < self.operators.len() {
                expr.push(self.operators[i]);
            }
        }
        expr
    }

    fn calculate(&self) -> f64 {
        eval(&self.to_expr())
    }
}

fn for_each_num(
    base_sol: &Solution,
    callback: &mut (impl (FnMut(&Solution) -> bool) + ?Sized),
) -> bool {
    for i in 0..DIGITS.len() {
        for j in i..DIGITS.len() {
            for k in j..DIGITS.len() {
                for l in k..DIGITS.len() {
                    let new_sol = Solution {
                        numbers: vec![
                            DIGITS[i],
                            DIGITS[j],
                            DIGITS[k],
                            DIGITS[l],
                        ],
                        ..base_sol.clone()
                    };
                    if callback(&new_sol) {
                        return true;
                    }
                }
            }
        }
    }

    false
}

fn for_each_dist_num(
    base_sol: &Solution,
    callback: &mut (impl (FnMut(&Solution) -> bool) + ?Sized),
) -> bool {
    for i in 0..DIGITS.len() {
        for j in (i + 1)..DIGITS.len() {
            for k in (j + 1)..DIGITS.len() {
                for l in (k + 1)..DIGITS.len() {
                    let new_sol = Solution {
                        numbers: vec![
                            DIGITS[i],
                            DIGITS[j],
                            DIGITS[k],
                            DIGITS[l],
                        ],
                        ..base_sol.clone()
                    };
                    if callback(&new_sol) {
                        return true;
                    }
                }
            }
        }
    }

    false
}

fn for_each_num_shuffle(base_sol: &Solution, callback: &mut impl FnMut(&Solution) -> bool) -> bool {
    let numbers = base_sol.numbers.clone();
    for i in 0..numbers.len() {
        let mut remaining1 = numbers.clone();
        remaining1.remove(i);

        for j in 0..remaining1.len() {
            let mut remaining2 = remaining1.clone();
            remaining2.remove(j);

            for k in 0..remaining2.len() {
                let mut remaining3 = remaining2.clone();
                remaining3.remove(k);

                let new_sol = Solution {
                    numbers: vec![
                        numbers[i].clone(),
                        remaining1[j].clone(),
                        remaining2[k].clone(),
                        remaining3[0].clone(),
                    ],
                    ..base_sol.clone()
                };
                if callback(&new_sol) {
                    return true;
                }
            }
        }
    }

    false
}

fn for_each_op(base_sol: &Solution, callback: &mut impl FnMut(&Solution) -> bool) -> bool {
    for op1 in SYMBOLS {
        for op2 in SYMBOLS {
            for op3 in SYMBOLS {
                let new_sol = Solution {
                    operators: vec![*op1, *op2, *op3],
                    ..base_sol.clone()
                };
                if callback(&new_sol) {
                    return true;
                }
            }
        }
    }

    false
}

fn for_each_bracket(base_sol: &Solution, callback: &mut impl FnMut(&Solution) -> bool) -> bool {
    for i in 0..4 {
        for j in (i + 1)..4 {
            let new_sol = Solution {
                brackets: (i, j),
                ..base_sol.clone()
            };
            if callback(&new_sol) {
                return true;
            }
        }
    }

    false
}

fn search(title: &str, num_iter: &dyn Fn(&Solution, &mut dyn FnMut(&Solution) -> bool) -> bool) {
    let solution = Solution {
        numbers: Vec::new(),
        operators: Vec::new(),
        brackets: (0, 0),
    };

    let mut impossible_nums: Vec<Vec<char>> = Vec::new();

    num_iter(&solution, &mut |sol1| {
        let possible = for_each_num_shuffle(sol1, &mut |sol2| {
            for_each_op(sol2, &mut |sol3| {
                for_each_bracket(sol3, &mut |sol4| sol4.calculate() == 10.0)
            })
        });

        if !possible {
            let mut sorted = sol1.numbers.clone();
            sorted.sort();
            impossible_nums.push(sorted);
        }

        false
    });

    println!("Impossible combinations for {}:", title);

    let length = impossible_nums.len();

    for nums in impossible_nums {
        println!("{:?}", nums);
    }

    println!("Total: {}", length);
}

fn main() {
    search("distinct numbers", &|sol, cb| for_each_dist_num(sol, cb));
    search("non-distinct numbers", &|sol, cb| for_each_num(sol, cb));
}
