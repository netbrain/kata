package raindrops

// Source: exercism/x-common
// Commit: 9db5371 raindrops: Fix canonical-data.json formatting
// x-common version: 1.0.0

var tests = []struct {
	input    int
	expected string
}{
	{1, "1"},
	{3, "Pling"},
	{5, "Plang"},
	{7, "Plong"},
	{6, "Pling"},
	{8, "8"},
	{9, "Pling"},
	{10, "Plang"},
	{14, "Plong"},
	{15, "PlingPlang"},
	{21, "PlingPlong"},
	{25, "Plang"},
	{27, "Pling"},
	{35, "PlangPlong"},
	{49, "Plong"},
	{52, "52"},
	{105, "PlingPlangPlong"},
	{3125, "Plang"},
}

var factorsTests = []struct {
	input    int
	expected []int
}{
	{
		input:    28,
		expected: []int{1, 2, 4, 7, 14, 28},
	},
	{
		input:    30,
		expected: []int{1, 2, 3, 5, 6, 10, 15, 30},
	},
	{
		input:    34,
		expected: []int{1, 2, 17, 34},
	},
}
