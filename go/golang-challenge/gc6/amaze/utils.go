package amaze

import (
	"errors"
	"fmt"

	"github.com/golangchallenge/gc6/mazelib"
)

const (
	UP    = Direction("up")
	DOWN  = Direction("down")
	LEFT  = Direction("left")
	RIGHT = Direction("right")
)

var (
	ErrCoordOutsideGrid       = errors.New("Coordinate outside grid")
	ErrTreasureOrStartInvalid = errors.New("Starting pos cannot be in the same room as the treasure.")
	ErrNoMoreBacktracks       = errors.New("Can't backtrack anymore.")
	ErrReachedEnd             = errors.New("Reached end cell")
	ErrWallInTheWay           = errors.New("Wall in the way")
)

type Direction string

type Step struct {
	previous  *Step
	direction Direction
	survey    mazelib.Survey
}

func (d Direction) Opposite() Direction {
	switch d {
	case UP:
		return DOWN
	case DOWN:
		return UP
	case LEFT:
		return RIGHT
	case RIGHT:
		return LEFT
	}
	return ""
}

type Position struct {
	x, y int
}

func (m *Position) XY() (int, int) {
	return m.x, m.y
}

func (m *Position) Move(direction Direction) *Position {
	switch direction {
	case UP:
		m.Up()
	case DOWN:
		m.Down()
	case LEFT:
		m.Left()
	case RIGHT:
		m.Right()
	}
	return m
}

func (m Position) Copy() *Position {
	return &m
}

func (m *Position) Down() *Position {
	m.add(0, 1)
	return m
}

func (m *Position) Up() *Position {
	m.add(0, -1)
	return m
}

func (m *Position) Left() *Position {
	m.add(-1, 0)
	return m
}

func (m *Position) Right() *Position {
	m.add(1, 0)
	return m
}

func (m *Position) add(x, y int) {
	m.x += x
	m.y += y
}

func (m *Position) String() string {
	return fmt.Sprintf("%d:%d", m.x, m.y)
}

func MazeString(m mazelib.MazeI) string {
	out := ""
	str := make([][]string, m.Height()*3)
	for i := 0; i < m.Height(); i++ {
		str[i*3] = make([]string, m.Width()*3)
		str[i*3+1] = make([]string, m.Width()*3)
		str[i*3+2] = make([]string, m.Width()*3)
		for j := 0; j < m.Width(); j++ {
			room, _ := m.GetRoom(j, i)
			str[i*3][j*3] = "▛"
			str[i*3][j*3+1] = " "
			str[i*3][j*3+2] = "▜"
			str[i*3+2][j*3] = "▙"
			str[i*3+2][j*3+1] = " "
			str[i*3+2][j*3+2] = "▟"
			str[i*3+1][j*3] = " "
			str[i*3+1][j*3+2] = " "
			str[i*3+1][j*3+1] = " "

			if room.Walls.Top {
				str[i*3][j*3+1] = "▀"
			}

			if room.Walls.Bottom {
				str[i*3+2][j*3+1] = "▄"
			}

			if room.Walls.Left {
				str[i*3+1][j*3] = "▌"
			}

			if room.Walls.Right {
				str[i*3+1][j*3+2] = "▐"
			}

			if room.Visited {
				str[i*3+1][j*3+1] = "·"
			}

			if room.Treasure {
				str[i*3+1][j*3+1] = "⚿"
			} else if room.Start {
				str[i*3+1][j*3+1] = "⚑"
			}

			x, y := m.Icarus()
			if x == j && y == i {
				str[i*3+1][j*3+1] = "☉"
			}

		}
	}

	for x := 0; x < len(str); x++ {
		for y := 0; y < len(str[x]); y++ {
			out += str[x][y]
		}
		out += "\n"
	}

	return out
}
