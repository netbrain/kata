package main

import (
	"fmt"
	"reflect"
	"sort"
	"testing"
)

func TestString(t *testing.T) {
	var b board
	s := fmt.Sprintf("\n%s", b)
	expected := `
0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 0
`

	if s != expected {
		t.Fatalf("expected %s to equal %s", s, expected)
	}
}

func TestThatOnlyOneOccurenceOf1to9CanOccurInAHorizontalRow(t *testing.T) {
	b := board{{1, 2, 3, 4, 5, 6, 7, 8, 9}}
	if !b.isHorizontalRowValid(0) {
		t.Fatalf("Expected the row %v to be valid", b[0])
	}
}

func TestThatAHorizontalRowIsNotValidIfHasEmptyCells(t *testing.T) {
	b := board{{1, 2, 3, 4, 5, 6, 7, 8, 0}}
	if b.isHorizontalRowValid(0) {
		t.Fatalf("Expected the row %v to be invalid", b[0])
	}
}

func TestThatOnlyOneOccurenceOf1to9CanOccurInAVerticalRow(t *testing.T) {
	b := board{
		{1},
		{2},
		{3},
		{4},
		{5},
		{6},
		{7},
		{8},
		{9},
	}
	if !b.isVerticalRowValid(0) {
		t.Fatalf("Expected the vertical row %v to be valid", b[0])
	}
}

func TestThatAVerticalRowIsNotValidIfHasEmptyCells(t *testing.T) {
	b := board{
		{1},
		{2},
		{3},
		{4},
		{5},
		{6},
		{7},
		{8},
		{0},
	}
	if b.isVerticalRowValid(0) {
		t.Fatalf("Expected the vertical row %v to be invalid", b[0])
	}
}

func TestThatOnlyOneOccurenceOf1to9CanOccurInAStack(t *testing.T) {
	b := board{
		{1, 2, 3},
		{4, 5, 6},
		{7, 8, 9},
	}
	if !b.isStackValid(0) {
		t.Fatalf("Expected the vertical row %v to be valid", b[0])
	}
}

func TestThatAStackIsNotValidIfHasEmptyCells(t *testing.T) {
	b := board{
		{1, 2, 3},
		{4, 5, 6},
		{7, 8, 0},
	}
	if b.isStackValid(0) {
		t.Fatalf("Expected the vertical row %v to be invalid", b[0])
	}
}

func TestValidBoard(t *testing.T) {
	b := board{
		{1, 2, 3, 4, 5, 6, 7, 8, 9},
		{4, 5, 6, 7, 8, 9, 1, 2, 3},
		{7, 8, 9, 1, 2, 3, 4, 5, 6},
		{2, 3, 4, 5, 6, 7, 8, 9, 1},
		{5, 6, 7, 8, 9, 1, 2, 3, 4},
		{8, 9, 1, 2, 3, 4, 5, 6, 7},
		{3, 4, 5, 6, 7, 8, 9, 1, 2},
		{6, 7, 8, 9, 1, 2, 3, 4, 5},
		{9, 1, 2, 3, 4, 5, 6, 7, 8},
	}
	if err := b.isValid(); err != nil {
		t.Fatalf("Expected the board %s to be valid, instead got error %s", b, err)
	}
}

func TestInvalidBoards(t *testing.T) {
	boards := []board{
		{
			{1, 0, 3, 4, 5, 6, 7, 8, 9}, //missing values
			{4, 5, 6, 7, 8, 9, 1, 2, 3},
			{7, 8, 9, 1, 2, 3, 4, 5, 6},
			{2, 3, 4, 5, 6, 7, 8, 9, 1},
			{5, 6, 7, 0, 9, 1, 2, 3, 4},
			{8, 9, 1, 2, 3, 4, 5, 6, 7},
			{3, 4, 5, 6, 7, 8, 9, 1, 2},
			{6, 7, 8, 9, 1, 2, 3, 4, 5},
			{9, 1, 2, 3, 4, 0, 6, 7, 8},
		},
		{ /*empty board*/ },
		{
			{1, 2, 1, 4, 5, 6, 7, 8, 9}, //extra 1
			{4, 5, 6, 7, 8, 9, 1, 2, 3},
			{7, 8, 9, 1, 2, 3, 4, 5, 6},
			{2, 3, 4, 5, 6, 7, 8, 9, 1},
			{5, 6, 7, 8, 9, 1, 2, 3, 4},
			{8, 9, 1, 2, 3, 4, 5, 6, 7},
			{3, 4, 5, 6, 7, 8, 9, 1, 2},
			{6, 7, 8, 9, 1, 2, 3, 4, 5},
			{9, 1, 2, 3, 4, 5, 6, 7, 8},
		},
	}

	for i, b := range boards {
		if err := b.isValid(); err == nil {
			t.Fatalf("Expected the board (%d)\n %s to be invalid", i, b)
		}
	}
}

func TestGetValidNumbersForHorizontalRow(t *testing.T) {
	board := board{
		{1, 2, 3, 4, 5, 6, 7, 8}, //all except 9
	}

	validNumbers := board.getValidNumbersForHorizontalRow(0)
	if len(validNumbers) != 1 {
		t.Fatalf("Did not expect the length to be %d", len(validNumbers))
	}

	if validNumbers[0] != 9 {
		t.Fatalf("Expected 9 as valid number but got %d", validNumbers[0])
	}
}

func TestGetValidNumbersForVerticalRow(t *testing.T) {
	board := board{
		{1}, //all except 9
		{2},
		{3},
		{4},
		{5},
		{6},
		{7},
		{8},
	}

	validNumbers := board.getValidNumbersForVerticalRow(0)
	if len(validNumbers) != 1 {
		t.Fatalf("Did not expect the length to be %d", len(validNumbers))
	}

	if validNumbers[0] != 9 {
		t.Fatalf("Expected 9 as valid number but got %d", validNumbers[0])
	}
}

func TestGetValidNumbersForStack(t *testing.T) {
	board := board{
		{1, 2, 3}, //all except 9
		{4, 5, 6},
		{7, 8},
	}

	validNumbers := board.getValidNumbersForStack(0)
	if len(validNumbers) != 1 {
		t.Fatalf("Did not expect the length to be %d", len(validNumbers))
	}

	if validNumbers[0] != 9 {
		t.Fatalf("Expected 9 as valid number but got %d", validNumbers[0])
	}
}

func TestGetValidNumbersForPosition(t *testing.T) {
	var board board
	for x := range board {
		for y := range board[x] {
			if len(board.getValidNumbersForPosition(x, y)) != 9 {
				t.Fatalf("Expected row: %d, and col: %d to produce 9 valid numbers on board:\n%v", x, y, board)
			}
		}
	}
}

func TestGetValidNumberAtStack4(t *testing.T) {
	board := board{
		{1, 2, 5, 8, 7, 6, 9, 3, 4},
		{3, 7, 6, 1, 4, 5, 2, 8, 9},
		{4, 8, 9, 2, 6, 3, 5, 1, 7},
	}

	hnumbers := board.getValidNumbersForHorizontalRow(3)

	if len(hnumbers) != 9 {
		t.Fatalf("Expected horizontal row to have 9 valid numbers, but instead got: %d (%v)", len(hnumbers), hnumbers)
	}

	vnumbers := board.getValidNumbersForVerticalRow(0)

	if len(vnumbers) != 6 {
		t.Fatalf("Expected horizontal row to have 6 valid numbers, but instead got: %d (%v)", len(vnumbers), vnumbers)
	}

	snumbers := board.getValidNumbersForStack(3)

	if len(snumbers) != 9 {
		t.Fatalf("Expected horizontal row to have 9 valid numbers, but instead got: %d (%v)", len(snumbers), snumbers)
	}

	cnumbers := board.getValidNumbersForPosition(3, 0)

	if len(cnumbers) != 6 {
		t.Fatalf("Expected horizontal row to have 9 valid numbers, but instead got: %d (%v)", len(cnumbers), cnumbers)
	}

	sort.Ints(cnumbers)
	if !reflect.DeepEqual(cnumbers, []int{2, 5, 6, 7, 8, 9}) {
		t.Fatalf("Expected valid numbers to result in: 2,5,6,7,8,9 but instead got: %v", cnumbers)
	}
}

func TestEmptyBoard(t *testing.T) {
	var board board

	board.Solve()

	if err := board.isValid(); err != nil {
		t.Fatalf("Board isn't valid: \n%s\nerr:%s", board, err)
	}
}

func TestTheGuardianBoard(t *testing.T) {
	board := board{
		{0, 0, 0, 7, 3, 0, 0, 0, 0},
		{0, 0, 4, 0, 0, 0, 0, 0, 0},
		{0, 2, 6, 9, 0, 4, 0, 0, 0},
		{8, 0, 0, 0, 5, 0, 4, 0, 0},
		{0, 0, 0, 3, 7, 1, 0, 0, 6},
		{6, 0, 0, 0, 9, 0, 2, 0, 1},
		{1, 7, 0, 0, 0, 0, 5, 2, 0},
		{0, 0, 8, 0, 0, 0, 9, 0, 0},
		{0, 0, 5, 2, 0, 3, 0, 0, 0},
	}

	board.Solve()

	if err := board.isValid(); err != nil {
		t.Fatalf("Board isn't valid: \n%s\nerr:%s", board, err)
	}
}
