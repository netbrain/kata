//Package clock handles clock functionality
package clock

import (
	"fmt"
)

const testVersion = 4

//Clock represents hour and minute
type Clock struct {
	hour, minute int
}

//New creates a new clock
func New(hour, minute int) Clock {

	hour += minute / 60
	hour %= 24
	minute %= 60

	if minute < 0 {
		minute += 60
		hour -= 1
	}

	if hour < 0 {
		hour += 24
	}

	c := Clock{
		hour:   hour,
		minute: minute,
	}
	return c
}

//String returns a string representation of the clock
func (c Clock) String() string {
	return fmt.Sprintf("%.2d:%.2d", c.hour, c.minute)
}

//Add adds minutes to the current clock
func (c Clock) Add(minutes int) Clock {
	return New(c.hour, c.minute+minutes)
}
