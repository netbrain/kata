//Package acronym does all the things
package acronym

import (
	"bytes"
	"strings"
)

const testVersion = 3

//Abbreviate takes the first character of every word and concatenates them to an uppercase abbreviation
func Abbreviate(in string) (out string) {
	words := strings.Split(strings.Replace(in, "-", " ", -1), " ")
	abbr := make([]byte, len(words))
	for i := range words {

		abbr[i] = words[i][0]
	}

	return string(bytes.ToUpper(abbr))

}
