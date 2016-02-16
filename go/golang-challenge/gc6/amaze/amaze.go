package amaze

import (
	"errors"
	"log"
	"math"
	"math/rand"
	"time"

	"github.com/golangchallenge/gc6/mazelib"
)

var bt = false

type Maze struct {
	rooms    [][]*mazelib.Room
	w        int
	h        int
	pos      *Position
	end      *Position
	lastStep *Step
}

func NewMaze(w, h int) *Maze {
	m := &Maze{
		w:     w,
		h:     h,
		rooms: make([][]*mazelib.Room, w),
		pos:   &Position{},
		end:   &Position{},
	}

	for x := 0; x < w; x++ {
		m.rooms[x] = make([]*mazelib.Room, h)
		for y := 0; y < h; y++ {
			m.rooms[x][y] = &mazelib.Room{
				Walls: mazelib.Survey{
					Top:    true,
					Right:  true,
					Bottom: true,
					Left:   true,
				},
			}
		}
	}

	return m
}

func (m *Maze) createJunction(p *Position) ([]*Position, error) {
	m.pos = p
	endPoints := make([]*Position, 0, 4)
	for _, dir := range []Direction{UP, DOWN, LEFT, RIGHT} {
		moves := 0
		if m.canVisitDirection(dir) {
			m.SmashAndMove(dir)
			moves++
		}
		if m.canVisitDirection(dir) {
			m.SmashAndMove(dir)
			moves++
		}

		endPoints = append(endPoints, m.pos.Copy())

		for moves > 0 {
			moves--
			m.Backtrack()
		}
	}

	jEnds := make([]*Position, 0, 4)
	for _, ep := range endPoints {
		m.pos = ep
		if dir := m.calculateNextStep(); dir != "" {
			m.SmashAndMove(dir)
			jEnds = append(jEnds, m.pos.Copy())
			m.Backtrack()
		}

	}

	return jEnds, nil
}

func (m *Maze) GenerateMaze() {
	m.setRandomStartAndEnd()
	_, err := m.LookAround()
	if err != nil {
		log.Println(err)
	}

	junctions := []*Position{m.pos}

	for !m.allRoomsVisited() {
		i := -1
		var junction *Position
		for i, junction = range junctions {
			js, err := m.createJunction(junction)
			if err != nil && err.Error() != "" {
				if err.Error() == "room outside of maze boundaries" {
					continue
				} else if err == ErrReachedEnd {
					continue
				} else if err == ErrCoordOutsideGrid {
					continue
				}
				log.Printf("err: %s", err)
				break
			}
			junctions = append(junctions, js...)
		}
		junctions = junctions[i+1:]

		if len(junctions) == 0 {
			for i := 0; i < m.w; i++ {
				for j := 0; j < m.h; j++ {
					room := m.rooms[i][j]
					if !room.Visited {
						for dir, adjRoom := range m.getAdjacentRooms(i, j) {

							if adjRoom.Visited {
								m.pos = &Position{x: i, y: j}
								m.SmashAndMove(dir)
								room.Visited = true
								break
							}
						}
					}
				}
			}
		}
	}

	m.setRandomStartAndEnd()
}

func (m *Maze) rmWall(dir Direction) error {
	//fmt.Println(MazeString(m))
	//time.Sleep(time.Millisecond * 100)

	fromRoom, err := m.GetRoom(m.pos.XY())
	if err != nil {
		return err
	}
	toRoom, err := m.GetRoom(m.pos.Copy().Move(dir).XY())
	if err != nil {
		return err
	}

	switch dir {
	case UP:
		fromRoom.RmWall(mazelib.N)
		toRoom.RmWall(mazelib.S)
	case DOWN:
		fromRoom.RmWall(mazelib.S)
		toRoom.RmWall(mazelib.N)
	case LEFT:
		fromRoom.RmWall(mazelib.W)
		toRoom.RmWall(mazelib.E)
	case RIGHT:
		fromRoom.RmWall(mazelib.E)
		toRoom.RmWall(mazelib.W)
	}
	return nil
}

func (m *Maze) allRoomsVisited() bool {
	for i := 0; i < m.w; i++ {
		for j := 0; j < m.h; j++ {
			if !m.rooms[i][j].Visited {
				return false
			}
		}
	}
	return true
}

func (m *Maze) getAdjacentRooms(x, y int) map[Direction]*mazelib.Room {
	rooms := make(map[Direction]*mazelib.Room)

	coords := map[Direction]*Position{
		LEFT:  &Position{x: x - 1, y: y},
		RIGHT: &Position{x: x + 1, y: y},
		UP:    &Position{x: x, y: y - 1},
		DOWN:  &Position{x: x, y: y + 1},
	}

	for dir, p := range coords {
		r, err := m.GetRoom(p.XY())
		if err != nil {
			continue
		}
		rooms[dir] = r
	}
	return rooms

}

func (m *Maze) canVisit(x, y int) bool {
	if !m.validCoordinate(x, y) {
		return false
	}

	room, err := m.GetRoom(x, y)

	if err != nil {
		panic(err)
	}

	if room.Visited {
		return false
	}

	return true

}

func (m *Maze) canVisitDirection(d Direction) bool {
	return m.canVisit(m.pos.Copy().Move(d).XY())
}

func (m *Maze) sumSteps(dir Direction) int {
	sum := 0
	s := m.lastStep
	for s != nil {
		if s.direction == dir {
			sum++
		}
		s = s.previous
	}
	return sum
}

func (m *Maze) getPreviousStepAt(i int) *Step {
	lastStep := m.lastStep
	for i > 0 {
		if lastStep == nil {
			break
		}
		lastStep = lastStep.previous
		i--
	}
	return lastStep
}

func (m *Maze) calculateNextStep() Direction {
	directions := make(map[Direction]int)
	oneStep := map[Direction]*Position{
		DOWN:  m.pos.Copy().Down(),
		UP:    m.pos.Copy().Up(),
		LEFT:  m.pos.Copy().Left(),
		RIGHT: m.pos.Copy().Right(),
	}

	for _, dir := range []Direction{DOWN, UP, LEFT, RIGHT} {
		if m.canVisit(oneStep[dir].XY()) {
			if _, exist := directions[dir]; !exist {
				directions[dir] = 0
			}

			if step := m.getPreviousStepAt(0); step != nil && step.direction == dir {
				directions[dir] = 1
				if step := m.getPreviousStepAt(1); step != nil && step.direction == dir {
					directions[dir] = -2
					if step := m.getPreviousStepAt(2); step != nil {
						directions[step.direction.Opposite()] = 4
					}
				}
			}
		}
	}

	maxPoints := math.MinInt64
	var winners []Direction
	for key, val := range directions {
		if maxPoints < val {
			maxPoints = val
			winners = make([]Direction, 0, 4)
		}

		if maxPoints == val {
			winners = append(winners, key)
		}
	}

	winCount := len(winners)
	if winCount > 0 {
		winner := winners[rand.Intn(winCount)]
		return winner
	}

	return ""
}

func (m *Maze) SmashAndMove(dir Direction) (*Step, error) {
	m.rmWall(dir)
	return m.Move(dir)
}

func (m *Maze) setRandomStartAndEnd() {
	rand.Seed(time.Now().UnixNano())

	sx := rand.Intn(m.w)
	sy := rand.Intn(m.h)
	ex := rand.Intn(m.w)
	ey := rand.Intn(m.h)

	if sx == ex && sy == ey {
		m.setRandomStartAndEnd()
	} else {
		m.SetTreasure(sx, sy)
		m.SetStartPoint(ex, ey)
	}
}

func (m *Maze) validCoordinate(x, y int) bool {
	return x >= 0 && y >= 0 && x < m.w && y < m.h
}

func (m *Maze) GetRoom(x, y int) (*mazelib.Room, error) {
	if !m.validCoordinate(x, y) {
		return nil, ErrCoordOutsideGrid
	}

	return m.rooms[x][y], nil
}

func (m *Maze) Width() int {
	return m.w
}

func (m *Maze) Height() int {
	return m.h
}

func (m *Maze) SetStartPoint(x, y int) error {
	room, err := m.GetRoom(x, y)
	if err != nil {
		return err
	}

	if room.Start {
		return ErrTreasureOrStartInvalid
	}

	m.pos.x = x
	m.pos.y = y

	for i := 0; i < m.w; i++ {
		for j := 0; j < m.h; j++ {
			m.rooms[i][j].Start = (x == i && j == y)
		}
	}
	return nil
}

func (m *Maze) SetTreasure(x, y int) error {
	room, err := m.GetRoom(x, y)
	if err != nil {
		return err
	}

	if room.Start {
		return ErrTreasureOrStartInvalid
	}

	m.end.x = x
	m.end.y = y

	for i := 0; i < m.w; i++ {
		for j := 0; j < m.h; j++ {
			m.rooms[i][j].Treasure = (x == i && j == y)
		}
	}
	return nil
}

func (m *Maze) LookAround() (mazelib.Survey, error) {
	return m.Discover(m.pos.XY())
}

func (m *Maze) Discover(x, y int) (mazelib.Survey, error) {
	room, err := m.GetRoom(x, y)
	if err != nil {
		return mazelib.Survey{}, err
	}
	return room.Walls, nil
}

func (m *Maze) Icarus() (x, y int) {
	return m.pos.XY()
}

func (m *Maze) move(d Direction) (mazelib.Survey, error) {
	var err error
	switch d {
	case UP:
		err = m.MoveUp()
	case DOWN:
		err = m.MoveDown()
	case LEFT:
		err = m.MoveLeft()
	case RIGHT:
		err = m.MoveRight()
	default:
		err = errors.New("Invalid direction")
	}

	if err != nil {
		return mazelib.Survey{}, err
	}

	return m.LookAround()
}

func (m *Maze) Move(d Direction) (*Step, error) {
	s, err := m.move(d)
	if err != nil {
		return nil, err
	}

	m.lastStep = &Step{
		direction: d,
		previous:  m.lastStep,
		survey:    s,
	}
	return m.lastStep, err
}

func (m *Maze) Backtrack() (*Step, error) {
	if m.lastStep == nil {
		return nil, ErrNoMoreBacktracks
	}

	d := m.lastStep.direction.Opposite()

	s, err := m.move(d)
	if err != nil {
		return nil, err
	}

	lastStep := m.lastStep
	lastStep.survey = s
	lastStep.direction = lastStep.direction.Opposite()
	m.lastStep = m.lastStep.previous
	return lastStep, nil
}

func (m *Maze) MoveLeft() error {
	room, err := m.GetRoom(m.pos.XY())
	if err != nil {
		return err
	}

	if room.Walls.Left {
		return ErrWallInTheWay
	}

	if !m.validCoordinate(m.pos.Copy().Left().XY()) {
		return ErrCoordOutsideGrid
	}
	m.pos.Left()
	room, err = m.GetRoom(m.pos.XY())
	if err != nil {
		return err
	}
	room.Visited = true
	return nil

}

func (m *Maze) MoveRight() error {
	room, err := m.GetRoom(m.pos.XY())
	if err != nil {
		return err
	}

	if room.Walls.Right {
		return ErrWallInTheWay
	}

	if !m.validCoordinate(m.pos.Copy().Right().XY()) {
		return ErrCoordOutsideGrid
	}
	m.pos.Right()
	room, err = m.GetRoom(m.pos.XY())
	if err != nil {
		return err
	}
	room.Visited = true
	return nil
}

func (m *Maze) MoveUp() error {
	room, err := m.GetRoom(m.pos.XY())
	if err != nil {
		return err
	}

	if room.Walls.Top {
		return ErrWallInTheWay
	}

	if !m.validCoordinate(m.pos.Copy().Up().XY()) {
		return ErrCoordOutsideGrid
	}
	m.pos.Up()
	room, err = m.GetRoom(m.pos.XY())
	if err != nil {
		return err
	}
	room.Visited = true
	return nil
}

func (m *Maze) MoveDown() error {
	room, err := m.GetRoom(m.pos.XY())
	if err != nil {
		return err
	}

	if room.Walls.Bottom {
		return ErrWallInTheWay
	}

	if !m.validCoordinate(m.pos.Copy().Down().XY()) {
		return ErrCoordOutsideGrid
	}
	m.pos.Down()
	room, err = m.GetRoom(m.pos.XY())
	if err != nil {
		return err
	}
	room.Visited = true
	return nil
}
