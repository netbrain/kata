package pangram

import (
	"strings"
)

const testVersion = 2

func IsPangram(in string) bool {
	for _, r := range []rune{
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	} {
		if !strings.ContainsRune(in, r) {
			return false
		}
	}
	return true
}
