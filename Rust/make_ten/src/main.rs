use fasteval::{ez_eval, EmptyNamespace};

const DIGITS: &'static [&'static str] = &["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"];
const SYMBOLS: &'static [&'static str] = &["+", "-", "*", "/"];

#[derive(Clone)]
struct Solution {
    numbers: Vec<String>,
    operators: Vec<String>,
    brackets: (usize, usize),
}

impl Solution {
    fn to_expr(&self) -> String {
        let mut expr = String::new();
        for i in 0..self.numbers.len() {
            if self.brackets.0 == i {
                expr.push('[');
            }
            expr.push_str(&self.numbers[i]);
            if self.brackets.1 == i {
                expr.push(']');
            }
            if i < self.operators.len() {
                expr.push_str(&self.operators[i]);
            }
        }
        expr
    }

    fn calculate(&self) -> f64 {
        let mut ns = EmptyNamespace;
        if let Ok(result) = ez_eval(&self.to_expr(), &mut ns) {
            result
        } else {
            panic!("Invalid expression: {}", self.to_expr());
        }
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
                            DIGITS[i].to_string(),
                            DIGITS[j].to_string(),
                            DIGITS[k].to_string(),
                            DIGITS[l].to_string(),
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
                            DIGITS[i].to_string(),
                            DIGITS[j].to_string(),
                            DIGITS[k].to_string(),
                            DIGITS[l].to_string(),
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
                    operators: vec![op1.to_string(), op2.to_string(), op3.to_string()],
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

    let mut impossible_nums: Vec<Vec<String>> = Vec::new();

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
