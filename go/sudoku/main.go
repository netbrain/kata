package main

import (
	"fmt"
	"time"
)

const (
	nineFactorial = 9 * 8 * 7 * 6 * 5 * 4 * 3 * 2 * 1
)

var (
	ErrOutOfNumbers = fmt.Errorf("Out of numbers")
)

type board [9][9]int

type cellValues struct {
	row     int
	col     int
	numbers []int
}

func main() {

	b := board{}

	fmt.Println("Solving the following sudoku board")
	fmt.Println(b)
	fmt.Println("--------------------------------------")
	t := time.Now()
	b.Solve()
	elapsed := time.Now().Sub(t)
	fmt.Printf("%s\n was solved in %s\n",b,elapsed)
}

func (b board) String() string {
	var s string
	for row := range b {
		if row == 0{
			s += "┏━━━┳━━━┳━━━┳━━━┳━━━┳━━━┳━━━┳━━━┳━━━┓"
		}else{
			s += "┣━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━╋━━━┫"
		}
		s += "\n"
		for col, cellValue := range b[row] {
			if col == 0 {
				s += "┃"
			}
			if(cellValue == 0){
				s += " - ┃"
			}else{
				s += fmt.Sprintf(" %d ┃", cellValue)
			}
		}
		s += "\n"
		if row == 8{
			s += "┗━━━┻━━━┻━━━┻━━━┻━━━┻━━━┻━━━┻━━━┻━━━┛"
		}
	}
	return s
}

func (b *board) isHorizontalRowValid(i int) bool {
	if i > 8 || i < 0 {
		return false
	}
	row := b[i]
	sum := 1
	for _, number := range row {
		sum *= number
	}
	return sum == nineFactorial
}

func (b *board) isVerticalRowValid(i int) bool {
	if i > 8 || i < 0 {
		return false
	}
	sum := 1
	for _, row := range b {
		sum *= row[i]
	}
	return sum == nineFactorial

}

func (b *board) isStackValid(i int) bool {
	if i > 8 || i < 0 {
		return false
	}
	sum := 1
	for x := 0; x < 9; x++ {
		row := i%3*3 + x/3
		col := (i*3 + x%3) % 9
		sum *= b[row][col]
	}
	return sum == nineFactorial
}

func (b *board) isValid() error {
	for i := 0; i < 9; i++ {
		if !b.isHorizontalRowValid(i) {
			return fmt.Errorf("Board failed validation at horizontal row: %d", i)
		}
		if !b.isVerticalRowValid(i) {
			return fmt.Errorf("Board failed validation at vertical row: %d", i)
		}
		if !b.isStackValid(i) {
			return fmt.Errorf("Board failed validation at stack: %d", i)
		}
	}
	return nil
}

func (b board) solveStep() (*board, error) {
	values := b.findCellWithFewestPossibilities()
	if len(values) == 0 {
		return &b, b.isValid()
	} else {
		value := values[0]
		if len(value.numbers) == 0 {
			return &b, ErrOutOfNumbers
		}
		b[value.row][value.col] = value.numbers[0]
	}
	return &b, nil
}

func (b *board) findCellWithFewestPossibilities() []*cellValues {
	smallest := 10
	values := make([]*cellValues, 0, 9)
	for row := range b {
		for col, value := range b[row] {
			if value != 0 {
				continue
			}
			validNumbers := b.getValidNumbersForPosition(row, col)
			if len(validNumbers) < smallest {
				smallest = len(validNumbers)
				values = values[:1]
				values[0] = &cellValues{
					row:     row,
					col:     col,
					numbers: validNumbers,
				}
			} else if len(validNumbers) == smallest {
				values = append(values, &cellValues{
					row:     row,
					col:     col,
					numbers: validNumbers,
				})
			}
		}
	}
	return values
}

func (b *board) Solve() {
	steps := make([]*board, 0)
	initialBoard := b
	for {
		if b.isValid() == nil {
			break
		}
		bStep, err := b.solveStep()
		if err == ErrOutOfNumbers {
			lastIndex := len(steps) - 1
			if lastIndex == -1 {
				fmt.Println("Cant backtrack anymore, invalid board?")
				return
			}
			bStep = steps[lastIndex]
			steps = steps[:lastIndex]
		} else {
			steps = append(steps, bStep)
		}

		b = bStep
	}

	for row, _ := range initialBoard {
		for col, _ := range initialBoard[row] {
			initialBoard[row][col] = b[row][col]
		}
	}

}

func (b *board) getValidNumbersForHorizontalRow(i int) []int {
	n := []int{1, 2, 3, 4, 5, 6, 7, 8, 9}

	for _, col := range b[i] {
		if col != 0 {
			n[col-1] = 0
		}
	}

	validNumbers := n[:0]
	for _, col := range n {
		if col != 0 {
			validNumbers = append(validNumbers, col)
		}
	}

	return validNumbers

}

//TODO not very DRY, duplicate code with above function
func (b *board) getValidNumbersForVerticalRow(i int) []int {
	n := []int{1, 2, 3, 4, 5, 6, 7, 8, 9}

	for _, row := range b {
		if row[i] != 0 {
			n[row[i]-1] = 0
		}
	}

	validNumbers := n[:0]
	for _, num := range n {
		if num != 0 {
			validNumbers = append(validNumbers, num)
		}
	}

	return validNumbers
}

func (b *board) getValidNumbersForStack(i int) []int {
	n := []int{1, 2, 3, 4, 5, 6, 7, 8, 9}
	for x := 0; x < 9; x++ {
		row := i/3*3 + x/3
		col := (i*3 + x%3) % 9
		if b[row][col] != 0 {
			n[b[row][col]-1] = 0
		}
	}

	validNumbers := n[:0]
	for _, num := range n {
		if num != 0 {
			validNumbers = append(validNumbers, num)
		}
	}

	return validNumbers
}

func (b *board) getValidNumbersForPosition(row, col int) []int {
	set := make(map[int]int)
	incrementor := func(numbers []int) {
		for _, num := range numbers {
			set[num]++
		}
	}
	incrementor(b.getValidNumbersForHorizontalRow(row))
	incrementor(b.getValidNumbersForVerticalRow(col))
	incrementor(b.getValidNumbersForStack(col/3 + row/3*3))

	filteredNumbers := make([]int, 0, len(set))
	for n, times := range set {
		if times == 3 {
			filteredNumbers = append(filteredNumbers, n)
		}
	}

	return filteredNumbers
}