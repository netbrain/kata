//Package gigasecond takes care of the gigasecond perspective on time
package gigasecond

// import path for the time package from the standard library
import (
	"math"
	"time"
)

const testVersion = 4

//AddGigasecond adds 10^9 (gigasecond) seconds to a time.Time
func AddGigasecond(t time.Time) time.Time {
	gigaSecond := time.Second * time.Duration(math.Pow(10, 9))
	return t.Add(gigaSecond)
}
