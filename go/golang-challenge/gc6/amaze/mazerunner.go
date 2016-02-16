package amaze

import (
	"log"
	"math/rand"

	"github.com/golangchallenge/gc6/mazelib"
)

type Survey struct {
	Survey    mazelib.Survey
	Direction Direction
	Err       error
}

func (s *Survey) WallAt(dir Direction) bool {
	switch dir {
	case UP:
		return s.Survey.Top
	case DOWN:
		return s.Survey.Bottom
	case LEFT:
		return s.Survey.Left
	case RIGHT:
		return s.Survey.Right
	}
	return false
}

type MazeRunner struct {
	pos       *Position
	mentalMap map[string]bool
	lastStep  *Step
	m         *Maze
}

func NewMazeRunner() *MazeRunner {
	mr := &MazeRunner{
		pos:       &Position{},
		mentalMap: make(map[string]bool),
		m:         NewMaze(30, 20),
	}

	mr.m.SetStartPoint(15, 10)

	return mr
}

func (m *MazeRunner) AlreadyVisited(dir Direction) bool {
	_, exists := m.mentalMap[m.pos.Copy().Move(dir).String()]
	return exists
}

func (m *MazeRunner) FindTreasure(surveyChan <-chan Survey, directionChan chan<- Direction) {
	for {
		select {
		case s := <-surveyChan:

			m.mentalMap[m.pos.String()] = true

			if s.Err == mazelib.ErrVictory {
				return
			} else if s.Err != nil {
				log.Fatal(s.Err)
			}

			directions := make([]Direction, 0, 4)
			for _, dir := range []Direction{DOWN, UP, LEFT, RIGHT} {
				if !m.AlreadyVisited(dir) && !s.WallAt(dir) {
					directions = append(directions, dir)
				}
			}
			dirCount := len(directions)
			if dirCount == 0 {
				if m.lastStep == nil {
					return
				}

				lastStep := m.lastStep
				m.lastStep = lastStep.previous
				dir := lastStep.direction.Opposite()

				m.pos.Move(dir)
				m.m.SmashAndMove(dir)
				directionChan <- dir

			} else {
				r := rand.Intn(dirCount)
				dir := directions[r]
				if s.Direction != "" {
					m.pos.Move(dir)
					m.lastStep = &Step{
						previous:  m.lastStep,
						direction: dir,
						survey:    s.Survey,
					}
				}
				m.m.SmashAndMove(dir)
				directionChan <- dir
			}
		}
	}
}
