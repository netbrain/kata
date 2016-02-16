package amaze

import (
	"fmt"
	"testing"

	"github.com/golangchallenge/gc6/mazelib"
)

func TestAmazeImplementsMazeLib(t *testing.T) {
	_ = mazelib.MazeI(&Maze{})
}

func TestCanInstantiateNewMaze(t *testing.T) {
	maze := NewMaze(15, 10)
	fmt.Println(MazeString(maze))
}

func TestCanGenerateValidNewMaze(t *testing.T) {
	maze := NewMaze(15, 10)
	maze.GenerateMaze()
	fmt.Println(MazeString(maze))
}
