//Package hamming Calculates the Hamming difference between two DNA strands.
package hamming

import "errors"

const testVersion = 6

//Distance returns the Hamming difference between two DNA strands.
func Distance(a, b string) (diff int, err error) {
	if len(a) != len(b) {
		err = errors.New("invalid argument")
		return
	}
	for i := len(a) - 1; i >= 0; i-- {
		if a[i] != b[i] {
			diff++
		}
	}
	return diff, err
}
