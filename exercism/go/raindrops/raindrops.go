//Package raindrops converts the factors of a number to a textual representation
package raindrops

import "strconv"

const testVersion = 3

//Convert converts a number to a string consisting of zero or more Pling, Plang and Plong's
func Convert(number int) (rain string) {
	if number%3 == 0 {
		rain = "Pling"
	}

	if number%5 == 0 {
		rain += "Plang"
	}

	if number%7 == 0 {
		rain += "Plong"
	}

	if rain == "" {
		rain = strconv.Itoa(number)
	}
	return
}
