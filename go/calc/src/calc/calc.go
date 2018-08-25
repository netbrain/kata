package calc

import (
	"fmt"
	"strconv"
	"strings"
)

type stateFn func() stateFn

type TokenType int

const (
	ErrTokenType            TokenType = iota
	NumberTokenType
	AdditionTokenType
	SubtractionTokenType
	MultiplicationTokenType
	DivisionTokenType
	ParenStartTokenType
	ParenEndTokenType
)

type Token struct {
	data string
	pos  int
	typ  TokenType
}

func (t *Token) Priority() int  {
	switch t.typ {
	case AdditionTokenType:
		fallthrough
	case SubtractionTokenType:
		return 1
	case MultiplicationTokenType:
		fallthrough
	case DivisionTokenType:
		return 2
	}
	return 0
}

type Calculator struct {
	input  []byte
	pos    int
	offset int
	tokens []*Token
}

func (c *Calculator) Parse(s string) (float64,error) {
	replacer := strings.NewReplacer(" ","","\t","","\n","","\r","")
	s = replacer.Replace(s)

	c.input = []byte(s)
	c.pos = 0
	c.offset = 0
	for state := c.root; state != nil; {
		state = state()
	}

	//reorder tokens to postfix
	var stack []*Token
	var output []*Token
	for _,t := range append([]*Token{{
		data: "(",
		typ:  ParenStartTokenType,
	}},append(c.tokens,&Token{
		data: ")",
		typ:  ParenEndTokenType,
	})...) {
		switch t.typ {
		case ErrTokenType:
			return 0,fmt.Errorf(t.data)
		case NumberTokenType:
			output = append(output,t)
		case ParenStartTokenType:
			stack = append(stack,t)
		case ParenEndTokenType:
			for stack[len(stack)-1].typ != ParenStartTokenType {
				output = append(output,stack[len(stack)-1])
				stack = stack[:len(stack)-1]
			}
			stack = stack[:len(stack)-1]
		case DivisionTokenType:
			fallthrough
		case MultiplicationTokenType:
			fallthrough
		case AdditionTokenType:
			fallthrough
		case SubtractionTokenType:
			for t.Priority() <= stack[len(stack)-1].Priority(){
				output = append(output,stack[len(stack)-1])
				stack = stack[:len(stack)-1]
			}
			stack = append(stack,t)
		}
	}

	c.tokens = output
	return c.calculate()

}

func (c *Calculator) calculate() (float64,error) {
	var stack []float64
	for _, t := range c.tokens {
		switch t.typ {
		case NumberTokenType:
			n,err := strconv.ParseFloat(t.data,64)
			if err != nil {
				return 0,fmt.Errorf("not a number '%s'",t.data)
			}
			stack = append(stack,n)
		case AdditionTokenType:
			stack = append(stack[0:len(stack)-2],stack[len(stack)-2]+stack[len(stack)-1])
		case SubtractionTokenType:
			if len(stack) == 1 {
				stack[0] *= -1
				break
			}
			stack = append(stack[0:len(stack)-2],stack[len(stack)-2]-stack[len(stack)-1])
		case MultiplicationTokenType:
			stack = append(stack[0:len(stack)-2],stack[len(stack)-2]*stack[len(stack)-1])
		case DivisionTokenType:
			if stack[len(stack)-1] == 0 {
				stack = append(stack[0:len(stack)-2],0)
				break
			}
			stack = append(stack[0:len(stack)-2],stack[len(stack)-2]/stack[len(stack)-1])
		}
	}
	return stack[0],nil
}

func (c *Calculator) root() stateFn {
	n := c.peek()
	if n == 0 {
		return nil
	}

	if n >= '0' && n <= '9' {
		return c.number
	}

	if n == '+' {
		return c.plus
	}

	if n == '-' {
		return c.minus
	}

	if n == '*' {
		return c.multiplication
	}

	if n == '/' {
		return c.division
	}

	if n == '(' {
		return c.parenStart
	}

	if n == ')' {
		return c.parenEnd
	}

	return c.err("syntax error")
}

func (c *Calculator) number() stateFn {
	for n := c.peek(); n >= '0' && n <= '9' || n == '.'; n = c.next() {
	}
	c.pos--
	c.emit(NumberTokenType)
	return c.root
}

func (c *Calculator) plus() stateFn {
	n := c.next()
	if n != '+' {
		return c.err("expected plus")
	}
	c.emit(AdditionTokenType)

	return c.root
}

func (c *Calculator) minus() stateFn {
	n := c.next()
	if n != '-'{
		return c.err("expected minus")
	}
	c.emit(SubtractionTokenType)

	return c.root
}

func (c *Calculator) multiplication() stateFn {
	n := c.next()
	if n != '*'{
		return c.err("expected multiplication")
	}
	c.emit(MultiplicationTokenType)

	return c.root
}

func (c *Calculator) division() stateFn {
	n := c.next()
	if n != '/'{
		return c.err("expected division")
	}
	c.emit(DivisionTokenType)

	return c.root
}

func (c *Calculator) parenStart() stateFn {
	n := c.next()
	if n != '('{
		return c.err("expected (")
	}
	c.emit(ParenStartTokenType)
	return c.root
}

func (c *Calculator) parenEnd() stateFn {
	n := c.next()
	if n != ')'{
		return c.err("expected )")
	}
	c.emit(ParenEndTokenType)
	return c.root
}

func (c *Calculator) err(msg string) stateFn {
	return func() stateFn {
		c.emitErr(msg)
		return nil
	}
}

func (c *Calculator) peek() byte {
	if c.pos >= len(c.input) {
		return 0
	}
	return byte(c.input[c.pos])
}

func (c *Calculator) next() byte {
	r := c.peek()
	c.pos++
	return r
}

func (c *Calculator) emit(typ TokenType) {
	c.tokens = append(c.tokens, &Token{
		data: string(c.input[c.offset:c.pos]),
		pos:  c.offset,
		typ:  typ,
	})
	c.offset = c.pos
}

func (c *Calculator) emitErr(msg string) {
	c.input = []byte(msg)
	c.pos = len(c.input)
	c.offset = 0
	c.emit(ErrTokenType)
}
