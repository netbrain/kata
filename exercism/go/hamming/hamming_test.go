package hamming

import "testing"

const targetTestVersion = 6

func TestTestVersion(t *testing.T) {
	if testVersion != targetTestVersion {
		t.Fatalf("Found testVersion = %v, want %v.", testVersion, targetTestVersion)
	}
}

func TestHamming(t *testing.T) {
	for _, tc := range testCases {
		got, err := Distance(tc.s1, tc.s2)
		if tc.want < 0 {
			// check if err is of error type
			var _ error = err

			// we expect error
			if err == nil {
				t.Errorf("Distance(%q, %q). error is nil.",
					tc.s1, tc.s2)
			}
		} else {
			if got != tc.want {
				t.Errorf("Distance(%q, %q) = %d, want %d.",
					tc.s1, tc.s2, got, tc.want)
			}

			// we do not expect error
			if err != nil {
				t.Fatalf("Distance(%q, %q) returned error: %v when expecting none.",
					tc.s1, tc.s2, err)
			}
		}
	}
}

func BenchmarkHamming(b *testing.B) {
	// bench combined time to run through all test cases
	for i := 0; i < b.N; i++ {
		for _, tc := range testCases {
			Distance(tc.s1, tc.s2)
		}
	}
}
