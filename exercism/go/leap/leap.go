//Package leap handles the leap year problem
package leap

const testVersion = 3

//IsLeapYear determines if a given year is a leap year
func IsLeapYear(year int) bool {
	return year%4 == 0 && year%100 != 0 || year%400 == 0
}
