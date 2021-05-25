import random
import numpy as np

class Map:

    # Default constructor
    def __init__(self):
        self.width = 0
        self.height = 0
        self.cells = []
    
    # Initialization of map by string.
    def ReadFromString(self, cellStr, width, height, obstacles=None):
        self.width = width
        self.height = height
        self.obstacles = obstacles
        self.cells = [[0 for _ in range(width)] for _ in range(height)]
        cellLines = cellStr.split("\n")
        i = 0
        j = 0
        for l in cellLines:
            if len(l) != 0:
                j = 0
                for c in l:
                    if c == '.':
                        self.cells[i][j] = 0
                    elif c == '#' or c == '@' or c == 'T':
                        self.cells[i][j] = 1
                    else:
                        continue
                    j += 1

                if j != width:
                    raise Exception("Size Error. Map width = ", j, ", but must be", width )
                
                i += 1

        if i != height:
            raise Exception("Size Error. Map height = ", i, ", but must be", height )
    
    # Initialization of map by list of cells.
    def SetGridCells(self, width, height, gridCells):
        self.width = width
        self.height = height
        self.cells = gridCells

    def getNumpy(self):
        return np.array(self.cells)

    # Checks cell is on grid.
    def inBounds(self, i, j):
        return (0 <= j < self.width) and (0 <= i < self.height)
    
    # Checks cell is not obstacle.
    def Traversable(self, i, j, t=0):
        # TODO time dim
        return not self.cells[i][j]

    # Creates a list of neighbour cells as (i,j) tuples.
    def GetNeighbors(self, i, j, t=0):
        # TODO time dim

        neighbors = []

        prev = False

        for x, y in zip([i+1, i, i-1, i], [j, j+1, j, j-1]):
            if self.inBounds(x, y) and self.Traversable(x, y):
                neighbors += [(x, y)]
 
        cond = lambda x: self.inBounds(x[0], x[1]) and self.Traversable(x[0], x[1])
        for x, y in zip([i+1, i+1, i-1, i-1], [j+1, j-1, j+1, j-1]):
            if all(map(cond, [(x, y), (x, j), (i, y)])):
                neighbors += [(x, y)]

        return neighbors


# class Obstacles:
#     def __init__(self, n, w, h):
#         self.w = w
#         self.h = h
#         self.n = n
#         self.obstacles = self.gen_obstacles()

#     def gen_obstacles(self):
#         obstacles = []
#         for i in range(self.n):
#             pass
#         return obstacles

#     def collide(self, i, j, t):
#         for o in self.obstacles:
#             if o.collide(i, j, t):
#                 break
#         else:
#             return False
#         return True


# class SingleObstacle:
#     def __init__(self, w, h, upT=10):
#         self.traj = self.gen_traj(w, h, upT)
#         self.ln = len(self.traj)

#     def gen_traj(self, w, h, upT, s=(None, None)):
#         si, sj = s
#         # moves = [-1, -1, 0, +1, +1]
#         moves = [(0, 0)] + [(0, 1), (0, -1), (1, 0), (-1, 0)]*2
        
#         if si is None or sj is None:
#             si = random.randint(0, h-1)
#             sj = random.randint(0, w-1)

#         ci, cj = si, sj
        
#         traj = [(ci, cj)]
#         print(upT//2)
        
#         for _ in range(upT // 2):
#             # mi = random.choice(moves)
#             # mj = random.choice(moves)
#             mi, mj = random.choice(moves)
#             if 0 <= ci + mi < h:
#                 ci = ci + mi
#             if 0 <= cj + mj < w:
#                 cj = cj + mj
#             traj.append((ci, cj))
        
#         print('T: ', traj)

#         traj += bfs(ci, cj, si, sj, w, h)

#         return traj

#     def get_by_time(self, time):
#         t = time % self.ln
#         return self.traj[t]

#     def get_by_loc(self, i, j):
#         time_series = []
#         for t, (x, y) in enumerate(self.traj):
#             if i == x and j == y:
#                 time_series.append(t)
#         return time_series


# def bfs(i1, j1, i2, j2, w, h):
#     moves = [(0, 1), (0, -1), (1, 0), (-1, 0)]
#     parent = {(i1, j1): (None, None)}

#     q = [(i1, j1)]
#     while True:
#         ci, cj = q.pop(0)
#         if ci < 0 or ci >= h or cj < 0 or cj >= h:
#             continue
        
#         if ci == i2 and cj == j2:
#             break
    
#         for mi, mj in moves:
#             new = (ci + mi, cj + mj)
#             if new in parent:
#                 continue
#             q.append(new)
#             parent[new] = (ci, cj)

#     b = (i2, j2)
#     backtrace = [b]

#     while True:
#         if b == (i1, j1):
#             backtrace.append(b)
#             break
#         b = parent[b]

#     return list(reversed(backtrace))


# if __name__ == '__main__':
#     for _ in range(10):
#         o = SingleObstacle(5, 5, 10)
#         print(o.traj)

