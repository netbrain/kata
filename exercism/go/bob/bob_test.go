package bob

import "testing"

const targetTestVersion = 3

func TestTestVersion(t *testing.T) {
	if testVersion != targetTestVersion {
		t.Errorf("Found testVersion = %v, want %v", testVersion, targetTestVersion)
	}
}

func TestHeyBob(t *testing.T) {
	for _, tt := range testCases {
		actual := Hey(tt.input)
		if actual != tt.expected {
			msg := `
	ALICE (%s): %q
	BOB: %s

	Expected Bob to respond: %s`
			t.Errorf(msg, tt.description, tt.input, actual, tt.expected)
		}
	}
}

func BenchmarkBob(b *testing.B) {
	for _, tt := range testCases {
		for i := 0; i < b.N; i++ {
			Hey(tt.input)
		}
	}
}
