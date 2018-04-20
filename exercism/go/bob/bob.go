package bob

import "strings"

const testVersion = 3

func Hey(aliceSays string) (bobAnswers string) {
	aliceSays = strings.TrimSpace(aliceSays)

	if strings.ToUpper(aliceSays) == aliceSays && strings.ContainsAny(strings.ToLower(aliceSays), "abcdefghijklmnopqrstuvwxyz") {
		return "Whoa, chill out!"
	}
	if aliceSays == "" {
		return "Fine. Be that way!"
	}

	if aliceSays[len(aliceSays)-1] == '?' {
		return "Sure."
	}
	return "Whatever."
}
