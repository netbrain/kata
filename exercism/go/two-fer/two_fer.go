//Package twofer provides a solution for the "One for you, one for me." problem.
package twofer

import "fmt"

//ShareWith returns the string "One for you, one for me" where "you" will be replaced by the input name if provided.
func ShareWith(name string) string {
	if name == "" {
		name = "you"
	}
	return fmt.Sprintf("One for %s, one for me.", name)
}
