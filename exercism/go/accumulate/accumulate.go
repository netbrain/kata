package accumulate

const testVersion = 1

func Accumulate(in []string, converter func(string) string) []string {
	for i := range in {
		in[i] = converter(in[i])
	}
	return in
}
