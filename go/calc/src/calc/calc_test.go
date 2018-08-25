package calc

import (
	"fmt"
	"testing"
)

func TestCalculation(t *testing.T) {
	tests := map[string]float64{
		"1+1":                   2,
		"2+2":                   4,
		"1+1+1":                 3,
		"1+2+3+4":               10,
		"-1":                    -1,
		"1-1":                   0,
		"5-1-2":                 2,
		"1*2":                   2,
		"1*2*3":                 6,
		"1*1*1*1":               1,
		"10":                    10,
		"-10":                   -10,
		"1/0":                   0,
		"1/10":                  0.1,
		"0.1+0.1":               0.2,
		"1+1*2":                 3,
		"2*(1+1)":               4,
		"(((((-(-5))*(1+1)))))": 10,
		"1 + 1":                 2,
		"1 * 2":                 2,
		" - 200 / 100\t":        -2,
		"50-5*3+2\r\n":          37,
	}
	for in, out := range tests {
		t.Run(in, func(t *testing.T) {
			calc := &Calculator{}
			result, err := calc.Parse(in)
			if err != nil {
				t.Log(err)
				t.Fail()
			}
			if result != out {
				t.Log(fmt.Sprintf("%s should yield %f but got %f", in, out, result))
				t.Fail()
			}
		})
	}
}
